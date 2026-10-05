package com.cpl.db;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 * Encapsulates MySQL database connection credentials.
 * Demonstrates:
 * - Unit III: FileReader and FileWriter with Properties
 */
public class DbConfig {
    private String host;
    private int port;
    private String database;
    private String user;
    private String password;

    public DbConfig() {
        this("localhost", 3306, "campus_premier_league", "root", "root123");
    }

    public DbConfig(String host, int port, String database, String user, String password) {
        this.host = host;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public static DbConfig loadFromFile(File file) {
        DbConfig config = new DbConfig();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Properties props = new Properties();
                props.load(reader);
                config.host = props.getProperty("db.host", "localhost");
                config.port = Integer.parseInt(props.getProperty("db.port", "3306"));
                config.database = props.getProperty("db.name", "campus_premier_league");
                config.user = props.getProperty("db.user", "root");
                config.password = props.getProperty("db.password", "root123");
            } catch (Exception e) {
                // fall back to default
            }
        }
        return config;
    }

    public void saveToFile(File file) throws IOException {
        Properties props = new Properties();
        props.setProperty("db.host", host);
        props.setProperty("db.port", String.valueOf(port));
        props.setProperty("db.name", database);
        props.setProperty("db.user", user);
        props.setProperty("db.password", password);
        try (FileWriter writer = new FileWriter(file)) {
            props.store(writer, "CPL MySQL Database Configuration");
        }
    }

    public String getJdbcUrl() {
        return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                host, port, database);
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }
    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
