package com.cpl.auction;

import com.cpl.exception.AuctionClosedException;
import com.cpl.exception.BudgetExceededException;
import com.cpl.exception.OverseasLimitException;
import com.cpl.exception.SquadFullException;
import com.cpl.model.Player;
import com.cpl.model.Team;

/**
 * Encapsulates the shared monitor for a single player auction lot.
 * Demonstrates:
 * - Unit II: Synchronization, monitor locks, wait(), notifyAll()
 */
public class AuctionLot {
    public enum State { WAITING, ACTIVE, SOLD, UNSOLD }

    private final int lotNumber;
    private final Player player;
    private State state;
    private double currentBid;
    private Team highestBidder;
    private int countdownSeconds;
    private boolean timerResetRequested;

    public AuctionLot(int lotNumber, Player player) {
        this.lotNumber = lotNumber;
        this.player = player;
        this.state = State.WAITING;
        this.currentBid = 0.0;
        this.highestBidder = null;
        this.countdownSeconds = 3;
        this.timerResetRequested = false;
    }

    // Unit II: Synchronized critical section with wait/notifyAll
    public synchronized boolean submitBid(Team team, double amount)
            throws AuctionClosedException, BudgetExceededException, SquadFullException, OverseasLimitException {
        if (state != State.ACTIVE) {
            throw new AuctionClosedException(String.format("Lot #%d for %s is closed", lotNumber, player.getName()));
        }
        if (amount <= currentBid && currentBid > 0) {
            return false;
        }
        if (amount < player.getBasePrice()) {
            return false;
        }
        if (highestBidder != null && highestBidder.getId() == team.getId()) {
            return false; // Already highest bidder
        }
        if (amount > team.getCurrentPurse()) {
            throw new BudgetExceededException(team.getName(), amount, team.getCurrentPurse());
        }
        if (team.getSquadSize() >= team.getMaxSquad()) {
            throw new SquadFullException(team.getName(), team.getSquadSize(), team.getMaxSquad());
        }
        if (player.isOverseas() && team.getOverseasCount() >= team.getMaxOverseas()) {
            throw new OverseasLimitException(team.getName(), team.getOverseasCount(), team.getMaxOverseas());
        }

        this.currentBid = amount;
        this.highestBidder = team;
        this.countdownSeconds = 3; // Reset "going once, twice, sold" timer
        this.timerResetRequested = true;

        // Unit II: notifyAll() wakes up the auctioneer countdown thread
        notifyAll();
        return true;
    }

    public synchronized void startLot() {
        this.state = State.ACTIVE;
        this.currentBid = player.getBasePrice();
        this.countdownSeconds = 3;
        notifyAll();
    }

    public synchronized boolean tickCountdown() {
        if (timerResetRequested) {
            timerResetRequested = false;
            return false;
        }
        countdownSeconds--;
        return countdownSeconds <= 0;
    }

    public synchronized void resolveLot() {
        if (highestBidder != null) {
            state = State.SOLD;
        } else {
            state = State.UNSOLD;
        }
        notifyAll();
    }

    public int getLotNumber() {
        return lotNumber;
    }

    public Player getPlayer() {
        return player;
    }

    public synchronized State getState() {
        return state;
    }

    public synchronized double getCurrentBid() {
        return currentBid;
    }

    public synchronized Team getHighestBidder() {
        return highestBidder;
    }

    public synchronized int getCountdownSeconds() {
        return countdownSeconds;
    }
}
