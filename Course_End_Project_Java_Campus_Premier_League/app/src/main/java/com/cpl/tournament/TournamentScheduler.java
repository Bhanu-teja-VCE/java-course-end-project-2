package com.cpl.tournament;

import com.cpl.model.Match;
import com.cpl.model.MatchStage;
import com.cpl.model.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates balanced round-robin fixtures for the franchise teams.
 * Demonstrates:
 * - Unit I: Algorithms & Collections
 */
public class TournamentScheduler {

    public static List<Match> generateRoundRobinFixtures(List<Team> teams) {
        List<Match> fixtures = new ArrayList<Match>();
        int n = teams.size();
        int matchCounter = 1;

        // Double round robin: Home and Away (56 matches for 8 teams)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    Team home = teams.get(i);
                    Team away = teams.get(j);
                    fixtures.add(new Match(matchCounter, matchCounter, MatchStage.LEAGUE,
                            home.getId(), home.getName(), away.getId(), away.getName()));
                    matchCounter++;
                }
            }
        }
        return fixtures;
    }
}
