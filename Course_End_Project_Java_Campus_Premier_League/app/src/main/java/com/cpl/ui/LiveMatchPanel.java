package com.cpl.ui;

import com.cpl.match.MatchEngine;
import com.cpl.match.PitchCondition;
import com.cpl.model.BallEvent;
import com.cpl.model.Innings;
import com.cpl.model.Match;
import com.cpl.model.Team;
import com.cpl.tournament.Tournament;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

/**
 * Live Match Simulator Panel with scoreboard, wagon wheel, worm graph, and commentary.
 * Demonstrates:
 * - Unit IV: Complex composite layout (BorderLayout, GridLayout), JTextArea, JScrollPane, custom canvas integration
 */
public class LiveMatchPanel extends JPanel implements MatchEngine.MatchListener {
    private static final long serialVersionUID = 1L;

    private final JLabel scoreLabel = new JLabel("0 / 0 (0.0 ov)", SwingConstants.CENTER);
    private final JLabel teamsHeaderLabel = new JLabel("Select Teams to Play T20 Match", SwingConstants.CENTER);
    private final JLabel runRateLabel = new JLabel("CRR: 0.00 | Target: -", SwingConstants.CENTER);
    private final JLabel matchResultLabel = new JLabel("", SwingConstants.CENTER);

    private final WagonWheelCanvas wagonWheel = new WagonWheelCanvas();
    private final RunRateCanvas runRateCanvas = new RunRateCanvas();
    private final JTextArea commentaryArea = new JTextArea(6, 40);

    private final JComboBox<String> homeTeamCombo = new JComboBox<String>();
    private final JComboBox<String> awayTeamCombo = new JComboBox<String>();
    private final JComboBox<PitchCondition> pitchCombo = new JComboBox<PitchCondition>(PitchCondition.values());

    private final JButton startMatchBtn = new JButton("Start Match");
    private final JButton pauseMatchBtn = new JButton("Pause");
    private final JButton simulateSeasonBtn = new JButton("Simulate Full Season (56 Matches)");

    private MatchEngine matchEngine;
    private final Tournament tournament;
    private final List<Team> teams;

    public LiveMatchPanel(Tournament tournament) {
        this.tournament = tournament;
        this.teams = tournament.getTeams();

        setLayout(new BorderLayout(10, 10));
        setBackground(Theme.BG_DARK);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        for (Team t : teams) {
            homeTeamCombo.addItem(t.getName());
            awayTeamCombo.addItem(t.getName());
        }
        if (awayTeamCombo.getItemCount() > 1) {
            awayTeamCombo.setSelectedIndex(1);
        }

        add(buildScoreboardHeader(), BorderLayout.NORTH);
        add(buildCenterCanvases(), BorderLayout.CENTER);
        add(buildBottomControlsAndCommentary(), BorderLayout.SOUTH);

        setupEventHandlers();
    }

    private JPanel buildScoreboardHeader() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 4, 4));
        panel.setBackground(Theme.BG_CARD);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        teamsHeaderLabel.setFont(Theme.FONT_HEADER);
        teamsHeaderLabel.setForeground(Theme.TEXT_LIGHT);

        scoreLabel.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 26));
        scoreLabel.setForeground(Theme.ACCENT_GOLD);

        runRateLabel.setFont(Theme.FONT_SUBHEADER);
        runRateLabel.setForeground(Theme.TEXT_MUTED);

        matchResultLabel.setFont(Theme.FONT_SUBHEADER);
        matchResultLabel.setForeground(Theme.ACCENT_GREEN);

        panel.add(teamsHeaderLabel);
        panel.add(scoreLabel);
        panel.add(runRateLabel);
        panel.add(matchResultLabel);
        return panel;
    }

    private JPanel buildCenterCanvases() {
        JPanel center = new JPanel(new GridLayout(1, 2, 12, 12));
        center.setOpaque(false);
        center.add(wagonWheel);
        center.add(runRateCanvas);
        return center;
    }

    private JPanel buildBottomControlsAndCommentary() {
        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setOpaque(false);

        // Control bar
        JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        controlBar.setBackground(Theme.BG_CARD);

        controlBar.add(new JLabel("Home:"));
        controlBar.add(homeTeamCombo);
        controlBar.add(new JLabel("Away:"));
        controlBar.add(awayTeamCombo);
        controlBar.add(new JLabel("Pitch:"));
        controlBar.add(pitchCombo);

        startMatchBtn.setBackground(Theme.ACCENT_BLUE);
        startMatchBtn.setForeground(Color.WHITE);
        pauseMatchBtn.setBackground(Theme.BG_CARD_LIGHT);
        pauseMatchBtn.setForeground(Color.WHITE);
        pauseMatchBtn.setEnabled(false);

        simulateSeasonBtn.setBackground(Theme.ACCENT_GREEN);
        simulateSeasonBtn.setForeground(Color.WHITE);

        controlBar.add(startMatchBtn);
        controlBar.add(pauseMatchBtn);
        controlBar.add(simulateSeasonBtn);

        // Commentary text box
        commentaryArea.setEditable(false);
        commentaryArea.setBackground(Theme.BG_CARD_LIGHT);
        commentaryArea.setForeground(Theme.TEXT_LIGHT);
        commentaryArea.setFont(Theme.FONT_MONO);
        commentaryArea.setLineWrap(true);
        commentaryArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(commentaryArea);
        scroll.setPreferredSize(new Dimension(800, 100));

        bottom.add(controlBar, BorderLayout.NORTH);
        bottom.add(scroll, BorderLayout.CENTER);
        return bottom;
    }

    private void setupEventHandlers() {
        startMatchBtn.addActionListener(e -> {
            int hIdx = homeTeamCombo.getSelectedIndex();
            int aIdx = awayTeamCombo.getSelectedIndex();
            if (hIdx == aIdx) {
                JOptionPane.showMessageDialog(this, "Home and Away teams must be different!", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Team t1 = teams.get(hIdx);
            Team t2 = teams.get(aIdx);
            PitchCondition pitch = (PitchCondition) pitchCombo.getSelectedItem();

            wagonWheel.clearShots();
            runRateCanvas.clearData();
            commentaryArea.setText("Match commenced! Both teams ready...\n");
            matchResultLabel.setText("");

            startMatchBtn.setEnabled(false);
            pauseMatchBtn.setEnabled(true);

            Match match = new Match(999, 1, com.cpl.model.MatchStage.LEAGUE, t1.getId(), t1.getName(), t2.getId(), t2.getName());
            teamsHeaderLabel.setText(t1.getName() + " vs " + t2.getName());

            matchEngine = new MatchEngine(match, t1, t2, pitch, System.currentTimeMillis());
            matchEngine.setBallDelayMs(60); // 60ms delay for live view animation
            matchEngine.addListener(this);

            Thread matchThread = new Thread(matchEngine, "MatchSimulator-Thread");
            matchThread.start();
        });

        pauseMatchBtn.addActionListener(e -> {
            if ("Pause".equals(pauseMatchBtn.getText())) {
                if (matchEngine != null) matchEngine.pauseSimulation();
                pauseMatchBtn.setText("Resume");
            } else {
                if (matchEngine != null) matchEngine.resumeSimulation();
                pauseMatchBtn.setText("Pause");
            }
        });

        simulateSeasonBtn.addActionListener(e -> {
            simulateSeasonBtn.setEnabled(false);
            Thread seasonThread = new Thread(() -> {
                int count = 0;
                for (Match m : tournament.getFixtures()) {
                    if (m.getStatus() == Match.Status.SCHEDULED) {
                        Team t1 = tournament.getTeam(m.getTeam1Id());
                        Team t2 = tournament.getTeam(m.getTeam2Id());
                        MatchEngine eng = new MatchEngine(m, t1, t2, PitchCondition.BALANCED, System.currentTimeMillis() + count);
                        eng.setBallDelayMs(0); // instant
                        eng.run();
                        tournament.recordCompletedMatch(m);
                        count++;
                    }
                }
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Successfully simulated full season! Check Standings tab.", "Season Finished", JOptionPane.INFORMATION_MESSAGE);
                    simulateSeasonBtn.setEnabled(true);
                });
            }, "SeasonBatch-Thread");
            seasonThread.start();
        });
    }

    // --- MatchListener Callbacks ---

    @Override
    public void onBallBowled(BallEvent event, Innings currentInnings, int inningsNum) {
        SwingUtilities.invokeLater(() -> {
            scoreLabel.setText(String.format("Inn %d: %d / %d (%.1f ov)",
                    inningsNum, currentInnings.getTotalRuns(), currentInnings.getWickets(), currentInnings.getOversCompleted()));
            runRateLabel.setText(String.format("CRR: %.2f | Runs: %d",
                    currentInnings.getCurrentRunRate(), currentInnings.getTotalRuns()));

            wagonWheel.addShot(event);

            int oversCount = (currentInnings.getLegalBalls() + 5) / 6;
            if (inningsNum == 1) {
                runRateCanvas.updateData(currentInnings.getOverRuns(), oversCount, null, 0);
            } else {
                runRateCanvas.updateData(null, 0, currentInnings.getOverRuns(), oversCount);
            }

            commentaryArea.append(event.toString() + "\n");
            commentaryArea.setCaretPosition(commentaryArea.getDocument().getLength());
        });
    }

    @Override
    public void onInningsCompleted(Innings innings, int inningsNum) {
        SwingUtilities.invokeLater(() -> {
            commentaryArea.append(String.format("\n=== INNINGS %d COMPLETED: %d/%d (%.1f Overs) ===\n\n",
                    inningsNum, innings.getTotalRuns(), innings.getWickets(), innings.getOversCompleted()));
        });
    }

    @Override
    public void onMatchCompleted(Match match) {
        SwingUtilities.invokeLater(() -> {
            matchResultLabel.setText(match.getResultSummary() + " | MoM: " + match.getMomPlayerName());
            startMatchBtn.setEnabled(true);
            pauseMatchBtn.setEnabled(false);
            tournament.recordCompletedMatch(match);
        });
    }
}
