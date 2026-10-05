# DESIGN.md: Campus Premier League (CPL) Simulator

**Course:** Object Oriented Programming through Java (VCE-R25)  
**Department:** Computer Science & Engineering  
---

## 1. System Architecture & Package Structure

The system is organized into decoupled packages, ensuring strict separation of concerns, high testability, and clear visibility of university syllabus concepts.

```
com.cpl
 ├── model/           # Core entities (Player hierarchy, Team, Match, Innings, BallEvent)
 ├── auction/         # Auction engine, Auctioneer thread, BiddingBot threads, Strategies
 ├── match/           # Ball-by-ball engine, Pitch, OverPhase, Commentary, Scorecard
 ├── tournament/      # Tournament fixture scheduler, PointsTable, Playoff engine
 ├── db/              # JDBC connection manager, DAOs, Stored Procedure callers, Schema installer
 ├── io/              # CSV player parser, Tournament file serializer, Scorecard exporter
 ├── exception/       # Custom checked and unchecked exception hierarchy
 ├── engine/          # Simulation clock, event bus, config constants, thread registry
 └── ui/              # Swing GUI panels, Custom Canvas (Wagon wheel, Run-rate graph), Dialogs
```

---

## 2. Detailed Class Hierarchy & Syllabus Mapping

### Package `com.cpl.model`
* `Player` (Abstract class): Base class with `id`, `name`, `age`, `basePrice`, `isOverseas`, `battingRating`, `bowlingRating`, `fieldingRating`, `soldPrice`, `ownerTeamId`.
* `Batter` (`extends Player`): Implements specialized boundary probabilities and strike-rotation logic.
* `Bowler` (`extends Player`): Implements specialized economy and wicket-taking probabilities.
* `AllRounder` (`extends Player`): Balanced attributes combining batting and bowling capabilities.
* `WicketKeeper` (`extends Player`): Specialized keeper catches and stumping bonuses.
* `Team`: Represents campus franchise (`id`, `name`, `shortName`, `color`, `initialPurse`, `currentPurse`, `squad`, `minSquad`, `maxSquad`, `maxOverseas`).
* `Role` (Enum): `BATTER`, `BOWLER`, `ALL_ROUNDER`, `WICKET_KEEPER`.
* `BallEvent`: Immutable record of a ball (`ballNumber`, `overNumber`, `batterId`, `bowlerId`, `runsScored`, `extraType`, `isWicket`, `dismissalType`, `commentary`).
* `Innings`: Container for 20 overs, fall of wickets, over-by-over summaries, extras, and total score.
* `Match`: Container for two innings, match status, winner, margin, player of the match.

### Package `com.cpl.auction`
* `Auctioneer` (`extends Thread`, `MAX_PRIORITY = 10`): Drives the auction lots sequentially, calls countdown ticks ("going once, going twice, sold"), notifies bots, accepts bids atomically, and resolves sales.
* `BiddingBot` (`implements Runnable`, `priority = 5`): Autonomous thread representing an AI team bidder evaluating lots.
* `BiddingStrategy` (Interface): Strategy pattern for bot decisions (`evaluateBid(Team team, Player lot, double currentBid)`).
  * `AggressiveStrategy` (`implements BiddingStrategy`): Bids aggressively for star players (high ratings) up to 35% of purse.
  * `BudgetMindedStrategy` (`implements BiddingStrategy`): Value hunter, only bids up to 1.5x base price.
  * `NeedsBasedStrategy` (`implements BiddingStrategy`): Analyzes team composition deficits (e.g. lack of bowlers) and targets gaps.
  * `RandomStrategy` (`implements BiddingStrategy`): Stochastic campus wildcard bidder.
* `AuctionLot`: Encapsulates current lot state, current highest bid, highest bidder team, countdown timer.

### Package `com.cpl.match`
* `MatchEngine` (`implements Runnable`): Simulates a T20 match ball-by-ball or in rapid mode.
* `PitchCondition` (Enum): `BATTER_PARADISE`, `BOWLING_GREEN`, `SPINNER_DUSTBOWL`, `BALANCED`.
* `OverPhase` (Enum): `POWERPLAY` (Overs 1-6), `MIDDLE` (Overs 7-15), `DEATH` (Overs 16-20).
* `CommentaryEngine`: Generates realistic cricket commentary using template substitutions and `StringBuffer`.
* `WagonWheel`: 2D polar projection of shot directions based on delivery line and shot outcome.

### Package `com.cpl.tournament`
* `Tournament`: Manages league lifecycle (Auction phase &rarr; 56 League Matches &rarr; Top 4 Playoffs &rarr; Final).
* `PointsTable`: Computes points (Win=2, Tie=1, Loss=0) and Net Run Rate (NRR) with `TreeMap` and `TreeSet`.
* `TournamentScheduler`: Round-robin combinatorial match generation.

### Package `com.cpl.db`
* `Database`: Thread-safe JDBC connection manager utilizing MySQL Connector/J Type 4 driver.
* `DbConfig`: Loads database host, port, user, and password from `db.properties`.
* `SchemaInstaller`: Automated installer parsing `cpl_schema.sql` and executing DDL/DML.
* `TournamentDao`: CRUD for teams, players, matches, and ball events with `PreparedStatement` and transactions.
* `StoredProcedureCaller`: Invokes `sp_points_table`, `sp_top_scorers`, and `sp_close_auction_lot` via `CallableStatement`.
* `InMemoryTournamentRepository`: Fallback offline in-memory repository ensuring zero crash when MySQL is absent.

### Package `com.cpl.io`
* `PlayerCsvParser`: Imports 120 fictional players using `FileReader` and `StringTokenizer`.
* `TournamentSerializer`: Saves and loads full tournament state using `ObjectOutputStream` / `ObjectInputStream` (`.cpl` files).
* `ScorecardExporter`: Generates clean ASCII scorecards to text files using `FileWriter`.
* `CsvStatsExporter`: Exports Orange/Purple cap statistics to CSV.

### Package `com.cpl.exception`
* `CplException` (Base checked exception)
  * `AuctionException`
    * `BudgetExceededException` (Bid exceeds available purse)
    * `SquadFullException` (Team already has max 18 players)
    * `OverseasLimitException` (Exceeds max 4 overseas players)
    * `AuctionClosedException` (Bid submitted after "SOLD" call)
  * `InvalidPlayerException`
  * `DatabaseUnavailableException`
* `IllegalMatchStateException` (Unchecked runtime exception)

---

## 3. Concurrency Model & Lock Hierarchy

### Active Threads:
1. **Swing EDT (Event Dispatch Thread):** Renders GUI components, canvas graphs, and handles user input.
2. **Auctioneer Thread (`MAX_PRIORITY = 10`):** Manages lot countdowns, auction timer (`wait(timeout)` / `notifyAll()`), and sales.
3. **BiddingBot Threads (`Priority = 5`):** 8 bot threads competing for active lots.
4. **Parallel Match Worker Threads (`Priority = 5`):** Simulates league matches in parallel batches.
5. **Database Async Logger Thread (`MIN_PRIORITY = 1`):** Background batch persistence of ball events and match summaries.

### Lock Ordering & Synchronization Rules:
To avoid deadlocks, resources must strictly follow this lock acquisition hierarchy:
1. `Tournament.class` (System level lock)
2. `AuctionLot` monitor (Guards current bid, timer, and current lot)
3. `Team` monitor (Guards purse balance and squad roster)
4. `Player` monitor (Guards owner assignment)

*Deadlock Prevention Guarantee:* A `Team` lock is never held while attempting to acquire an `AuctionLot` lock. The `Auctioneer` always synchronizes on `AuctionLot` first, validates bids, and then atomically acquires the bidding `Team` lock to commit funds.

---

## 4. Database Schema & Stored Procedures

Four core relational tables:
1. `cpl_team` (id, name, short_name, purse, overseas_count)
2. `cpl_player` (id, name, role, base_price, sold_price, team_id, is_overseas, batting_rating, bowling_rating)
3. `cpl_match` (id, match_number, stage, team1_id, team2_id, winner_id, team1_score, team2_score, mom_player_id)
4. `cpl_ball_event` (id, match_id, innings_num, over_num, ball_num, batter_id, bowler_id, runs, extras, is_wicket)

Three Stored Procedures:
1. `sp_close_auction_lot(in_player_id, in_team_id, in_sold_price)`: Atomically transfers player and deducts team purse inside an ACID transaction.
2. `sp_points_table()`: Computes matches played, won, lost, points, and Net Run Rate `(RunsScored / OversFaced) - (RunsConceded / OversBowled)`.
3. `sp_top_scorers(in_limit)`: Retrieves Orange Cap leaders by aggregating runs and calculating batting strike rates.

---

## 5. Test & Validation Plan

* **Unit Tests (25+ tests):**
  * Player validation and role calculations.
  * CSV parsing with `StringTokenizer`.
  * Atomic auction bid rules (purse bounds, squad full, overseas quota).
  * Multithreaded auction simulation with 8 concurrent bots.
  * Scorecard invariant verification: `batter_runs + extras == team_total`.
  * Net Run Rate mathematical correctness.
  * Stored procedure calls and offline repository fallback.
  * Object serialization integrity test.
* **Invariant Checker (`SafetyChecker`):** Run after every match and auction lot to assert zero corrupted states.
* **Headless Experiment (`Experiment`):** Runs 200 simulated T20 matches to record score distributions and verify statistical plausibility.
