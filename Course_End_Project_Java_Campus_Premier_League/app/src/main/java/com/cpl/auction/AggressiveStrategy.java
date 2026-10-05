package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Team;

/**
 * Aggressive bidding strategy:
 * Targets high-rated marquee players and is willing to bid up to 35% of purse.
 * // Unit I: Implementing interfaces
 */
public class AggressiveStrategy implements BiddingStrategy {

    @Override
    public double evaluateBid(Team team, Player lot, double currentBid) {
        if (!team.canBid(currentBid + 100000.0, lot)) {
            return 0.0;
        }

        double rating = lot.getOverallRating();
        // Aggressive bots strongly contest players with rating > 75
        double maxWillingToPay = lot.getBasePrice() * (rating > 85 ? 4.5 : (rating > 75 ? 2.5 : 1.2));
        double purseCeiling = team.getCurrentPurse() * 0.35;
        double ceiling = Math.min(maxWillingToPay, purseCeiling);

        double nextBid = currentBid <= 0 ? lot.getBasePrice() : currentBid + 100000.0;
        if (nextBid <= ceiling) {
            return nextBid;
        }
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Aggressive Marquee Hunter";
    }
}
