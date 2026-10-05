package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Team;

/**
 * Budget-minded value strategy:
 * Never bids more than 1.5x base price and preserves purse for full squad.
 * // Unit I: Implementing interfaces
 */
public class BudgetMindedStrategy implements BiddingStrategy {

    @Override
    public double evaluateBid(Team team, Player lot, double currentBid) {
        if (!team.canBid(currentBid + 50000.0, lot)) {
            return 0.0;
        }

        double ceiling = lot.getBasePrice() * 1.50;
        double nextBid = currentBid <= 0 ? lot.getBasePrice() : currentBid + 50000.0;
        if (nextBid <= ceiling) {
            return nextBid;
        }
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Budget Value Optimizer";
    }
}
