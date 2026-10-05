package com.cpl.ui;

import com.cpl.model.Match;
import com.cpl.tournament.Tournament;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Fixtures schedule viewer panel displaying all scheduled and completed league/playoff matches.
 * Demonstrates:
 * - Unit IV: JTable, JScrollPane, JButton action filters
 */
public class FixturesPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Match #", "Stage", "Team 1", "Team 2", "Status", "Result", "Player of Match"}, 0);
    private final JTable table = new JTable(model);
    private final Tournament tournament;

    public FixturesPanel(Tournament tournament) {
        this.tournament = tournament;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildFilterBar(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTable(null);
    }

    private JPanel buildFilterBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.setBackground(Theme.BG_CARD);

        JLabel lbl = new JLabel("Filter Fixtures: ");
        lbl.setFont(Theme.FONT_SUBHEADER);
        lbl.setForeground(Theme.TEXT_LIGHT);
        bar.add(lbl);

        JButton btnAll = new JButton("All Fixtures");
        JButton btnCompleted = new JButton("Completed");
        JButton btnScheduled = new JButton("Scheduled");

        btnAll.addActionListener(e -> refreshTable(null));
        btnCompleted.addActionListener(e -> refreshTable("COMPLETED"));
        btnScheduled.addActionListener(e -> refreshTable("SCHEDULED"));

        bar.add(btnAll);
        bar.add(btnCompleted);
        bar.add(btnScheduled);
        return bar;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_CARD);
        table.setBackground(Theme.BG_CARD_LIGHT);
        table.setForeground(Theme.TEXT_LIGHT);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void refreshTable(String filterStatus) {
        model.setRowCount(0);
        List<Match> matches = tournament.getFixtures();
        for (Match m : matches) {
            if (filterStatus != null && !filterStatus.equalsIgnoreCase(m.getStatus().name())) {
                continue;
            }
            model.addRow(new Object[]{
                m.getMatchNumber(),
                m.getStage().name(),
                m.getTeam1Name(),
                m.getTeam2Name(),
                m.getStatus().name(),
                m.getResultSummary(),
                m.getMomPlayerName() != null ? m.getMomPlayerName() : "-"
            });
        }
    }
}
