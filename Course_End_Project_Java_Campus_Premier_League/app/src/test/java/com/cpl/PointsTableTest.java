package com.cpl;

import com.cpl.model.Team;
import com.cpl.tournament.PointsTable;
import com.cpl.tournament.PointsTableEntry;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PointsTableTest {

    @Test
    void testPointsAndNetRunRateCalculation() {
        PointsTableEntry entry = new PointsTableEntry(1, "Test Team", "TT", "#000");
        assertEquals(0, entry.getPoints());
        assertEquals(0.0, entry.getNetRunRate(), 0.001);

        // Match 1: Team scored 180 in 20.0 ov (9.00 rpo), Conceded 140 in 20.0 ov (7.00 rpo) -> Won
        // NRR = +2.000
        entry.recordMatchResult(180, 20.0, 140, 20.0, true, false);
        assertEquals(2, entry.getPoints());
        assertEquals(1, entry.getWins());
        assertEquals(2.000, entry.getNetRunRate(), 0.001);

        // Match 2: Team scored 120 in 20.0 ov (6.00 rpo), Conceded 160 in 20.0 ov (8.00 rpo) -> Lost
        // Total scored = 300 in 40.0 ov (7.50), Total conceded = 300 in 40.0 ov (7.50) -> NRR = 0.000
        entry.recordMatchResult(120, 20.0, 160, 20.0, false, false);
        assertEquals(2, entry.getPoints());
        assertEquals(1, entry.getWins());
        assertEquals(1, entry.getLosses());
        assertEquals(0.000, entry.getNetRunRate(), 0.001);
    }

    @Test
    void testRankingsSortingOrder() {
        List<Team> teams = new ArrayList<Team>();
        Team t1 = new Team(1, "Team One", "T1", "#111", 10000.0);
        Team t2 = new Team(2, "Team Two", "T2", "#222", 10000.0);
        Team t3 = new Team(3, "Team Three", "T3", "#333", 10000.0);
        teams.add(t1);
        teams.add(t2);
        teams.add(t3);

        PointsTable table = new PointsTable(teams);
        PointsTableEntry e1 = table.getEntry(1);
        PointsTableEntry e2 = table.getEntry(2);
        PointsTableEntry e3 = table.getEntry(3);

        // T1 has 4 points
        e1.recordMatchResult(160, 20.0, 140, 20.0, true, false);
        e1.recordMatchResult(170, 20.0, 150, 20.0, true, false);

        // T2 has 2 points (+1.0 NRR)
        e2.recordMatchResult(160, 20.0, 140, 20.0, true, false);

        // T3 has 2 points (+0.2 NRR)
        e3.recordMatchResult(150, 20.0, 146, 20.0, true, false);

        List<PointsTableEntry> rankings = table.getRankings();
        assertEquals(3, rankings.size());
        assertEquals("Team One", rankings.get(0).getTeamName());
        assertEquals("Team Two", rankings.get(1).getTeamName());
        assertEquals("Team Three", rankings.get(2).getTeamName());
    }
}
