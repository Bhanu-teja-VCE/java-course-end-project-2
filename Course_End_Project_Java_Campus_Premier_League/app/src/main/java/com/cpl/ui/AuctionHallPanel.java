package com.cpl.ui;

import com.cpl.auction.AuctionLot;
import com.cpl.auction.Auctioneer;
import com.cpl.auction.BiddingBot;
import com.cpl.model.Player;
import com.cpl.model.Team;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Auction Hall GUI Panel.
 * Demonstrates:
 * - Unit IV: Swing components (JPanel, JLabel, JButton, JTable, JProgressBar), Layouts (BorderLayout, GridLayout, FlowLayout)
 * - Unit IV: Delegation event model (ActionListeners)
 */
public class AuctionHallPanel extends JPanel implements Auctioneer.AuctionListener {
    private static final long serialVersionUID = 1L;

    private final JLabel playerNameLabel = new JLabel("Auction Waiting to Start", SwingConstants.CENTER);
    private final JLabel playerRoleLabel = new JLabel("Role: -", SwingConstants.CENTER);
    private final JLabel playerRatingsLabel = new JLabel("Ratings: -", SwingConstants.CENTER);
    private final JLabel currentBidLabel = new JLabel("Current Bid: ₹0", SwingConstants.CENTER);
    private final JLabel highestBidderLabel = new JLabel("Highest Bidder: None", SwingConstants.CENTER);
    private final JLabel callPhraseLabel = new JLabel("Waiting...", SwingConstants.CENTER);
    private final JProgressBar countdownBar = new JProgressBar(0, 3);

    private final DefaultTableModel bidTableModel = new DefaultTableModel(new Object[]{"Lot", "Player", "Team", "Bid (₹)"}, 0);
    private final JTable bidTable = new JTable(bidTableModel);

    private final JPanel teamsGridPanel = new JPanel(new GridLayout(2, 4, 8, 8));
    private final JButton bid50kButton = new JButton("+ ₹50,000");
    private final JButton bid100kButton = new JButton("+ ₹1,00,000");
    private final JButton pauseButton = new JButton("Pause");
    private final JButton startAuctionButton = new JButton("Start Live Auction");

    private Auctioneer auctioneer;
    private Team userTeam;
    private final List<Team> teams;
    private final List<Player> players;
    private final List<BiddingBot> bots;

    public AuctionHallPanel(List<Team> teams, List<Player> players, List<BiddingBot> bots) {
        this.teams = teams;
        this.players = players;
        this.bots = bots;
        if (!teams.isEmpty()) {
            this.userTeam = teams.get(0); // Default user franchise
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        add(buildTopHeader(), BorderLayout.NORTH);
        add(buildCenterLotView(), BorderLayout.CENTER);
        add(buildTeamsSummaryPanel(), BorderLayout.SOUTH);

        setupEventHandlers();
        refreshTeamsGrid();
    }

    private JPanel buildTopHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = new JLabel("CPL Live Player Auction - Mega Bidding Floor");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Theme.TEXT_LIGHT);
        panel.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        startAuctionButton.setBackground(Theme.ACCENT_GREEN);
        startAuctionButton.setForeground(Color.WHITE);
        pauseButton.setBackground(Theme.BG_CARD_LIGHT);
        pauseButton.setForeground(Color.WHITE);
        pauseButton.setEnabled(false);

        controls.add(startAuctionButton);
        controls.add(pauseButton);
        panel.add(controls, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCenterLotView() {
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);

        // Left: Current Lot Card
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Theme.BG_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.ACCENT_BLUE, 2),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        playerNameLabel.setFont(Theme.FONT_HEADER);
        playerNameLabel.setForeground(Theme.ACCENT_GOLD);
        playerNameLabel.setAlignmentX(CENTER_ALIGNMENT);

        playerRoleLabel.setFont(Theme.FONT_SUBHEADER);
        playerRoleLabel.setForeground(Theme.TEXT_LIGHT);
        playerRoleLabel.setAlignmentX(CENTER_ALIGNMENT);

        playerRatingsLabel.setFont(Theme.FONT_BODY);
        playerRatingsLabel.setForeground(Theme.TEXT_MUTED);
        playerRatingsLabel.setAlignmentX(CENTER_ALIGNMENT);

        currentBidLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 22));
        currentBidLabel.setForeground(Theme.ACCENT_GREEN);
        currentBidLabel.setAlignmentX(CENTER_ALIGNMENT);

        highestBidderLabel.setFont(Theme.FONT_SUBHEADER);
        highestBidderLabel.setForeground(Theme.TEXT_LIGHT);
        highestBidderLabel.setAlignmentX(CENTER_ALIGNMENT);

        callPhraseLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        callPhraseLabel.setForeground(Theme.ACCENT_RED);
        callPhraseLabel.setAlignmentX(CENTER_ALIGNMENT);

        countdownBar.setValue(3);
        countdownBar.setStringPainted(true);
        countdownBar.setForeground(Theme.ACCENT_GOLD);
        countdownBar.setMaximumSize(new Dimension(280, 20));

        JPanel userBidControls = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        userBidControls.setOpaque(false);
        userBidControls.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BG_CARD_LIGHT), "Your Team Bid (" + (userTeam != null ? userTeam.getShortName() : "") + ")",
                0, 0, Theme.FONT_BODY, Theme.TEXT_MUTED));

        bid50kButton.setBackground(Theme.ACCENT_BLUE);
        bid50kButton.setForeground(Color.WHITE);
        bid100kButton.setBackground(Theme.ACCENT_BLUE);
        bid100kButton.setForeground(Color.WHITE);
        bid50kButton.setEnabled(false);
        bid100kButton.setEnabled(false);

        userBidControls.add(bid50kButton);
        userBidControls.add(bid100kButton);

        card.add(playerNameLabel);
        card.add(Box.createVerticalStrut(6));
        card.add(playerRoleLabel);
        card.add(playerRatingsLabel);
        card.add(Box.createVerticalStrut(14));
        card.add(currentBidLabel);
        card.add(highestBidderLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(callPhraseLabel);
        card.add(countdownBar);
        card.add(Box.createVerticalStrut(14));
        card.add(userBidControls);

        center.add(card);

        // Right: Recent Bids Log Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Theme.BG_CARD);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BG_CARD_LIGHT), "Live Auction Log",
                0, 0, Theme.FONT_SUBHEADER, Theme.TEXT_LIGHT));

        bidTable.setBackground(Theme.BG_CARD_LIGHT);
        bidTable.setForeground(Theme.TEXT_LIGHT);
        bidTable.setRowHeight(24);
        tablePanel.add(new JScrollPane(bidTable), BorderLayout.CENTER);

        center.add(tablePanel);
        return center;
    }

    private JPanel buildTeamsSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.BG_CARD);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Theme.BG_CARD_LIGHT), "Franchise Purses & Squad Counts",
                0, 0, Theme.FONT_SUBHEADER, Theme.TEXT_LIGHT));

        teamsGridPanel.setOpaque(false);
        panel.add(teamsGridPanel, BorderLayout.CENTER);
        return panel;
    }

    private void refreshTeamsGrid() {
        teamsGridPanel.removeAll();
        for (Team t : teams) {
            JPanel tile = new JPanel(new GridLayout(2, 1));
            tile.setBackground(Theme.BG_CARD_LIGHT);
            tile.setBorder(BorderFactory.createLineBorder(Color.decode(t.getColorHex()), 2));

            JLabel title = new JLabel(t.getShortName() + " - " + t.getName(), SwingConstants.CENTER);
            title.setForeground(Theme.TEXT_LIGHT);
            title.setFont(Theme.FONT_SUBHEADER);

            JLabel info = new JLabel(String.format("₹%,.1fL | Sq: %d/%d (OS: %d)",
                    t.getCurrentPurse() / 100000.0, t.getSquadSize(), t.getMaxSquad(), t.getOverseasCount()), SwingConstants.CENTER);
            info.setForeground(Theme.ACCENT_GOLD);
            info.setFont(Theme.FONT_BODY);

            tile.add(title);
            tile.add(info);
            teamsGridPanel.add(tile);
        }
        teamsGridPanel.revalidate();
        teamsGridPanel.repaint();
    }

    private void setupEventHandlers() {
        startAuctionButton.addActionListener(e -> {
            startAuctionButton.setEnabled(false);
            pauseButton.setEnabled(true);
            bid50kButton.setEnabled(true);
            bid100kButton.setEnabled(true);

            // Start bots in background
            for (BiddingBot bot : bots) {
                Thread botThread = new Thread(bot, "Bot-" + bot.getTeam().getShortName());
                botThread.start();
            }

            // Start auctioneer thread
            auctioneer = new Auctioneer(players, bots);
            auctioneer.addListener(this);
            auctioneer.start();
        });

        pauseButton.addActionListener(e -> {
            if ("Pause".equals(pauseButton.getText())) {
                if (auctioneer != null) auctioneer.pauseAuction();
                pauseButton.setText("Resume");
            } else {
                if (auctioneer != null) auctioneer.resumeAuction();
                pauseButton.setText("Pause");
            }
        });

        bid50kButton.addActionListener(e -> placeManualUserBid(50000.0));
        bid100kButton.addActionListener(e -> placeManualUserBid(100000.0));
    }

    private void placeManualUserBid(double increment) {
        if (auctioneer == null || auctioneer.getActiveLot() == null) return;
        AuctionLot lot = auctioneer.getActiveLot();
        double targetBid = lot.getCurrentBid() + increment;
        try {
            lot.submitBid(userTeam, targetBid);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Bid Rejected", JOptionPane.WARNING_MESSAGE);
        }
    }

    // --- AuctionListener Callback Implementations (Unit IV: Event delegation) ---

    @Override
    public void onLotStarted(AuctionLot lot) {
        SwingUtilities.invokeLater(() -> {
            Player p = lot.getPlayer();
            playerNameLabel.setText(String.format("Lot #%d: %s", lot.getLotNumber(), p.getName()));
            playerRoleLabel.setText(String.format("Role: %s %s", p.getRole().getDisplayName(), p.isOverseas() ? "[OVERSEAS]" : "[CAMPUS]"));
            playerRatingsLabel.setText(String.format("Bat: %d | Bowl: %d | Field: %d | Base: ₹%,.0f",
                    p.getBattingRating(), p.getBowlingRating(), p.getFieldingRating(), p.getBasePrice()));
            currentBidLabel.setText(String.format("Current Bid: ₹%,.0f", lot.getCurrentBid()));
            highestBidderLabel.setText("Highest Bidder: Opening Lot");
            callPhraseLabel.setText("Bidding Opened!");
            countdownBar.setValue(3);
        });
    }

    @Override
    public void onBidReceived(AuctionLot lot, Team team, double amount) {
        SwingUtilities.invokeLater(() -> {
            currentBidLabel.setText(String.format("Current Bid: ₹%,.0f", amount));
            highestBidderLabel.setText("Highest Bidder: " + (team != null ? team.getName() : "None"));
            bidTableModel.insertRow(0, new Object[]{
                lot.getLotNumber(), lot.getPlayer().getName(), team != null ? team.getShortName() : "", String.format("₹%,.0f", amount)
            });
            countdownBar.setValue(3);
        });
    }

    @Override
    public void onCountdownTick(AuctionLot lot, int secondsRemaining, String callPhrase) {
        SwingUtilities.invokeLater(() -> {
            countdownBar.setValue(secondsRemaining);
            callPhraseLabel.setText(callPhrase);
        });
    }

    @Override
    public void onLotClosed(AuctionLot lot, boolean sold) {
        SwingUtilities.invokeLater(() -> {
            if (sold) {
                callPhraseLabel.setText("SOLD to " + lot.getHighestBidder().getShortName() + "!");
            } else {
                callPhraseLabel.setText("UNSOLD!");
            }
            refreshTeamsGrid();
        });
    }

    @Override
    public void onAuctionFinished(int totalSold, int totalUnsold) {
        SwingUtilities.invokeLater(() -> {
            callPhraseLabel.setText("AUCTION COMPLETED!");
            JOptionPane.showMessageDialog(this,
                    String.format("Mega Auction Concluded!\nTotal Sold: %d\nTotal Unsold: %d", totalSold, totalUnsold),
                    "Auction Complete", JOptionPane.INFORMATION_MESSAGE);
            startAuctionButton.setEnabled(false);
            pauseButton.setEnabled(false);
            bid50kButton.setEnabled(false);
            bid100kButton.setEnabled(false);
        });
    }
}
