package com.cpl.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Executes the packaged cpl_schema.sql script to initialize tables and procedures.
 * Demonstrates:
 * - Unit V: JDBC Statement and DDL execution
 */
public class SchemaInstaller {

    public static void installSchema(DbConfig config) throws SQLException, IOException {
        // Connect to server without database first to ensure CREATE DATABASE succeeds
        String serverUrl = String.format("jdbc:mysql://%s:%d/?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                config.getHost(), config.getPort());

        try (Connection conn = DriverManager.getConnection(serverUrl, config.getUser(), config.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + config.getDatabase());
        }

        // Now connect to the database and run script
        try (Connection conn = Database.getConnection(config)) {
            executeSqlScript(conn, "/sql/cpl_schema.sql");
        }
    }

    private static void executeSqlScript(Connection conn, String resourcePath) throws SQLException, IOException {
        InputStream is = SchemaInstaller.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Schema script not found: " + resourcePath);
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            String delimiter = ";";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("--")) continue;

                if (line.toUpperCase().startsWith("DELIMITER")) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        delimiter = parts[1].trim();
                    }
                    continue;
                }

                sb.append(line).append("\n");

                if (line.endsWith(delimiter)) {
                    String sql = sb.toString();
                    sql = sql.substring(0, sql.length() - delimiter.length() - 1).trim();
                    if (!sql.isEmpty()) {
                        try (Statement stmt = conn.createStatement()) {
                            stmt.execute(sql);
                        }
                    }
                    sb.setLength(0);
                }
            }
        }
    }
}
