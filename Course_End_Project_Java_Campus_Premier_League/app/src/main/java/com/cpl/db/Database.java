package com.cpl.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC connection utility class.
 * Demonstrates:
 * - Unit V: JDBC Architecture, Type 4 Driver (Connector/J), DriverManager, Connection
 */
public class Database {
    private static DbConfig activeConfig = new DbConfig();

    static {
        try {
            // Unit V: Explicit loading of MySQL Type 4 JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Warning: MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    public static void setConfig(DbConfig config) {
        activeConfig = config;
    }

    public static DbConfig getConfig() {
        return activeConfig;
    }

    // Unit I: Method Overloading (getConnection with default active config)
    public static Connection getConnection() throws SQLException {
        return getConnection(activeConfig);
    }

    // Unit I: Method Overloading (getConnection with explicit DbConfig)
    public static Connection getConnection(DbConfig config) throws SQLException {
        // Unit V: DriverManager establishing Connection
        return DriverManager.getConnection(config.getJdbcUrl(), config.getUser(), config.getPassword());
    }

    public static boolean testConnection(DbConfig config) {
        try (Connection conn = getConnection(config)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
