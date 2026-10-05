package com.cpl.auction;

import com.cpl.model.Player;
import com.cpl.model.Role;
import com.cpl.model.Team;

/**
 * Needs-based tactical strategy:
 * Evaluates team role deficits (needs 5 batters, 5 bowlers, 2 keepers, 3 all-rounders).
 * Bids high on roles the team is currently lacking.
 * // Unit I: Implementing interfaces
 */
public class NeedsBasedStrategy implements BiddingStrategy {

    @Override
    public double evaluateBid(Team team, Player lot, double currentBid) {
        if (!team.canBid(currentBid + 100000.0, lot)) {
            return 0.0;
        }

        Role role = lot.getRole();
        int currentCount = team.getRoleCount(role);

        // Desired quota thresholds
        int target = 4;
        if (role == Role.BATTER || role == Role.BOWLER) target = 5;
        if (role == Role.WICKET_KEEPER) target = 2;

        if (currentCount >= target) {
            // Role already fulfilled, only bid if exceptional steal
            return 0.0;
        }

        // Urgency factor
        double multiplier = 1.0 + (target - currentCount) * 0.5;
        double ceiling = lot.getBasePrice() * multiplier;

        double nextBid = currentBid <= 0 ? lot.getBasePrice() : currentBid + 100000.0;
        if (nextBid <= ceiling) {
            return nextBid;
        }
        return 0.0;
    }

    @Override
    public String getStrategyName() {
        return "Tactical Needs-Based";
    }
}
