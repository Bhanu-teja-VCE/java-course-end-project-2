package com.cpl.ui;

import com.cpl.auction.AggressiveStrategy;
import com.cpl.auction.BiddingBot;
import com.cpl.auction.BiddingStrategy;
import com.cpl.auction.BudgetMindedStrategy;
import com.cpl.auction.NeedsBasedStrategy;
import com.cpl.auction.RandomStrategy;
import com.cpl.db.Database;
import com.cpl.db.DbConfig;
import com.cpl.db.InMemoryTournamentRepository;
import com.cpl.db.MySqlTournamentRepository;
import com.cpl.db.TournamentRepository;
import com.cpl.engine.CplConfig;
import com.cpl.engine.ThreadRegistry;
import com.cpl.io.PlayerCsvParser;
import com.cpl.io.TournamentSerializer;
import com.cpl.model.Player;
import com.cpl.model.Team;
import com.cpl.tournament.Tournament;

import javax.swing.BorderFactory;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application frame containing top navigation, multi-tab interface, and status bar.
 * Demonstrates:
 * - Unit IV: JFrame, JTabbedPane, JMenuBar, JMenu, JMenuItem, BorderLayout
 */
public class MainFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTabbedPane tabbedPane = new JTabbedPane();
    private final JLabel statusLabel = new JLabel(" Database: Initializing...", SwingConstants.LEFT);

    private final DbConfig dbConfig;
    private TournamentRepository repository;
    private Tournament tournament;

    public MainFrame() {
        super("Campus Premier League (CPL) - T20 Cricket & Mega Auction Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setMinimumSize(new Dimension(980, 640));
        setLocationRelativeTo(null);

        this.dbConfig = DbConfig.loadFromFile(new File("db.properties"));
        initDataAndRepository();

        setJMenuBar(buildMenuBar());
        setLayout(new BorderLayout());

        // Register main GUI thread
        ThreadRegistry.register(Thread.currentThread());

        // Setup tabs
        setupTabs();

        add(tabbedPane, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        updateDatabaseStatus();
    }

    private void initDataAndRepository() {
        // Load initial teams
        List<Team> initialTeams = createDefaultTeams();

        // Load 120 players from resource CSV
        List<Player> initialPlayers;
        try {
            initialPlayers = PlayerCsvParser.parseFromResource("/data/players.csv");
        } catch (Exception e) {
            initialPlayers = new ArrayList<Player>();
        }

        // Initialize repository
        if (Database.testConnection(dbConfig)) {
            repository = new MySqlTournamentRepository();
        } else {
            repository = new InMemoryTournamentRepository(initialTeams, initialPlayers);
        }

        tournament = new Tournament(CplConfig.LEAGUE_NAME, CplConfig.SEASON_YEAR, initialTeams, initialPlayers);
    }

    private List<Team> createDefaultTeams() {
        List<Team> list = new ArrayList<Team>();
        list.add(new Team(1, "CSE Cyber Knights", "CCK", "#1d5fa8", 10000000.00));
        list.add(new Team(2, "AI Tech Aces", "ATA", "#0b6b43", 10000000.00));
        list.add(new Team(3, "Mech Mavericks", "MM", "#b3261e", 10000000.00));
        list.add(new Team(4, "ECE Spark Warriors", "ESW", "#86570a", 10000000.00));
        list.add(new Team(5, "Civil Centurions", "CNC", "#5d6c64", 10000000.00));
        list.add(new Team(6, "Data Dynamos", "DD", "#7a3d9c", 10000000.00));
        list.add(new Team(7, "BioTech Titans", "BTT", "#12794a", 10000000.00));
        list.add(new Team(8, "Quantum Quarks", "QQ", "#d97706", 10000000.00));
        return list;
    }

    private void setupTabs() {
        List<Team> teams = tournament.getTeams();
        List<Player> players = tournament.getAllPlayers();

        // Create 8 bot bidders with diverse strategies
        List<BiddingBot> bots = new ArrayList<BiddingBot>();
        BiddingStrategy[] strategies = {
            new AggressiveStrategy(), new BudgetMindedStrategy(),
            new NeedsBasedStrategy(), new RandomStrategy()
        };

        for (int i = 0; i < teams.size(); i++) {
            Team t = teams.get(i);
            BiddingStrategy s = strategies[i % strategies.length];
            bots.add(new BiddingBot(t, s));
        }

        AuctionHallPanel auctionPanel = new AuctionHallPanel(teams, players, bots);
        SquadsPanel squadsPanel = new SquadsPanel(teams);
        FixturesPanel fixturesPanel = new FixturesPanel(tournament);
        LiveMatchPanel liveMatchPanel = new LiveMatchPanel(tournament);
        StandingsPanel standingsPanel = new StandingsPanel(tournament, repository);
        ThreadMonitorPanel threadMonitor = new ThreadMonitorPanel();

        tabbedPane.addTab("Live Auction", auctionPanel);
        tabbedPane.addTab("Franchise Squads", squadsPanel);
        tabbedPane.addTab("Fixtures Schedule", fixturesPanel);
        tabbedPane.addTab("Live Match Simulator", liveMatchPanel);
        tabbedPane.addTab("Standings & Caps", standingsPanel);
        tabbedPane.addTab("Thread Monitor", threadMonitor);

        tabbedPane.setMnemonicAt(0, KeyEvent.VK_A);
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_S);
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_F);
        tabbedPane.setMnemonicAt(3, KeyEvent.VK_M);
        tabbedPane.setMnemonicAt(4, KeyEvent.VK_T);
        tabbedPane.setMnemonicAt(5, KeyEvent.VK_R);

        tabbedPane.addChangeListener(e -> {
            int selected = tabbedPane.getSelectedIndex();
            if (selected == 1) squadsPanel.updateRosterTable();
            if (selected == 2) fixturesPanel.refreshTable(null);
            if (selected == 4) standingsPanel.refreshTables();
        });
    }

    private JMenuBar buildMenuBar() {
        JMenuBar mb = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        JMenuItem saveItem = new JMenuItem("Save Tournament (.cpl)...");
        JMenuItem loadItem = new JMenuItem("Load Tournament (.cpl)...");
        JMenuItem exitItem = new JMenuItem("Exit");

        saveItem.addActionListener(e -> saveTournamentFile());
        loadItem.addActionListener(e -> loadTournamentFile());
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        mb.add(fileMenu);

        // Database Menu
        JMenu dbMenu = new JMenu("Database");
        dbMenu.setMnemonic(KeyEvent.VK_D);

        JMenuItem dbSettingsItem = new JMenuItem("Connection Settings...");
        JMenuItem dbReconnectItem = new JMenuItem("Reconnect to MySQL");

        dbSettingsItem.addActionListener(e -> {
            SettingsDialog dialog = new SettingsDialog(this, dbConfig);
            dialog.setVisible(true);
            updateDatabaseStatus();
        });

        dbReconnectItem.addActionListener(e -> {
            if (Database.testConnection(dbConfig)) {
                repository = new MySqlTournamentRepository();
            }
            updateDatabaseStatus();
        });

        dbMenu.add(dbSettingsItem);
        dbMenu.add(dbReconnectItem);
        mb.add(dbMenu);

        // Help Menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About CPL");
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Campus Premier League (CPL)\n" +
                "Course: Object Oriented Programming through Java (VCE-R25)\n" +
                "Batch 6, Vardhaman College of Engineering\n" +
                "Authors:\n" +
                "- Madhavarapu Saritha (25881A05V7)\n" +
                "- Chepyala Vishal (25881A05X9)\n" +
                "- Gundu Srijay Krishna (25881A05X0)",
                "About CPL", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        mb.add(helpMenu);

        return mb;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(Theme.BG_CARD);
        bar.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        statusLabel.setForeground(Theme.TEXT_LIGHT);
        statusLabel.setFont(Theme.FONT_BODY);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel batchInfo = new JLabel("VCE-R25 CSE | Batch 6  ");
        batchInfo.setForeground(Theme.TEXT_MUTED);
        batchInfo.setFont(Theme.FONT_BODY);
        bar.add(batchInfo, BorderLayout.EAST);
        return bar;
    }

    private void updateDatabaseStatus() {
        if (Database.testConnection(dbConfig)) {
            statusLabel.setText(" Database: Connected to MySQL 8.0 (" + dbConfig.getDatabase() + ")");
            statusLabel.setForeground(Theme.ACCENT_GREEN);
        } else {
            statusLabel.setText(" Database: Offline Mode (Using High-Performance In-Memory Engine)");
            statusLabel.setForeground(Theme.ACCENT_GOLD);
        }
    }

    private void saveTournamentFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("cpl_season_2026.cpl"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                TournamentSerializer.saveToFile(tournament, chooser.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Tournament successfully serialized to " + chooser.getSelectedFile().getName(),
                        "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error saving tournament: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadTournamentFile() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                tournament = TournamentSerializer.loadFromFile(chooser.getSelectedFile());
                tabbedPane.removeAll();
                setupTabs();
                JOptionPane.showMessageDialog(this, "Tournament successfully loaded from " + chooser.getSelectedFile().getName(),
                        "Loaded", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading tournament: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
