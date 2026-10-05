package com.cpl.ui;

import com.cpl.auction.AggressiveStrategy;
import com.cpl.auction.AuctionLot;
import com.cpl.auction.BiddingBot;
import com.cpl.auction.BudgetMindedStrategy;
import com.cpl.auction.NeedsBasedStrategy;
import com.cpl.auction.RandomStrategy;
import com.cpl.db.InMemoryTournamentRepository;
import com.cpl.io.PlayerCsvParser;
import com.cpl.model.BallEvent;
import com.cpl.model.Batter;
import com.cpl.model.Innings;
import com.cpl.model.Match;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.Tournament;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Automates GUI rendering and screenshot generation for project documentation.
 */
public class ScreenshotTour {

    public static void main(String[] args) throws Exception {
        File outDir = new File(args.length > 0 ? args[0] : "screenshots");
        outDir.mkdirs();

        List<Team> teams = createTeams();
        List<Player> players = PlayerCsvParser.parseFromResource("/data/players.csv");

        // Distribute some players for squad and match visuals
        for (int i = 0; i < 40; i++) {
            Team t = teams.get(i % teams.size());
            if (t.getSquadSize() < t.getMaxSquad()) {
                t.addPlayer(players.get(i), players.get(i).getBasePrice());
            }
        }

        Tournament tournament = new Tournament("Campus Premier League", 2026, teams, players);
        InMemoryTournamentRepository repo = new InMemoryTournamentRepository(teams, players);

        // Bots
        List<BiddingBot> bots = new ArrayList<BiddingBot>();
        bots.add(new BiddingBot(teams.get(0), new AggressiveStrategy()));
        bots.add(new BiddingBot(teams.get(1), new BudgetMindedStrategy()));
        bots.add(new BiddingBot(teams.get(2), new NeedsBasedStrategy()));
        bots.add(new BiddingBot(teams.get(3), new RandomStrategy()));

        int w = 1100;
        int h = 720;

        // 1. Auction Hall
        AuctionHallPanel auctionPanel = new AuctionHallPanel(teams, players, bots);
        AuctionLot lot = new AuctionLot(1, players.get(0));
        lot.startLot();
        auctionPanel.onLotStarted(lot);
        auctionPanel.onBidReceived(lot, teams.get(1), 1200000.0);
        auctionPanel.onCountdownTick(lot, 2, "Going once...");
        captureComponent(auctionPanel, w, h, new File(outDir, "01-auction-hall.png"));

        // 2. Squads Panel
        SquadsPanel squadsPanel = new SquadsPanel(teams);
        captureComponent(squadsPanel, w, h, new File(outDir, "02-squads.png"));

        // 3. Fixtures Panel
        FixturesPanel fixturesPanel = new FixturesPanel(tournament);
        captureComponent(fixturesPanel, w, h, new File(outDir, "03-fixtures.png"));

        // 4. Live Match Panel
        LiveMatchPanel liveMatchPanel = new LiveMatchPanel(tournament);
        // Add sample deliveries for wagon wheel and worm graph visuals
        Innings sampleInn1 = new Innings(teams.get(0).getId(), teams.get(1).getId());
        for (int i = 1; i <= 24; i++) {
            int runs = (i % 6 == 0) ? 6 : ((i % 4 == 0) ? 4 : (i % 2));
            BallEvent b = new BallEvent(i, (i - 1) / 6, ((i - 1) % 6) + 1,
                    1, "Rohit Sharma", 2, "Jasprit Bumrah", runs, 0, "NONE",
                    (i == 18), "CAUGHT", i * 15.0, 40 + i * 2.0, "Great shot!");
            sampleInn1.recordBall(b);
            liveMatchPanel.onBallBowled(b, sampleInn1, 1);
        }
        captureComponent(liveMatchPanel, w, h, new File(outDir, "04-live-match.png"));

        // 5. Standings & Caps Panel
        StandingsPanel standingsPanel = new StandingsPanel(tournament, repo);
        captureComponent(standingsPanel, w, h, new File(outDir, "05-standings.png"));

        // 6. Thread Monitor Panel
        ThreadMonitorPanel threadMonitor = new ThreadMonitorPanel();
        captureComponent(threadMonitor, w, h, new File(outDir, "06-thread-monitor.png"));

        System.out.println("Successfully generated all 6 GUI screenshots in: " + outDir.getAbsolutePath());
    }

    private static void captureComponent(JComponent comp, int width, int height, File outFile) throws Exception {
        comp.setSize(new Dimension(width, height));
        comp.doLayout();
        comp.validate();

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        comp.printAll(g2);
        g2.dispose();

        ImageIO.write(img, "png", outFile);
    }

    private static List<Team> createTeams() {
        List<Team> list = new ArrayList<Team>();
        list.add(new Team(1, "CSE Cyber Knights", "CCK", "#1d5fa8", 10000000.0));
        list.add(new Team(2, "AI Tech Aces", "ATA", "#0b6b43", 10000000.0));
        list.add(new Team(3, "Mech Mavericks", "MM", "#b3261e", 10000000.0));
        list.add(new Team(4, "ECE Spark Warriors", "ESW", "#86570a", 10000000.0));
        list.add(new Team(5, "Civil Centurions", "CNC", "#5d6c64", 10000000.0));
        list.add(new Team(6, "Data Dynamos", "DD", "#7a3d9c", 10000000.0));
        list.add(new Team(7, "BioTech Titans", "BTT", "#12794a", 10000000.0));
        list.add(new Team(8, "Quantum Quarks", "QQ", "#d97706", 10000000.0));
        return list;
    }
}
