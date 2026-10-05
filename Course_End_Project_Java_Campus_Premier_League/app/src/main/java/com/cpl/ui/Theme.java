package com.cpl.ui;

import java.awt.Color;
import java.awt.Font;

/**
 * Styling and theme palette for CPL Swing User Interface.
 * Demonstrates:
 * - Unit I: static final constants
 */
public final class Theme {
    private Theme() {}

    public static final Color BG_DARK = new Color(15, 23, 42); // Slate 900
    public static final Color BG_CARD = new Color(30, 41, 59); // Slate 800
    public static final Color BG_CARD_LIGHT = new Color(51, 65, 85); // Slate 700
    public static final Color TEXT_LIGHT = new Color(248, 250, 252);
    public static final Color TEXT_MUTED = new Color(148, 163, 184);

    public static final Color ACCENT_BLUE = new Color(37, 99, 235);
    public static final Color ACCENT_GREEN = new Color(22, 163, 74);
    public static final Color ACCENT_GOLD = new Color(234, 179, 8);
    public static final Color ACCENT_RED = new Color(220, 38, 38);
    public static final Color ACCENT_PURPLE = new Color(147, 51, 234);

    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_MONO = new Font("JetBrains Mono", Font.BOLD, 13);
}
