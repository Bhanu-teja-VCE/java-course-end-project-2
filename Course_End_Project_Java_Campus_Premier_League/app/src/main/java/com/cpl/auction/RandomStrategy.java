package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Team;

import java.util.Random;

/**
 * Stochastic wildcard campus strategy:
 * Simulates unpredictable bidding psychology.
 * // Unit I: Implementing interfaces
 */
public class RandomStrategy implements BiddingStrategy {
    private final Random random = new Random();

    @Override
    public double evaluateBid(Team team, Player lot, double currentBid) {
        if (!team.canBid(currentBid + 50000.0, lot)) {
            return 0.0;
        }

        // 40% probability of bidding on any legal lot up to 2x base price
        if (random.nextDouble() < 0.40) {
            double ceiling = lot.getBasePrice() * (1.2 + random.nextDouble() * 0.8);
            double nextBid = currentBid <= 0 ? lot.getBasePrice() : currentBid + 50000.0;
            if (nextBid <= ceiling) {
                return nextBid;
            }
        }
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Wildcard Opportunist";
    }
}
