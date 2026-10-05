package com.cpl;

import com.cpl.engine.SafetyChecker;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class ScorecardInvariantTest {

    private Team t1;
    private Team t2;

    @BeforeEach
    void setUp() throws Exception {
        t1 = new Team(1, "Titans", "TIT", "#000", 10000000.0);
        t2 = new Team(2, "Royals", "ROY", "#111", 10000000.0);

        for (int i = 1; i <= 5; i++) {
            t1.addPlayer(new Batter(i, "T-Bat" + i, 200000.0, false, 80, 40, 80), 200000.0);
            t2.addPlayer(new Batter(i + 20, "R-Bat" + i, 200000.0, false, 80, 40, 80), 200000.0);
        }
        for (int i = 6; i <= 9; i++) {
            t1.addPlayer(new Bowler(i, "T-Bowl" + i, 200000.0, false, 30, 85, 80), 200000.0);
            t2.addPlayer(new Bowler(i + 20, "R-Bowl" + i, 200000.0, false, 30, 85, 80), 200000.0);
        }
        t1.addPlayer(new AllRounder(10, "T-AR", 200000.0, false, 80, 80, 80), 200000.0);
        t2.addPlayer(new AllRounder(30, "R-AR", 200000.0, false, 80, 80, 80), 200000.0);
        t1.addPlayer(new WicketKeeper(11, "T-WK", 200000.0, false, 80, 20, 85), 200000.0);
        t2.addPlayer(new WicketKeeper(31, "R-WK", 200000.0, false, 80, 20, 85), 200000.0);
    }

    @Test
    void testScorecardInvariantsAcrossVariedPitches() {
        PitchCondition[] pitches = PitchCondition.values();
        for (int i = 0; i < pitches.length; i++) {
            Match match = new Match(i + 1, i + 1, MatchStage.LEAGUE, t1.getId(), t1.getName(), t2.getId(), t2.getName());
            MatchEngine engine = new MatchEngine(match, t1, t2, pitches[i], 1000L + i * 50);
            engine.run();

            assertDoesNotThrow(() -> {
                SafetyChecker.verifyMatchInvariants(match);
            }, "Scorecard invariants must hold strictly for pitch condition: " + pitches[i]);
        }
    }
}
