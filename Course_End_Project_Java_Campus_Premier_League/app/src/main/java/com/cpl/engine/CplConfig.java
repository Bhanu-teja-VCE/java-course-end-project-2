package com.cpl.engine;

/**
 * Global configuration constants for Campus Premier League.
 * Demonstrates:
 * - Unit I: static, final constants
 */
public final class CplConfig {
    private CplConfig() {} // Unit I: Private constructor to prevent instantiation

    public static final String LEAGUE_NAME = "Campus Premier League";
    public static final int SEASON_YEAR = 2026;

    public static final double DEFAULT_INITIAL_PURSE = 10000000.00; // 1 Crore (100 Lakhs)
    public static final int MIN_SQUAD_SIZE = 11;
    public static final int MAX_SQUAD_SIZE = 18;
    public static final int MAX_OVERSEAS_PLAYERS = 4;

    public static final int TOTAL_LEAGUE_MATCHES = 56;
    public static final int OVERS_PER_INNINGS = 20;
    public static final int BALLS_PER_OVER = 6;
    public static final int MAX_BALLS_PER_INNINGS = 120;
    public static final int MAX_WICKETS = 10;

    public static final int AUCTION_COUNTDOWN_SECONDS = 3;
    public static final double MIN_BID_INCREMENT = 50000.00; // 50k INR
}
