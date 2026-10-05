package com.cpl.ui;

import com.cpl.engine.ThreadRegistry;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.util.List;

/**
 * Live thread monitor panel displaying all active CPL worker threads, lifecycle states, and priorities.
 * Demonstrates:
 * - Unit II: Thread life cycle (NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED)
 * - Unit II: Thread priorities (1 - 10)
 * - Unit IV: Swing Timer, JTable live updates
 */
public class ThreadMonitorPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Thread ID", "Thread Name", "Lifecycle State", "Priority", "Daemon", "Current Activity"}, 0);
    private final JTable table = new JTable(model);

    public ThreadMonitorPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Theme.BG_CARD);
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("CPL Concurrency & Thread Life-Cycle Monitor (Live 2Hz)");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_LIGHT);
        header.add(title, BorderLayout.WEST);

        add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG_CARD);
        table.setBackground(Theme.BG_CARD_LIGHT);
        table.setForeground(Theme.TEXT_LIGHT);
        table.setRowHeight(24);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Unit IV: Swing Timer firing every 500ms
        Timer timer = new Timer(500, e -> refreshSnapshot());
        timer.start();
    }

    private void refreshSnapshot() {
        model.setRowCount(0);
        List<ThreadRegistry.ThreadInfo> snapshot = ThreadRegistry.snapshot();
        for (ThreadRegistry.ThreadInfo t : snapshot) {
            model.addRow(new Object[]{
                t.id,
                t.name,
                t.state.name(),
                t.priority,
                t.isDaemon ? "Yes" : "No",
                t.activity
            });
        }
    }
}
