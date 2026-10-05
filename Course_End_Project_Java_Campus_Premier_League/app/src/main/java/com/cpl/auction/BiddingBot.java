package com.cpl.auction;

import com.cpl.exception.AuctionClosedException;
import com.cpl.exception.BudgetExceededException;
import com.cpl.exception.OverseasLimitException;
import com.cpl.exception.SquadFullException;
import com.cpl.model.Team;

/**
 * Autonomous AI bot thread competing in the live player auction.
 * Demonstrates:
 * - Unit II: Creating threads (implements Runnable), exception handling
 */
public class BiddingBot implements Runnable {
    private final Team team;
    private final BiddingStrategy strategy;
    private volatile boolean running;
    private AuctionLot currentLot;

    public BiddingBot(Team team, BiddingStrategy strategy) {
        this.team = team;
        this.strategy = strategy;
        this.running = true;
    }

    public void setCurrentLot(AuctionLot lot) {
        this.currentLot = lot;
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (currentLot != null && currentLot.getState() == AuctionLot.State.ACTIVE) {
                    // Decide bid
                    double currentBid = currentLot.getCurrentBid();
                    double desiredBid = strategy.evaluateBid(team, currentLot.getPlayer(), currentBid);

                    if (desiredBid > currentBid) {
                        try {
                            // Introduce realistic human-like deliberation delay (100 - 350 ms)
                            Thread.sleep(120 + (int)(Math.random() * 200));

                            if (currentLot.getState() == AuctionLot.State.ACTIVE) {
                                currentLot.submitBid(team, desiredBid);
                            }
                        } catch (BudgetExceededException | SquadFullException |
                                 OverseasLimitException | AuctionClosedException e) {
                            // Bot respects rule bounds
                        }
                    }
                }
                Thread.sleep(150);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopBot() {
        this.running = false;
    }

    public Team getTeam() {
        return team;
    }

    public BiddingStrategy getStrategy() {
        return strategy;
    }
}
