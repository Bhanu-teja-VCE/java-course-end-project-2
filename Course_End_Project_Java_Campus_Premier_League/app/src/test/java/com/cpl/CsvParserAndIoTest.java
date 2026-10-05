package com.cpl;

import com.cpl.io.PlayerCsvParser;
import com.cpl.io.ScorecardExporter;
import com.cpl.io.TournamentSerializer;
import com.cpl.match.MatchEngine;
import com.cpl.match.PitchCondition;
import com.cpl.model.Batter;
import com.cpl.model.Match;
import com.cpl.model.MatchStage;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.Tournament;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CsvParserAndIoTest {

    @Test
    void testPlayerCsvParsing() throws IOException {
        List<Player> players = PlayerCsvParser.parseFromResource("/data/players.csv");
        assertNotNull(players);
        assertEquals(120, players.size(), "Should parse exactly 120 players from CSV");

        // Verify roles distribution
        int batters = 0, bowlers = 0, allrounders = 0, keepers = 0, overseas = 0;
        for (Player p : players) {
            switch (p.getRole()) {
                case BATTER: batters++; break;
                case BOWLER: bowlers++; break;
                case ALL_ROUNDER: allrounders++; break;
                case WICKET_KEEPER: keepers++; break;
            }
            if (p.isOverseas()) overseas++;
        }

        assertTrue(batters >= 25);
        assertTrue(bowlers >= 25);
        assertTrue(allrounders >= 15);
        assertTrue(keepers >= 10);
        assertTrue(overseas >= 20);
    }

    @Test
    void testTournamentSerialization(@TempDir Path tempDir) throws Exception {
        List<Team> teams = new ArrayList<Team>();
        teams.add(new Team(1, "Serial Team A", "STA", "#111", 10000000.0));
        teams.add(new Team(2, "Serial Team B", "STB", "#222", 10000000.0));

        List<Player> players = new ArrayList<Player>();
        players.add(new Batter(1, "Serial Batter", 200000.0, false, 80, 40, 75));

        Tournament tournament = new Tournament("Serial Cup", 2026, teams, players);

        File targetFile = tempDir.resolve("tournament_test.cpl").toFile();
        TournamentSerializer.saveToFile(tournament, targetFile);
        assertTrue(targetFile.exists());
        assertTrue(targetFile.length() > 0);

        Tournament loaded = TournamentSerializer.loadFromFile(targetFile);
        assertNotNull(loaded);
        assertEquals("Serial Cup", loaded.getLeagueName());
        assertEquals(2, loaded.getTeams().size());
        assertEquals(1, loaded.getAllPlayers().size());
    }

    @Test
    void testScorecardExporting(@TempDir Path tempDir) throws Exception {
        Team t1 = new Team(1, "Team 1", "T1", "#111", 5000000.0);
        Team t2 = new Team(2, "Team 2", "T2", "#222", 5000000.0);
        t1.addPlayer(new Batter(1, "Player 1", 200000.0, false, 80, 40, 80), 200000.0);
        t1.addPlayer(new Batter(2, "Player 2", 200000.0, false, 80, 40, 80), 200000.0);
        t2.addPlayer(new Batter(3, "Player 3", 200000.0, false, 80, 40, 80), 200000.0);
        t2.addPlayer(new Batter(4, "Player 4", 200000.0, false, 80, 40, 80), 200000.0);

        Match match = new Match(1, 1, MatchStage.LEAGUE, t1.getId(), t1.getName(), t2.getId(), t2.getName());
        MatchEngine engine = new MatchEngine(match, t1, t2, PitchCondition.BALANCED, 100L);
        engine.run();

        File scFile = tempDir.resolve("scorecard.txt").toFile();
        ScorecardExporter.exportScorecard(match, scFile);
        assertTrue(scFile.exists());
        assertTrue(scFile.length() > 100);
    }
}
