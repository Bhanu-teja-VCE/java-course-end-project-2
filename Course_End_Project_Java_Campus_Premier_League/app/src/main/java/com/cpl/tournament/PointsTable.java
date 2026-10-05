package com.cpl.tournament;

import com.cpl.model.Match;
import com.cpl.model.Team;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Tournament Points Table with rankings and Net Run Rate computation.
 * Demonstrates:
 * - Unit III: Collections (TreeMap by team ID, TreeSet with custom Comparator)
 */
public class PointsTable implements Serializable {
    private static final long serialVersionUID = 1L;

    // Unit III: Collections (TreeMap sorted by key)
    private final Map<Integer, PointsTableEntry> entriesByTeamId;

    public PointsTable(List<Team> teams) {
        this.entriesByTeamId = new TreeMap<Integer, PointsTableEntry>();
        for (Team t : teams) {
            entriesByTeamId.put(t.getId(), new PointsTableEntry(t.getId(), t.getName(), t.getShortName(), t.getColorHex()));
        }
    }

    public synchronized void updateFromMatch(Match match) {
        if (match.getInnings1() == null || match.getInnings2() == null) {
            return;
        }

        int t1Id = match.getTeam1Id();
        int t2Id = match.getTeam2Id();

        PointsTableEntry e1 = entriesByTeamId.get(t1Id);
        PointsTableEntry e2 = entriesByTeamId.get(t2Id);
        if (e1 == null || e2 == null) return;

        int r1 = match.getInnings1().getTotalRuns();
        double o1 = match.getInnings1().getDecimalOvers();

        int r2 = match.getInnings2().getTotalRuns();
        double o2 = match.getInnings2().getDecimalOvers();

        boolean t1Won = (match.getWinnerId() != null && match.getWinnerId() == t1Id);
        boolean t2Won = (match.getWinnerId() != null && match.getWinnerId() == t2Id);
        boolean tied = (match.getWinnerId() == null);

        e1.recordMatchResult(r1, o1, r2, o2, t1Won, tied);
        e2.recordMatchResult(r2, o2, r1, o1, t2Won, tied);
    }

    // Unit III: Collections (TreeSet with custom Comparator for rankings)
    public synchronized List<PointsTableEntry> getRankings() {
        TreeSet<PointsTableEntry> sorted = new TreeSet<PointsTableEntry>(new Comparator<PointsTableEntry>() {
            @Override
            public int compare(PointsTableEntry o1, PointsTableEntry o2) {
                // Primary: Points descending
                if (o1.getPoints() != o2.getPoints()) {
                    return Integer.compare(o2.getPoints(), o1.getPoints());
                }
                // Secondary: NRR descending
                int nrrComp = Double.compare(o2.getNetRunRate(), o1.getNetRunRate());
                if (nrrComp != 0) return nrrComp;
                // Tertiary: Team name alphabetical
                return o1.getTeamName().compareTo(o2.getTeamName());
            }
        });

        sorted.addAll(entriesByTeamId.values());
        return Collections.unmodifiableList(new ArrayList<PointsTableEntry>(sorted));
    }

    public synchronized PointsTableEntry getEntry(int teamId) {
        return entriesByTeamId.get(teamId);
    }
}
