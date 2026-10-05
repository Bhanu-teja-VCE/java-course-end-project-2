package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Team;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * High-priority auctioneer thread driving lots, countdowns, and sales.
 * Demonstrates:
 * - Unit II: Extends Thread, Thread.MAX_PRIORITY (10), wait(timeout) & notifyAll(), inter-thread coordination
 * - Unit III: Collections (LinkedList for auction queue, HashSet for unsold players)
 */
public class Auctioneer extends Thread {

    public interface AuctionListener {
        void onLotStarted(AuctionLot lot);
        void onBidReceived(AuctionLot lot, Team team, double amount);
        void onCountdownTick(AuctionLot lot, int secondsRemaining, String callPhrase);
        void onLotClosed(AuctionLot lot, boolean sold);
        void onAuctionFinished(int totalSold, int totalUnsold);
    }

    // Unit III: Collections (LinkedList for FIFO auction queue)
    private final LinkedList<Player> auctionQueue;
    // Unit III: Collections (HashSet for unique unsold pool)
    private final Set<Player> unsoldSet;

    private final List<BiddingBot> botList;
    private final List<AuctionListener> listeners;
    private volatile boolean running;
    private volatile boolean paused;
    private AuctionLot activeLot;
    private int lotCounter;
    private int tickIntervalMs = 700; // configurable speed

    public Auctioneer(List<Player> players, List<BiddingBot> bots) {
        super("Auctioneer-Thread");
        // Unit II: Thread Priorities (High priority for auction master)
        setPriority(Thread.MAX_PRIORITY);

        this.auctionQueue = new LinkedList<Player>(players);
        this.unsoldSet = new HashSet<Player>();
        this.botList = new ArrayList<BiddingBot>(bots);
        this.listeners = new ArrayList<AuctionListener>();
        this.running = true;
        this.paused = false;
        this.lotCounter = 0;
    }

    public void addListener(AuctionListener l) {
        listeners.add(l);
    }

    public void setTickIntervalMs(int ms) {
        this.tickIntervalMs = Math.max(50, ms);
    }

    public void pauseAuction() {
        this.paused = true;
    }

    public synchronized void resumeAuction() {
        this.paused = false;
        notifyAll();
    }

    public void stopAuction() {
        this.running = false;
        interrupt();
    }

    @Override
    public void run() {
        int totalSold = 0;
        int totalUnsold = 0;

        while (running && !auctionQueue.isEmpty()) {
            synchronized (this) {
                while (paused && running) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }

            // Pop next player from LinkedList
            Player currentCandidate = auctionQueue.poll();
            if (currentCandidate == null) break;

            lotCounter++;
            activeLot = new AuctionLot(lotCounter, currentCandidate);

            // Inform bots of new active lot
            for (BiddingBot bot : botList) {
                bot.setCurrentLot(activeLot);
            }

            activeLot.startLot();
            for (AuctionListener l : listeners) {
                l.onLotStarted(activeLot);
            }

            // Countdown loop ("Going once, going twice, sold")
            double lastSeenBid = activeLot.getCurrentBid();
            while (running) {
                synchronized (this) {
                    while (paused && running) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            return;
                        }
                    }
                }

                synchronized (activeLot) {
                    // Check if new bid arrived
                    if (activeLot.getCurrentBid() > lastSeenBid) {
                        lastSeenBid = activeLot.getCurrentBid();
                        for (AuctionListener l : listeners) {
                            l.onBidReceived(activeLot, activeLot.getHighestBidder(), lastSeenBid);
                        }
                    }

                    // Unit II: wait with timeout on monitor
                    try {
                        activeLot.wait(tickIntervalMs);
                    } catch (InterruptedException e) {
                        return;
                    }

                    boolean timerExpired = activeLot.tickCountdown();
                    int secs = activeLot.getCountdownSeconds();
                    String callPhrase = secs == 2 ? "Going once..." :
                                        (secs == 1 ? "Going twice..." : "Final call!");

                    for (AuctionListener l : listeners) {
                        l.onCountdownTick(activeLot, secs, callPhrase);
                    }

                    if (timerExpired) {
                        activeLot.resolveLot();
                        break;
                    }
                }
            }

            // Resolve sale
            boolean sold = (activeLot.getState() == AuctionLot.State.SOLD);
            if (sold) {
                Team winningTeam = activeLot.getHighestBidder();
                try {
                    winningTeam.addPlayer(currentCandidate, activeLot.getCurrentBid());
                    totalSold++;
                } catch (Exception e) {
                    sold = false;
                }
            }

            if (!sold) {
                unsoldSet.add(currentCandidate);
                totalUnsold++;
            }

            for (AuctionListener l : listeners) {
                l.onLotClosed(activeLot, sold);
            }

            try {
                Thread.sleep(tickIntervalMs);
            } catch (InterruptedException e) {
                break;
            }
        }

        // Inform listeners completion
        for (AuctionListener l : listeners) {
            l.onAuctionFinished(totalSold, totalUnsold);
        }
    }

    public AuctionLot getActiveLot() {
        return activeLot;
    }

    public int getRemainingLotsCount() {
        return auctionQueue.size();
    }

    public Set<Player> getUnsoldSet() {
        return unsoldSet;
    }
}
