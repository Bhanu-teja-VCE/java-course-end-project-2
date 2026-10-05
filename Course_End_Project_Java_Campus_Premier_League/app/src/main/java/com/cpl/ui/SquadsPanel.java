package com.cpl.ui;

import com.cpl.model.Player;
import com.cpl.model.Team;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Squads viewer panel displaying team rosters, purses, and role compositions.
 * Demonstrates:
 * - Unit IV: JComboBox, JTable, DefaultTableModel, Layouts
 */
public class SquadsPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JComboBox<String> teamSelector = new JComboBox<String>();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"ID", "Player Name", "Role", "Overseas", "Bat Rating", "Bowl Rating", "Field Rating", "Sold Price (₹)"}, 0);
    private final JTable table = new JTable(tableModel);

    private final JLabel purseLabel = new JLabel("Purse Remaining: -");
    private final JLabel squadCountLabel = new JLabel("Squad Size: -");
    private final JLabel overseasCountLabel = new JLabel("Overseas: -");
    private final List<Team> teams;

    public SquadsPanel(List<Team> teams) {
        this.teams = teams;
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildTopBar(), BorderLayout.NORTH);
        add(buildCenterTable(), BorderLayout.CENTER);

        for (Team t : teams) {
            teamSelector.addItem(t.getName() + " (" + t.getShortName() + ")");
        }

        teamSelector.addActionListener(e -> updateRosterTable());
        updateRosterTable();
    }

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel lbl = new JLabel("Select Franchise Squad:");
        lbl.setFont(Theme.FONT_SUBHEADER);
        lbl.setForeground(Theme.TEXT_LIGHT);
        left.add(lbl);
        left.add(teamSelector);
        bar.add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new GridLayout(1, 3, 16, 0));
        right.setOpaque(false);
        purseLabel.setFont(Theme.FONT_BODY);
        purseLabel.setForeground(Theme.ACCENT_GOLD);
        squadCountLabel.setFont(Theme.FONT_BODY);
        squadCountLabel.setForeground(Theme.TEXT_LIGHT);
        overseasCountLabel.setFont(Theme.FONT_BODY);
        overseasCountLabel.setForeground(Theme.ACCENT_BLUE);

        right.add(purseLabel);
        right.add(squadCountLabel);
        right.add(overseasCountLabel);
        bar.add(right, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildCenterTable() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_CARD);
        table.setBackground(Theme.BG_CARD_LIGHT);
        table.setForeground(Theme.TEXT_LIGHT);
        table.setRowHeight(24);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    public void updateRosterTable() {
        int idx = teamSelector.getSelectedIndex();
        if (idx < 0 || idx >= teams.size()) return;
        Team team = teams.get(idx);

        purseLabel.setText(String.format("Purse Remaining: ₹%,.2f", team.getCurrentPurse()));
        squadCountLabel.setText(String.format("Squad: %d / %d", team.getSquadSize(), team.getMaxSquad()));
        overseasCountLabel.setText(String.format("Overseas: %d / %d", team.getOverseasCount(), team.getMaxOverseas()));

        tableModel.setRowCount(0);
        for (Player p : team.getSquad()) {
            tableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getRole().getDisplayName(),
                p.isOverseas() ? "Yes" : "No",
                p.getBattingRating(),
                p.getBowlingRating(),
                p.getFieldingRating(),
                p.getSoldPrice() != null ? String.format("₹%,.0f", p.getSoldPrice()) : "-"
            });
        }
    }
}
