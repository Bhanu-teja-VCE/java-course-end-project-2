package com.cpl.io;

import com.cpl.tournament.PointsTableEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Exports league standings and statistical leaderboards to CSV format.
 * Demonstrates:
 * - Unit III: FileWriter and CSV serialization
 */
public class CsvStatsExporter {

    public static void exportPointsTable(List<PointsTableEntry> standings, File targetFile) throws IOException {
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("Rank,Team,ShortName,Played,Won,Lost,Tied,Points,NetRunRate\n");
            int rank = 1;
            for (PointsTableEntry e : standings) {
                writer.write(String.format("%d,%s,%s,%d,%d,%d,%d,%d,%.3f\n",
                        rank++, e.getTeamName(), e.getShortName(),
                        e.getMatchesPlayed(), e.getWins(), e.getLosses(), e.getTies(),
                        e.getPoints(), e.getNetRunRate()));
            }
            writer.flush();
        }
    }
}
