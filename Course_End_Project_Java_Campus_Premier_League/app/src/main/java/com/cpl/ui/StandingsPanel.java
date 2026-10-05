package com.cpl.ui;

import com.cpl.db.TournamentRepository;
import com.cpl.io.CsvStatsExporter;
import com.cpl.tournament.PointsTableEntry;
import com.cpl.tournament.Tournament;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Standings and statistical leaderboards panel (Points Table with NRR, Orange Cap, Purple Cap).
 * Demonstrates:
 * - Unit IV: JTable, custom cell styling, file dialogs, event handling
 */
public class StandingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel pointsModel = new DefaultTableModel(
            new Object[]{"Rank", "Franchise Team", "Played", "Won", "Lost", "Tied", "Points", "Net Run Rate"}, 0);
    private final JTable pointsTable = new JTable(pointsModel);

    private final DefaultTableModel orangeCapModel = new DefaultTableModel(
            new Object[]{"Rank", "Player", "Franchise", "Runs", "Balls", "Strike Rate"}, 0);
    private final JTable orangeCapTable = new JTable(orangeCapModel);

    private final DefaultTableModel purpleCapModel = new DefaultTableModel(
            new Object[]{"Rank", "Player", "Franchise", "Wickets", "Balls", "Economy"}, 0);
    private final JTable purpleCapTable = new JTable(purpleCapModel);

    private final Tournament tournament;
    private final TournamentRepository repository;

    public StandingsPanel(Tournament tournament, TournamentRepository repository) {
        this.tournament = tournament;
        this.repository = repository;

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildHeaderBar(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);

        refreshTables();
    }

    private JPanel buildHeaderBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("CPL League Standings & Official Leaderboards");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_LIGHT);
        bar.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        JButton refreshBtn = new JButton("Refresh Standings");
        refreshBtn.setBackground(Theme.ACCENT_BLUE);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.addActionListener(e -> refreshTables());

        JButton exportCsvBtn = new JButton("Export Points Table (CSV)");
        exportCsvBtn.setBackground(Theme.ACCENT_GREEN);
        exportCsvBtn.setForeground(Color.WHITE);
        exportCsvBtn.addActionListener(e -> exportToCsv());

        controls.add(refreshBtn);
        controls.add(exportCsvBtn);
        bar.add(controls, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 10, 10));
        panel.setOpaque(false);

        // Top: Points Table
        JPanel ptPanel = new JPanel(new BorderLayout());
        ptPanel.setBackground(Theme.BG_CARD);
        ptPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BG_CARD_LIGHT), "Official Points Table (Net Run Rate)",
                0, 0, Theme.FONT_SUBHEADER, Theme.TEXT_LIGHT));

        pointsTable.setBackground(Theme.BG_CARD_LIGHT);
        pointsTable.setForeground(Theme.TEXT_LIGHT);
        pointsTable.setRowHeight(24);
        ptPanel.add(new JScrollPane(pointsTable), BorderLayout.CENTER);
        panel.add(ptPanel);

        // Bottom: Orange Cap and Purple Cap
        JPanel bottomSplit = new JPanel(new GridLayout(1, 2, 10, 10));
        bottomSplit.setOpaque(false);

        JPanel ocPanel = new JPanel(new BorderLayout());
        ocPanel.setBackground(Theme.BG_CARD);
        ocPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_GOLD), "Orange Cap Leaderboard (Most Runs)",
                0, 0, Theme.FONT_SUBHEADER, Theme.ACCENT_GOLD));
        orangeCapTable.setBackground(Theme.BG_CARD_LIGHT);
        orangeCapTable.setForeground(Theme.TEXT_LIGHT);
        orangeCapTable.setRowHeight(22);
        ocPanel.add(new JScrollPane(orangeCapTable), BorderLayout.CENTER);

        JPanel pcPanel = new JPanel(new BorderLayout());
        pcPanel.setBackground(Theme.BG_CARD);
        pcPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_PURPLE), "Purple Cap Leaderboard (Most Wickets)",
                0, 0, Theme.FONT_SUBHEADER, Theme.ACCENT_PURPLE));
        purpleCapTable.setBackground(Theme.BG_CARD_LIGHT);
        purpleCapTable.setForeground(Theme.TEXT_LIGHT);
        purpleCapTable.setRowHeight(22);
        pcPanel.add(new JScrollPane(purpleCapTable), BorderLayout.CENTER);

        bottomSplit.add(ocPanel);
        bottomSplit.add(pcPanel);
        panel.add(bottomSplit);

        return panel;
    }

    public void refreshTables() {
        // 1. Points Table
        pointsModel.setRowCount(0);
        List<PointsTableEntry> rankings = tournament.getPointsTable().getRankings();
        int rank = 1;
        for (PointsTableEntry e : rankings) {
            pointsModel.addRow(new Object[]{
                rank++,
                e.getTeamName() + " (" + e.getShortName() + ")",
                e.getMatchesPlayed(),
                e.getWins(),
                e.getLosses(),
                e.getTies(),
                e.getPoints(),
                String.format("%+.3f", e.getNetRunRate())
            });
        }

        // 2. Orange Cap
        orangeCapModel.setRowCount(0);
        try {
            List<Map<String, Object>> oc = repository.fetchOrangeCap(5);
            int r = 1;
            for (Map<String, Object> row : oc) {
                orangeCapModel.addRow(new Object[]{
                    r++, row.get("name"), row.get("team"), row.get("runs"), row.get("balls"),
                    String.format("%.2f", row.get("strike_rate"))
                });
            }
        } catch (Exception ex) {
            // fallback
        }

        // 3. Purple Cap
        purpleCapModel.setRowCount(0);
        try {
            List<Map<String, Object>> pc = repository.fetchPurpleCap(5);
            int r = 1;
            for (Map<String, Object> row : pc) {
                purpleCapModel.addRow(new Object[]{
                    r++, row.get("name"), row.get("team"), row.get("wickets"), row.get("balls"),
                    String.format("%.2f", row.get("economy"))
                });
            }
        } catch (Exception ex) {
            // fallback
        }
    }

    private void exportToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("cpl_standings.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                CsvStatsExporter.exportPointsTable(tournament.getPointsTable().getRankings(), chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Exported successfully to " + chooser.getSelectedFile().getName(),
                        "Export Successful", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Export failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
