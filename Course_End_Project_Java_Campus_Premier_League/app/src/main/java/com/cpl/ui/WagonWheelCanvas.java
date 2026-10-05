package com.cpl.ui;

import com.cpl.model.BallEvent;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * Custom 2D graphics component rendering a cricket wagon wheel.
 * Demonstrates:
 * - Unit IV: Swing JComponent, Graphics2D, Custom painting, geometry
 */
public class WagonWheelCanvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private final List<BallEvent> shots = new ArrayList<BallEvent>();

    public WagonWheelCanvas() {
        setPreferredSize(new Dimension(280, 280));
        setMinimumSize(new Dimension(200, 200));
        setBackground(new Color(22, 101, 52)); // Lush cricket outfield green
    }

    public synchronized void addShot(BallEvent shot) {
        if (shot.getRunsScored() > 0) {
            shots.add(shot);
            repaint();
        }
    }

    public synchronized void clearShots() {
        shots.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int cx = w / 2;
        int cy = h / 2;
        int radius = Math.min(cx, cy) - 15;

        // Draw outfield circle
        g2.setColor(new Color(20, 83, 45));
        g2.fillOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Draw boundary rope
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2.0f));
        g2.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);

        // Draw 30-yard inner circle
        int innerR = (int) (radius * 0.45);
        g2.setColor(new Color(255, 255, 255, 100));
        g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0));
        g2.drawOval(cx - innerR, cy - innerR, innerR * 2, innerR * 2);

        // Draw pitch rectangle
        g2.setColor(new Color(217, 119, 6)); // Clay pitch
        g2.fillRect(cx - 5, cy - 14, 10, 28);

        // Draw shot vectors
        synchronized (this) {
            for (BallEvent s : shots) {
                double angleRad = Math.toRadians(s.getShotAngleDegrees());
                double distFactor = Math.min(1.0, s.getShotDistanceMeters() / 85.0);
                int shotR = (int) (radius * distFactor);

                int targetX = cx + (int) (shotR * Math.cos(angleRad));
                int targetY = cy + (int) (shotR * Math.sin(angleRad));

                if (s.getRunsScored() == 6) {
                    g2.setColor(Theme.ACCENT_GOLD);
                    g2.setStroke(new BasicStroke(2.5f));
                } else if (s.getRunsScored() == 4) {
                    g2.setColor(Theme.ACCENT_BLUE);
                    g2.setStroke(new BasicStroke(2.0f));
                } else {
                    g2.setColor(new Color(240, 253, 244));
                    g2.setStroke(new BasicStroke(1.2f));
                }

                g2.drawLine(cx, cy, targetX, targetY);
                g2.fillOval(targetX - 2, targetY - 2, 4, 4);
            }
        }

        // Title overlay
        g2.setColor(Color.WHITE);
        g2.setFont(Theme.FONT_BODY);
        g2.drawString("Wagon Wheel", 10, 20);
    }
}
