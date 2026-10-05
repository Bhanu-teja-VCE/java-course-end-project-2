-- Campus Premier League (CPL) Database Schema
-- Compatible with MySQL 8.0+

CREATE DATABASE IF NOT EXISTS campus_premier_league;
USE campus_premier_league;

-- 1. Drop existing objects for clean installation
DROP PROCEDURE IF EXISTS sp_close_auction_lot;
DROP PROCEDURE IF EXISTS sp_points_table;
DROP PROCEDURE IF EXISTS sp_top_scorers;
DROP PROCEDURE IF EXISTS sp_top_wicket_takers;

DROP TABLE IF EXISTS cpl_ball_event;
DROP TABLE IF EXISTS cpl_auction_bid;
DROP TABLE IF EXISTS cpl_match;
DROP TABLE IF EXISTS cpl_player;
DROP TABLE IF EXISTS cpl_team;

-- 2. Franchise Teams Table
CREATE TABLE cpl_team (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    short_name VARCHAR(10) NOT NULL UNIQUE,
    color_hex VARCHAR(10) NOT NULL DEFAULT '#1d5fa8',
    initial_purse DECIMAL(12,2) NOT NULL DEFAULT 10000000.00, -- 100 Lakhs (1 Cr)
    current_purse DECIMAL(12,2) NOT NULL DEFAULT 10000000.00,
    max_squad INT NOT NULL DEFAULT 18,
    min_squad INT NOT NULL DEFAULT 11,
    max_overseas INT NOT NULL DEFAULT 4,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Players Table
CREATE TABLE cpl_player (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL, -- BATTER, BOWLER, ALL_ROUNDER, WICKET_KEEPER
    base_price DECIMAL(12,2) NOT NULL DEFAULT 200000.00,
    sold_price DECIMAL(12,2) DEFAULT NULL,
    team_id INT DEFAULT NULL,
    is_overseas BOOLEAN NOT NULL DEFAULT FALSE,
    batting_rating INT NOT NULL DEFAULT 70,
    bowling_rating INT NOT NULL DEFAULT 70,
    fielding_rating INT NOT NULL DEFAULT 75,
    FOREIGN KEY (team_id) REFERENCES cpl_team(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Auction Bids Table
CREATE TABLE cpl_auction_bid (
    id INT PRIMARY KEY AUTO_INCREMENT,
    lot_number INT NOT NULL,
    player_id INT NOT NULL,
    team_id INT NOT NULL,
    bid_amount DECIMAL(12,2) NOT NULL,
    bid_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES cpl_player(id) ON DELETE CASCADE,
    FOREIGN KEY (team_id) REFERENCES cpl_team(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. Matches Table
CREATE TABLE cpl_match (
    id INT PRIMARY KEY AUTO_INCREMENT,
    match_number INT NOT NULL,
    stage VARCHAR(30) NOT NULL DEFAULT 'LEAGUE', -- LEAGUE, QUALIFIER_1, ELIMINATOR, QUALIFIER_2, FINAL
    team1_id INT NOT NULL,
    team2_id INT NOT NULL,
    winner_id INT DEFAULT NULL,
    team1_runs INT NOT NULL DEFAULT 0,
    team1_wickets INT NOT NULL DEFAULT 0,
    team1_overs DECIMAL(4,1) NOT NULL DEFAULT 0.0,
    team2_runs INT NOT NULL DEFAULT 0,
    team2_wickets INT NOT NULL DEFAULT 0,
    team2_overs DECIMAL(4,1) NOT NULL DEFAULT 0.0,
    mom_player_id INT DEFAULT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    FOREIGN KEY (team1_id) REFERENCES cpl_team(id) ON DELETE CASCADE,
    FOREIGN KEY (team2_id) REFERENCES cpl_team(id) ON DELETE CASCADE,
    FOREIGN KEY (winner_id) REFERENCES cpl_team(id) ON DELETE SET NULL,
    FOREIGN KEY (mom_player_id) REFERENCES cpl_player(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. Ball Events Table (for granular telemetry)
CREATE TABLE cpl_ball_event (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    match_id INT NOT NULL,
    innings_num INT NOT NULL,
    over_num INT NOT NULL,
    ball_num INT NOT NULL,
    batter_id INT NOT NULL,
    bowler_id INT NOT NULL,
    runs_scored INT NOT NULL DEFAULT 0,
    extra_runs INT NOT NULL DEFAULT 0,
    extra_type VARCHAR(10) DEFAULT 'NONE',
    is_wicket BOOLEAN NOT NULL DEFAULT FALSE,
    dismissal_type VARCHAR(20) DEFAULT 'NONE',
    FOREIGN KEY (match_id) REFERENCES cpl_match(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 7. Stored Procedure 1: Close Auction Lot Atomically (ACID transaction)
DELIMITER $$
CREATE PROCEDURE sp_close_auction_lot(
    IN p_player_id INT,
    IN p_team_id INT,
    IN p_sold_price DECIMAL(12,2)
)
BEGIN
    DECLARE current_team_purse DECIMAL(12,2);
    
    START TRANSACTION;
    
    SELECT current_purse INTO current_team_purse 
    FROM cpl_team WHERE id = p_team_id FOR UPDATE;
    
    IF current_team_purse < p_sold_price THEN
        ROLLBACK;
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Insufficient purse for winning bid';
    ELSE
        UPDATE cpl_player 
        SET team_id = p_team_id, sold_price = p_sold_price 
        WHERE id = p_player_id;
        
        UPDATE cpl_team 
        SET current_purse = current_purse - p_sold_price 
        WHERE id = p_team_id;
        
        COMMIT;
    END IF;
END$$

-- 8. Stored Procedure 2: Points Table & Net Run Rate (NRR)
CREATE PROCEDURE sp_points_table()
BEGIN
    SELECT 
        t.id,
        t.name,
        t.short_name,
        t.color_hex,
        COUNT(m.id) AS matches_played,
        SUM(CASE WHEN m.winner_id = t.id THEN 1 ELSE 0 END) AS wins,
        SUM(CASE WHEN m.winner_id IS NOT NULL AND m.winner_id != t.id THEN 1 ELSE 0 END) AS losses,
        SUM(CASE WHEN m.winner_id IS NULL AND m.id IS NOT NULL THEN 1 ELSE 0 END) AS ties,
        SUM(CASE WHEN m.winner_id = t.id THEN 2 WHEN m.winner_id IS NULL AND m.id IS NOT NULL THEN 1 ELSE 0 END) AS points,
        ROUND(
            COALESCE(
                (SUM(CASE WHEN m.team1_id = t.id THEN m.team1_runs ELSE m.team2_runs END) / 
                 NULLIF(SUM(CASE WHEN m.team1_id = t.id THEN FLOOR(m.team1_overs) + (m.team1_overs - FLOOR(m.team1_overs))*10/6 
                                 ELSE FLOOR(m.team2_overs) + (m.team2_overs - FLOOR(m.team2_overs))*10/6 END), 0))
                -
                (SUM(CASE WHEN m.team1_id = t.id THEN m.team2_runs ELSE m.team1_runs END) / 
                 NULLIF(SUM(CASE WHEN m.team1_id = t.id THEN FLOOR(m.team2_overs) + (m.team2_overs - FLOOR(m.team2_overs))*10/6 
                                 ELSE FLOOR(m.team1_overs) + (m.team1_overs - FLOOR(m.team1_overs))*10/6 END), 0))
            , 0.000), 3
        ) AS net_run_rate
    FROM cpl_team t
    LEFT JOIN cpl_match m ON (t.id = m.team1_id OR t.id = m.team2_id) AND m.stage = 'LEAGUE'
    GROUP BY t.id, t.name, t.short_name, t.color_hex
    ORDER BY points DESC, net_run_rate DESC;
END$$

-- 9. Stored Procedure 3: Top Scorers (Orange Cap)
CREATE PROCEDURE sp_top_scorers(IN p_limit INT)
BEGIN
    SELECT 
        p.id,
        p.name,
        p.role,
        t.short_name AS team_name,
        COALESCE(SUM(b.runs_scored), 0) AS total_runs,
        COUNT(b.id) AS balls_faced,
        ROUND(COALESCE(SUM(b.runs_scored) * 100.0 / NULLIF(COUNT(b.id), 0), 0), 2) AS strike_rate
    FROM cpl_player p
    LEFT JOIN cpl_team t ON p.team_id = t.id
    LEFT JOIN cpl_ball_event b ON p.id = b.batter_id
    GROUP BY p.id, p.name, p.role, t.short_name
    HAVING total_runs > 0
    ORDER BY total_runs DESC, strike_rate DESC
    LIMIT p_limit;
END$$

-- 10. Stored Procedure 4: Top Wicket Takers (Purple Cap)
CREATE PROCEDURE sp_top_wicket_takers(IN p_limit INT)
BEGIN
    SELECT 
        p.id,
        p.name,
        p.role,
        t.short_name AS team_name,
        COALESCE(SUM(CASE WHEN b.is_wicket = TRUE THEN 1 ELSE 0 END), 0) AS total_wickets,
        COUNT(b.id) AS balls_bowled,
        ROUND(COALESCE(SUM(b.runs_scored + b.extra_runs) * 6.0 / NULLIF(COUNT(b.id), 0), 0), 2) AS economy_rate
    FROM cpl_player p
    LEFT JOIN cpl_team t ON p.team_id = t.id
    LEFT JOIN cpl_ball_event b ON p.id = b.bowler_id
    GROUP BY p.id, p.name, p.role, t.short_name
    HAVING total_wickets > 0
    ORDER BY total_wickets DESC, economy_rate ASC
    LIMIT p_limit;
END$$
DELIMITER ;

-- 11. Initial Franchise Teams Seed Data
INSERT INTO cpl_team (name, short_name, color_hex, initial_purse, current_purse) VALUES
('CSE Cyber Knights', 'CCK', '#1d5fa8', 10000000.00, 10000000.00),
('AI Tech Aces', 'ATA', '#0b6b43', 10000000.00, 10000000.00),
('Mech Mavericks', 'MM', '#b3261e', 10000000.00, 10000000.00),
('ECE Spark Warriors', 'ESW', '#86570a', 10000000.00, 10000000.00),
('Civil Centurions', 'CNC', '#5d6c64', 10000000.00, 10000000.00),
('Data Dynamos', 'DD', '#7a3d9c', 10000000.00, 10000000.00),
('BioTech Titans', 'BTT', '#12794a', 10000000.00, 10000000.00),
('Quantum Quarks', 'QQ', '#d97706', 10000000.00, 10000000.00);
