package com.cpl.ui;

import javax.swing.JComponent;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Custom 2D graphics component rendering the over-by-over worm run-rate graph.
 * Demonstrates:
 * - Unit IV: Swing JComponent, Graphics2D, Custom coordinate mapping
 */
public class RunRateCanvas extends JComponent {
    private static final long serialVersionUID = 1L;

    private int[] inn1OverRuns = new int[20];
    private int[] inn2OverRuns = new int[20];
    private int inn1OversCount = 0;
    private int inn2OversCount = 0;

    public RunRateCanvas() {
        setPreferredSize(new Dimension(320, 280));
        setMinimumSize(new Dimension(200, 200));
        setBackground(Theme.BG_CARD);
    }

    public synchronized void updateData(int[] inn1, int count1, int[] inn2, int count2) {
        if (inn1 != null) {
            this.inn1OverRuns = inn1.clone();
            this.inn1OversCount = count1;
        }
        if (inn2 != null) {
            this.inn2OverRuns = inn2.clone();
            this.inn2OversCount = count2;
        }
        repaint();
    }

    public synchronized void clearData() {
        inn1OverRuns = new int[20];
        inn2OverRuns = new int[20];
        inn1OversCount = 0;
        inn2OversCount = 0;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        g2.setColor(Theme.BG_CARD);
        g2.fillRect(0, 0, w, h);

        int padLeft = 40;
        int padBottom = 30;
        int padTop = 30;
        int padRight = 20;

        int plotW = w - padLeft - padRight;
        int plotH = h - padTop - padBottom;

        // Draw axes
        g2.setColor(Theme.BG_CARD_LIGHT);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRect(padLeft, padTop, plotW, plotH);

        // Draw grid lines (every 50 runs, every 5 overs)
        g2.setColor(new Color(255, 255, 255, 30));
        int maxRuns = 220;
        for (int r = 50; r <= maxRuns; r += 50) {
            int y = padTop + plotH - (int) ((r / (double) maxRuns) * plotH);
            g2.drawLine(padLeft, y, padLeft + plotW, y);
            g2.setColor(Theme.TEXT_MUTED);
            g2.setFont(Theme.FONT_BODY);
            g2.drawString(String.valueOf(r), 10, y + 5);
            g2.setColor(new Color(255, 255, 255, 30));
        }

        for (int ov = 5; ov <= 20; ov += 5) {
            int x = padLeft + (int) ((ov / 20.0) * plotW);
            g2.drawLine(x, padTop, x, padTop + plotH);
            g2.setColor(Theme.TEXT_MUTED);
            g2.drawString(ov + " ov", x - 12, h - 10);
            g2.setColor(new Color(255, 255, 255, 30));
        }

        // Plot Innings 1 (Blue line)
        drawWormLine(g2, inn1OverRuns, inn1OversCount, maxRuns, padLeft, padTop, plotW, plotH, Theme.ACCENT_BLUE, "Inn 1");

        // Plot Innings 2 (Gold line)
        drawWormLine(g2, inn2OverRuns, inn2OversCount, maxRuns, padLeft, padTop, plotW, plotH, Theme.ACCENT_GOLD, "Inn 2");

        // Title
        g2.setColor(Theme.TEXT_LIGHT);
        g2.setFont(Theme.FONT_BODY);
        g2.drawString("Comparative Run-Rate Worm", padLeft, 20);
    }

    private void drawWormLine(Graphics2D g2, int[] overRuns, int oversCount, int maxRuns,
                              int padLeft, int padTop, int plotW, int plotH, Color color, String label) {
        if (oversCount <= 0) return;

        g2.setColor(color);
        g2.setStroke(new BasicStroke(2.2f));

        int prevX = padLeft;
        int prevY = padTop + plotH;
        int cumRuns = 0;

        for (int i = 0; i < Math.min(oversCount, 20); i++) {
            cumRuns += overRuns[i];
            int x = padLeft + (int) (((i + 1) / 20.0) * plotW);
            int y = padTop + plotH - (int) ((cumRuns / (double) maxRuns) * plotH);
            g2.drawLine(prevX, prevY, x, y);
            g2.fillOval(x - 3, y - 3, 6, 6);
            prevX = x;
            prevY = y;
        }
    }
}
