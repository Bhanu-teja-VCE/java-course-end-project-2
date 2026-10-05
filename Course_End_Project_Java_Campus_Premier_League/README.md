# 🏏 Campus Premier League (CPL) — T20 Cricket Simulator & Auction Engine

**Course:** Object Oriented Programming through Java (VCE-R25, B.Tech CSE)  
**Institution:** Vardhaman College of Engineering, Hyderabad  
**Department:** Computer Science & Engineering  
**Interactive Visual Architecture Guide:** 🌐 [Open `project-explained.html`](project-explained.html) &bull; 📰 [Read Engineering Blog (`cpl-blog.html`)](cpl-blog.html)

---

## 📖 1. What is Campus Premier League (CPL)?

**Campus Premier League (CPL)** is a comprehensive Java desktop simulation suite that replicates the entire ecosystem of an IPL-style campus cricket tournament:
1. **Live Multi-threaded Player Auction:** An autonomous auctioneer thread drives lots with live timer countdowns ("Going once... Going twice... Sold!"), while 4 competing AI franchise bots bid dynamically based on budget constraints, role deficits, and player ratings. Human users can also place live manual bids.
2. **Stochastic Ball-by-Ball Match Engine:** Simulates realistic T20 deliveries governed by batter vs. bowler duel mechanics, pitch dynamics (Green Seaming, Batter-Friendly Flat track, Turning Dust Bowl, Balanced), over phase urgency (Powerplay, Middle, Death), and required run-rate pressure.
3. **Interactive 2D Graphics Canvases:** Custom AWT `Graphics2D` components painting 360-degree radial Wagon Wheels (shot trajectories by angle and distance) and dynamic comparative Run-Rate Worm graphs.
4. **Tournament League & Net Run Rate Engine:** Complete round-robin schedule (56 fixtures), official ICC/IPL Net Run Rate (NRR) mathematical calculation, sorted standings, Orange Cap (top batter) and Purple Cap (top bowler) awards, and top-4 playoff brackets.
5. **Dual-Persistence Architecture:** Runs seamlessly in **both online and offline modes**. When connected to MySQL 8.0, it executes stored procedures (`CallableStatement`), ACID transactions, and batch inserts. If MySQL is offline, it automatically falls back to an in-memory repository with Java Object Serialization (`.cpl` snapshots).

---

## 🚀 2. Step-by-Step Guide: How to Open & Run the Simulator

### ⚡ Method 1: Instant Launch (Windows Double-Click — Recommended)
1. Navigate to the project directory:
   `C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League`
2. **Double-click `run_cpl.bat`**.
3. The simulator window opens immediately at 1280x820 resolution in dark stadium mode!

---

### 💻 Method 2: Launch via PowerShell or Command Prompt
Open PowerShell or CMD and run:
```bat
cd "C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League"
.\run_cpl.bat
```
Or directly run the compiled shaded JAR:
```bat
cd "C:\Users\bhanu\Desktop\java cep\shreyan\Course_End_Project_Java_Campus_Premier_League\app"
java -jar target/CampusPremierLeague.jar
```

---

### 🗄️ Method 3: Running with MySQL (Optional for Full Database Features)
The project runs 100% offline out-of-the-box. To enable MySQL persistence:
1. Start MySQL 8.0 on your machine (default port 3306).
2. Create or verify `db.properties` inside `app/` (or use the built-in GUI settings):
   ```properties
   db.url=jdbc:mysql://localhost:3306/cpl_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
   db.user=root
   db.password=root123
   ```
3. Inside the app, open **Database > Test Connection** to verify connectivity and automatically install the database schema and stored procedures.

---

## 🎮 3. Walkthrough: Using the Simulator Screens

Once the application is running, you can explore its 6 core functional tabs:

### 1️⃣ Tab 1: Live Mega Auction Hall (Alt + A)
- **Start Auction:** Click `Start Auction` to launch the background `Auctioneer` coordinator thread and 4 autonomous `BiddingBot` threads.
- **Watch the Bidding War:** Observe bots competing for 120 college cricket athletes in real-time with visual countdown timers.
- **Place Human Bids:** Select your favorite franchise, click `Bid +₹50,000` or `Bid +₹1,00,000` before the countdown hits 0!
- **Fast Forward:** Click `Fast-Forward Auction` to instantly resolve remaining auction lots in background worker threads.

### 2️⃣ Tab 2: Franchise Squads & Purses (Alt + S)
- Select any of the 8 campus franchises from the dropdown (e.g. *Charminar Champions*, *Golconda Gladiators*, *Cyberabad Cobras*).
- Inspect the full roster, remaining purse balance, role distribution (Batters, Bowlers, All-rounders, Wicket-Keepers), and overseas player limits.

### 3️⃣ Tab 3: Fixtures & Schedule (Alt + F)
- View the complete 56-match round-robin tournament schedule.
- Filter matches by team or status (`Upcoming`, `In Progress`, `Completed`).
- Click `Simulate Next Match` or `Simulate All Season Matches` to run full tournaments in seconds.

### 4️⃣ Tab 4: Ball-by-Ball Match Simulator & 2D Graphics (Alt + M)
- **Live Match Controls:** Click `Start Match`, `Bowl Next Ball`, or `Fast-Forward Over`.
- **Radial Wagon Wheel (AWT 2D):** Visualizes actual shot vectors (angles $\theta$ and distances $r$) on a cricket ground oval canvas.
- **Run-Rate Worm Canvas:** Plots 1st innings vs 2nd innings score trajectories over 20 overs.
- **Dynamic Commentary:** Live play-by-play commentary synthesized in real time using mutable string buffers.

### 5️⃣ Tab 5: Points Table & Caps (Alt + T)
- **Standings:** Shows Matches Played, Won, Lost, Tied, Points, and dynamically computed **Net Run Rate (NRR)**.
- **Cap Winners:** Live display of the **Orange Cap** (Highest run-scorer) and **Purple Cap** (Highest wicket-taker).

### 6️⃣ Tab 6: Concurrency & Thread Monitor (Alt + R)
- Live real-time inspection table displaying every active Java thread (`Auctioneer`, `BiddingBot-1..4`, `SafetyChecker-Daemon`, `EDT`).
- Shows thread priority, current state (`RUNNABLE`, `TIMED_WAITING`, `WAITING`), and execution thread group.

---

## 🖼️ Application Screenshots

| 1. Live Mega Auction Hall | 2. Franchise Squads & Purses |
| :---: | :---: |
| ![Auction Hall](screenshots/01-auction-hall.png) | ![Squads](screenshots/02-squads.png) |
| *Real-time player bidding with timer countdown* | *Squad rosters, role counts, and purse tracking* |

| 3. Fixtures Schedule | 4. Ball-by-Ball Match Simulator |
| :---: | :---: |
| ![Fixtures](screenshots/03-fixtures.png) | ![Live Match](screenshots/04-live-match.png) |
| *56 round-robin fixtures and playoffs* | *Live scoreboard, wagon wheel & run-rate worm* |

| 5. Official Points Table & Caps | 6. Concurrency & Thread Monitor |
| :---: | :---: |
| ![Standings](screenshots/05-standings.png) | ![Thread Monitor](screenshots/06-thread-monitor.png) |
| *Net Run Rate rankings, Orange & Purple caps* | *Live thread lifecycle and priority tracking* |

---

## 🔬 4. Headless Experiment Results (200 Simulated Matches)

Verified using the headless benchmarking suite ([`Experiment.java`](app/src/test/java/com/cpl/tools/Experiment.java)) across 200 matches (400 innings, 45,600+ simulated deliveries):

| Metric | Measured Real Value | Syllabus Plausibility Criteria | Verification Status |
| :--- | :---: | :---: | :---: |
| **1st Innings Average Score** | **152.50 runs** | 140.0 – 180.0 runs | **PASSED (100% Plausible)** |
| **Score Standard Deviation** | **28.5 runs** | Typical T20 spread (18 – 30 runs) | **PASSED** |
| **Median 1st Innings Score** | **154.0 runs** | Centered bell curve | **PASSED** |
| **Score Minimum / Maximum** | **70 / 261 runs** | Realistic match extremes | **PASSED** |
| **1st Innings Average Wickets** | **6.61 wickets** | 5.0 – 8.0 wickets | **PASSED (100% Plausible)** |
| **2nd Innings Average Score** | **144.50 runs** | Chasing target distribution | **PASSED** |
| **Tied Matches Rate** | **0.0% (Resolved via Super Over)** | < 2.0% | **PASSED** |
| **Simulation Throughput** | **970.87 matches / second** | Sub-3 minute full season | **PASSED (< 1 second)** |
| **Safety Invariant Violations** | **0 across all 200 matches** | Zero math/score flaws | **PASSED (0 violations)** |

---

## 📚 5. Complete Java Syllabus Coverage (Units I – V)

| Unit | Syllabus Topic | Specific Implementation in CPL Code |
| :---: | :--- | :--- |
| **Unit I** | **OOP Principles, Encapsulation** | [`Player.java`](app/src/main/java/com/cpl/model/Player.java), [`Team.java`](app/src/main/java/com/cpl/model/Team.java), [`Match.java`](app/src/main/java/com/cpl/model/Match.java) with private fields and getters/setters. |
| | **Constructors & Overloading** | Overloaded constructors in `Player()`, `Batter()`, `Team()`, `Database.getConnection()`. |
| | **`this`, `static`, Arrays** | Static sequence `Player.idSequence`, static constants in `CplConfig`. Arrays: `int[] overRuns` and `int[] overWickets` in `Innings.java`. |
| | **Inheritance & `super`** | `Player` &rarr; `Batter`, `Bowler`, `AllRounder`, `WicketKeeper`. Explicit calls to `super(...)`. |
| | **Overriding & Dynamic Method Dispatch** | Overridden `calculateImpactScore()` across player subclasses; `evaluateBid()` across `BiddingStrategy` subclasses. |
| | **Abstract Classes & `final`** | `abstract class Player`, `final class CplConfig`, `final class Theme`. |
| | **Interfaces** | `Rateable`, `BiddingStrategy`, `TournamentRepository`, `Auctioneer.AuctionListener`, `MatchEngine.MatchListener`. |
| | **Packages & Access Control** | Structured packages: `model`, `auction`, `match`, `tournament`, `db`, `io`, `exception`, `engine`, `ui`. Protected methods and package-private members. |
| **Unit II** | **Exception Handling** | Custom checked hierarchy: `CplException` &rarr; `AuctionException` &rarr; `BudgetExceededException`, `SquadFullException`, `OverseasLimitException`, `AuctionClosedException`, `DatabaseUnavailableException`. Unchecked `IllegalMatchStateException`. |
| | **Multithreading & Life Cycle** | `Auctioneer extends Thread`, `BiddingBot implements Runnable`, `MatchEngine implements Runnable`. Visualized live in Thread Monitor (`NEW`, `RUNNABLE`, `TIMED_WAITING`, `WAITING`, `TERMINATED`). |
| | **Thread Priorities** | `Auctioneer` runs at `Thread.MAX_PRIORITY` (10); `BiddingBot` at 5; database logger at `MIN_PRIORITY` (1). |
| | **Synchronization & Critical Sections** | `synchronized` methods on `AuctionLot`, `Team.canBid()`, `Team.addPlayer()`, `PointsTable`. |
| | **Inter-thread Communication** | `wait(timeout)` and `notifyAll()` in `AuctionLot` (timer reset on new bid) and `MatchEngine` pause gate. |
| | **String & StringBuffer** | Dynamic ball-by-ball commentary synthesized using `StringBuffer` in `CommentaryEngine.java`. |
| **Unit III** | **Collections Framework** | `LinkedList` for auction lot queue; `ArrayList` for rosters and deliveries; `HashSet` for unsold pool; `HashMap` for player statistics maps; `TreeMap` for points table entries; `TreeSet` with custom `Comparator` for NRR rankings. |
| | **Arrays & StringTokenizer** | Tokenizing CSV lines with `StringTokenizer` in `PlayerCsvParser.java`; sorting arrays via `Arrays.sort()`. |
| | **File Streams & Serialization** | `FileReader` and `FileWriter` in `PlayerCsvParser`, `ScorecardExporter`, `CsvStatsExporter`. Java Object Serialization (`implements Serializable`) via `ObjectOutputStream` / `ObjectInputStream` in `TournamentSerializer.java` (`.cpl` files). |
| **Unit IV** | **Swing GUI & Layout Managers** | `JFrame`, `JTabbedPane`, `JPanel`, `JTable`, `JComboBox`, `JButton`, `JProgressBar`, `JTextArea`, `JScrollPane`, `JDialog`. Layouts: `BorderLayout`, `GridLayout`, `FlowLayout`, `BoxLayout`. |
| | **Event Delegation Model** | `ActionListener`, `ChangeListener`, keyboard mnemonics (Alt+A, Alt+S, Alt+F, Alt+M, Alt+T, Alt+R). |
| | **Custom 2D Graphics (AWT)** | Custom `Graphics2D` rendering in `WagonWheelCanvas.java` (polar coordinate shot vectors) and `RunRateCanvas.java` (comparative worm run-rate graph). |
| **Unit V** | **JDBC Architecture & Type 4 Driver** | Pure Java MySQL Connector/J driver managed via `DriverManager` in `Database.java`. |
| | **Statement & PreparedStatement** | Parameterized queries with `?` in `MySqlTournamentRepository.java` to prevent SQL injection. |
| | **CallableStatement & Stored Procedures** | Calling `sp_close_auction_lot`, `sp_points_table`, `sp_top_scorers`, `sp_top_wicket_takers`. |
| | **Transactions & Batching** | `conn.setAutoCommit(false)`, `conn.commit()`, `conn.rollback()` in auction sales; `ps.addBatch()` and `ps.executeBatch()` for ball telemetry. |
| | **ResultSet & ResultSetMetaData** | Dynamic column inspection in `MySqlTournamentRepository.java`. |

---

## 🎯 6. Viva Voce Preparation Guide for Faculty Evaluation

1. **Why is the Auctioneer thread given `MAX_PRIORITY` (10)?**  
   *Answer:* The auctioneer thread manages the countdown clock. If bot threads run at the same priority, thread contention could delay the clock tick, causing countdown stuttering. Giving the auctioneer `MAX_PRIORITY` guarantees timely countdown intervals and fair arbitration.
2. **How does the auction timer reset when a bid arrives?**  
   *Answer:* The auctioneer calls `lot.wait(timeout)`. When any bot or user calls `lot.submitBid()`, it updates `countdownSeconds = 3` and calls `notifyAll()`. This immediately wakes up the waiting auctioneer to register the new bid and restart the 3-second countdown.
3. **How is Net Run Rate calculated mathematically?**  
   *Answer:* \( \text{NRR} = \left(\frac{\text{Total Runs Scored}}{\text{Total Overs Faced}}\right) - \left(\frac{\text{Total Runs Conceded}}{\text{Total Overs Bowled}}\right) \). Overs with balls (e.g. 19.3) are converted to exact decimal fractions (\(19 + \frac{3}{6} = 19.5\)). If a team is bowled out before 20 overs, they are penalized as having faced the full 20.0 overs.
4. **How do you guarantee that a player is never sold twice?**  
   *Answer:* `AuctionLot` is a synchronized monitor. Once a lot completes, its state transitions to `SOLD`, the player is removed from the auction queue and added to the winning team's roster. `SafetyChecker.verifyAuctionInvariants()` verifies uniqueness across all squads with a `HashSet`.
5. **How does the system work without MySQL?**  
   *Answer:* We implemented the Repository Pattern (`TournamentRepository`). On startup, `Database.isAvailable()` runs a test ping with a 2-second timeout. If MySQL is unreachable, the system automatically instantiates `InMemoryTournamentRepository`, storing teams, rosters, and computing standings in-memory using `TreeMap` and `TreeSet`, with zero crashes.
