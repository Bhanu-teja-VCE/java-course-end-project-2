package com.cpl.io;

import com.cpl.model.AllRounder;
import com.cpl.model.Batter;
import com.cpl.model.Bowler;
import com.cpl.model.Player;
import com.cpl.model.Role;
import com.cpl.model.WicketKeeper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Parses player records from a CSV file.
 * Demonstrates:
 * - Unit II: Exception handling (try-with-resources, IOException)
 * - Unit III: FileReader, BufferedReader, StringTokenizer
 */
public class PlayerCsvParser {

    public static List<Player> parseFromReader(BufferedReader reader) throws IOException {
        List<Player> players = new ArrayList<Player>();
        String line = reader.readLine(); // skip header

        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) continue;

            // Unit III: StringTokenizer for splitting CSV line tokens
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            if (tokenizer.countTokens() < 8) continue;

            int id = Integer.parseInt(tokenizer.nextToken().trim());
            String name = tokenizer.nextToken().trim();
            String roleStr = tokenizer.nextToken().trim();
            double basePrice = Double.parseDouble(tokenizer.nextToken().trim());
            boolean isOverseas = Boolean.parseBoolean(tokenizer.nextToken().trim());
            int batRating = Integer.parseInt(tokenizer.nextToken().trim());
            int bowlRating = Integer.parseInt(tokenizer.nextToken().trim());
            int fieldRating = Integer.parseInt(tokenizer.nextToken().trim());

            Role role = Role.valueOf(roleStr.toUpperCase());
            Player player;
            switch (role) {
                case BATTER:
                    player = new Batter(id, name, basePrice, isOverseas, batRating, bowlRating, fieldRating);
                    break;
                case BOWLER:
                    player = new Bowler(id, name, basePrice, isOverseas, batRating, bowlRating, fieldRating);
                    break;
                case ALL_ROUNDER:
                    player = new AllRounder(id, name, basePrice, isOverseas, batRating, bowlRating, fieldRating);
                    break;
                case WICKET_KEEPER:
                    player = new WicketKeeper(id, name, basePrice, isOverseas, batRating, bowlRating, fieldRating);
                    break;
                default:
                    player = new Batter(id, name, basePrice, isOverseas, batRating, bowlRating, fieldRating);
            }
            players.add(player);
        }
        return players;
    }

    public static List<Player> parseFromFile(File file) throws IOException {
        // Unit III: FileReader wrapped in BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return parseFromReader(reader);
        }
    }

    public static List<Player> parseFromResource(String resourcePath) throws IOException {
        InputStream is = PlayerCsvParser.class.getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return parseFromReader(reader);
        }
    }
}
