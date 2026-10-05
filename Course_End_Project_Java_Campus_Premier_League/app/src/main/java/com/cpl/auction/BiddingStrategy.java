package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Team;

/**
 * Interface defining autonomous bidding behavior for AI team bots.
 * Demonstrates:
 * - Unit I: Interfaces & Strategy Pattern (Dynamic Method Dispatch)
 */
public interface BiddingStrategy {
    /**
     * Evaluates whether the bot wants to bid on the current lot.
     * @param team The team bot
     * @param lot The player currently on auction
     * @param currentBid The current highest bid amount
     * @return The next bid amount if bidding, or 0.0 to pass/withdraw
     */
    double evaluateBid(Team team, Player lot, double currentBid);

    String getStrategyName();
}
