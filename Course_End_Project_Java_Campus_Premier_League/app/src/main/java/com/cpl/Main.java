package com.cpl;

import com.cpl.ui.MainFrame;
import com.cpl.ui.Theme;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main application launcher for Campus Premier League (CPL).
 * Starts the Swing GUI on the Event Dispatch Thread (EDT).
 * Demonstrates:
 * - Unit IV: Event Dispatch Thread, Look and Feel
 */
public final class Main {

    private Main() {}

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    UIManager.put("nimbusBase", Theme.BG_DARK);
                    UIManager.put("nimbusSelectionBackground", Theme.ACCENT_BLUE);
                    UIManager.put("control", Theme.BG_CARD);
                    break;
                }
            }
        } catch (Exception ignored) {
            // keep standard look and feel if Nimbus unavailable
        }

        // Unit IV: Swing EDT
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
