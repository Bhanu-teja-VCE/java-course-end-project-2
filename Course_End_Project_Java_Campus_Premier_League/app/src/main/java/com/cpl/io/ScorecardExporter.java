package com.cpl.io;

import com.cpl.model.Innings;
import com.cpl.model.Match;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates an ASCII scorecard and writes it to a file.
 * Demonstrates:
 * - Unit III: FileWriter and character stream output
 */
public class ScorecardExporter {

    public static void exportScorecard(Match match, File targetFile) throws IOException {
        // Unit III: FileWriter character stream
        try (FileWriter writer = new FileWriter(targetFile)) {
            writer.write("========================================================================\n");
            writer.write(String.format(" CAMPUS PREMIER LEAGUE - MATCH #%d (%s)\n", match.getMatchNumber(), match.getStage().name()));
            writer.write(String.format(" %s vs %s\n", match.getTeam1Name(), match.getTeam2Name()));
            writer.write(String.format(" RESULT: %s\n", match.getResultSummary()));
            writer.write(String.format(" PLAYER OF THE MATCH: %s\n", match.getMomPlayerName()));
            writer.write("========================================================================\n\n");

            if (match.getInnings1() != null) {
                writeInningsScorecard(writer, "1st Innings: " + match.getTeam1Name(), match.getInnings1());
            }

            if (match.getInnings2() != null) {
                writeInningsScorecard(writer, "2nd Innings: " + match.getTeam2Name(), match.getInnings2());
            }
            writer.flush();
        }
    }

    private static void writeInningsScorecard(FileWriter w, String title, Innings inn) throws IOException {
        w.write("------------------------------------------------------------------------\n");
        w.write(" " + title + String.format(" - %d/%d (%.1f Overs)\n", inn.getTotalRuns(), inn.getWickets(), inn.getOversCompleted()));
        w.write("------------------------------------------------------------------------\n");
        w.write(String.format("%-24s | %-12s | %4s | %5s | %3s | %3s | %6s\n",
                "Batter", "Dismissal", "Runs", "Balls", "4s", "6s", "SR"));
        w.write("------------------------------------------------------------------------\n");

        for (Innings.BatterStats b : inn.getBatterStatsMap().values()) {
            w.write(String.format("%-24s | %-12s | %4d | %5d | %3d | %3d | %6.1f\n",
                    b.name, b.dismissalType, b.runs, b.ballsFaced, b.fours, b.sixes, b.getStrikeRate()));
        }

        w.write(String.format("Extras: %d | Total: %d/%d\n\n", inn.getExtras(), inn.getTotalRuns(), inn.getWickets()));
        w.write("BOWLING ANALYSIS:\n");
        w.write(String.format("%-24s | %5s | %5s | %5s | %6s\n", "Bowler", "Overs", "Runs", "Wkts", "Econ"));
        w.write("------------------------------------------------------------------------\n");
        for (Innings.BowlerStats bowl : inn.getBowlerStatsMap().values()) {
            w.write(String.format("%-24s | %5.1f | %5d | %5d | %6.2f\n",
                    bowl.name, bowl.getOvers(), bowl.runsConceded, bowl.wicketsTaken, bowl.getEconomyRate()));
        }
        w.write("\nFALL OF WICKETS: " + String.join(", ", inn.getFallOfWickets()) + "\n\n");
    }
}
