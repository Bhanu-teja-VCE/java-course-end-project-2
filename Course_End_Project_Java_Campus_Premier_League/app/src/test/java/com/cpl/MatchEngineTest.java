package com.cpl;

import com.cpl.match.MatchEngine;
import com.cpl.match.PitchCondition;
import com.cpl.model.AllRounder;
import com.cpl.model.Batter;
import com.cpl.model.Bowler;
import com.cpl.model.Match;
import com.cpl.model.MatchStage;
import com.cpl.model.Team;
import com.cpl.model.WicketKeeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MatchEngineTest {

    private Team team1;
    private Team team2;

    @BeforeEach
    void setUp() throws Exception {
        team1 = new Team(1, "Knights", "KNT", "#111", 10000000.0);
        team2 = new Team(2, "Warriors", "WAR", "#222", 10000000.0);

        // Populate minimum squads (11 players each)
        for (int i = 1; i <= 5; i++) {
            team1.addPlayer(new Batter(i, "K-Bat " + i, 200000.0, false, 80 + i, 30, 80), 200000.0);
            team2.addPlayer(new Batter(i + 20, "W-Bat " + i, 200000.0, false, 80 + i, 30, 80), 200000.0);
        }
        for (int i = 6; i <= 9; i++) {
            team1.addPlayer(new Bowler(i, "K-Bowl " + i, 200000.0, false, 30, 80 + i, 80), 200000.0);
            team2.addPlayer(new Bowler(i + 20, "W-Bowl " + i, 200000.0, false, 30, 80 + i, 80), 200000.0);
        }
        team1.addPlayer(new AllRounder(10, "K-AR", 200000.0, false, 80, 80, 80), 200000.0);
        team2.addPlayer(new AllRounder(30, "W-AR", 200000.0, false, 80, 80, 80), 200000.0);
        team1.addPlayer(new WicketKeeper(11, "K-WK", 200000.0, false, 80, 20, 85), 200000.0);
        team2.addPlayer(new WicketKeeper(31, "W-WK", 200000.0, false, 80, 20, 85), 200000.0);
    }

    @Test
    void testMatchExecutionAndStatus() {
        Match match = new Match(1, 1, MatchStage.LEAGUE, team1.getId(), team1.getName(), team2.getId(), team2.getName());
        assertEquals(Match.Status.SCHEDULED, match.getStatus());

        MatchEngine engine = new MatchEngine(match, team1, team2, PitchCondition.BALANCED, 42L);
        engine.run();

        assertEquals(Match.Status.COMPLETED, match.getStatus());
        assertNotNull(match.getInnings1());
        assertNotNull(match.getInnings2());
        assertTrue(match.getInnings1().getTotalRuns() > 0);
        assertTrue(match.getInnings2().getTotalRuns() > 0);
        assertNotNull(match.getResultSummary());
        assertNotNull(match.getMomPlayerName());
    }

    @Test
    void testMatchReproducibilityWithSameSeed() {
        Match m1 = new Match(1, 1, MatchStage.LEAGUE, team1.getId(), team1.getName(), team2.getId(), team2.getName());
        MatchEngine e1 = new MatchEngine(m1, team1, team2, PitchCondition.BALANCED, 9999L);
        e1.run();

        Match m2 = new Match(2, 2, MatchStage.LEAGUE, team1.getId(), team1.getName(), team2.getId(), team2.getName());
        MatchEngine e2 = new MatchEngine(m2, team1, team2, PitchCondition.BALANCED, 9999L);
        e2.run();

        // Exact scores and wickets must match with identical seed
        assertEquals(m1.getInnings1().getTotalRuns(), m2.getInnings1().getTotalRuns());
        assertEquals(m1.getInnings1().getWickets(), m2.getInnings1().getWickets());
        assertEquals(m1.getInnings2().getTotalRuns(), m2.getInnings2().getTotalRuns());
        assertEquals(m1.getInnings2().getWickets(), m2.getInnings2().getWickets());
        assertEquals(m1.getWinnerId(), m2.getWinnerId());
    }

    @Test
    void testSecondInningsStopsWhenTargetReached() {
        Match match = new Match(1, 1, MatchStage.LEAGUE, team1.getId(), team1.getName(), team2.getId(), team2.getName());
        MatchEngine engine = new MatchEngine(match, team1, team2, PitchCondition.BALANCED, 12345L);
        engine.run();

        if (match.getWinnerId() != null && match.getWinnerId() == team2.getId()) {
            // Team 2 chased successfully: total runs should be >= target
            assertTrue(match.getInnings2().getTotalRuns() > match.getInnings1().getTotalRuns());
        }
    }
}
