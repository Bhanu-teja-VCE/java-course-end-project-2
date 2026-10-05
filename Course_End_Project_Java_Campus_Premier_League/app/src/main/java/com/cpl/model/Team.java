package com.cpl.model;

import com.cpl.exception.BudgetExceededException;
import com.cpl.exception.OverseasLimitException;
import com.cpl.exception.SquadFullException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a franchise team in the Campus Premier League.
 * Demonstrates:
 * - Unit I: Encapsulation, private fields with getters/setters, this keyword
 * - Unit II: Thread synchronization on critical methods
 * - Unit III: Collections (ArrayList) & Serialization
 */
public class Team implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;
    private final String shortName;
    private final String colorHex;
    private final double initialPurse;
    private double currentPurse;
    private final int minSquad;
    private final int maxSquad;
    private final int maxOverseas;

    // Unit III: Collections (ArrayList)
    private final List<Player> squad;

    public Team(int id, String name, String shortName, String colorHex, double initialPurse) {
        this(id, name, shortName, colorHex, initialPurse, 11, 18, 4);
    }

    public Team(int id, String name, String shortName, String colorHex,
                double initialPurse, int minSquad, int maxSquad, int maxOverseas) {
        this.id = id;
        this.name = name;
        this.shortName = shortName;
        this.colorHex = colorHex;
        this.initialPurse = initialPurse;
        this.currentPurse = initialPurse;
        this.minSquad = minSquad;
        this.maxSquad = maxSquad;
        this.maxOverseas = maxOverseas;
        this.squad = new ArrayList<Player>();
    }

    // Unit II: Synchronized method for thread-safe bidding verification
    public synchronized boolean canBid(double amount, Player player) {
        if (amount > currentPurse) {
            return false;
        }
        if (squad.size() >= maxSquad) {
            return false;
        }
        if (player.isOverseas() && getOverseasCount() >= maxOverseas) {
            return false;
        }
        // Ensure team retains enough purse to fill the minimum squad at least at base prices
        int spotsRemaining = minSquad - (squad.size() + 1);
        if (spotsRemaining > 0) {
            double minimumReserveNeeded = spotsRemaining * 200000.0;
            if ((currentPurse - amount) < minimumReserveNeeded) {
                return false;
            }
        }
        return true;
    }

    // Unit II: Synchronized method for thread-safe player addition
    public synchronized void addPlayer(Player player, double finalPrice)
            throws BudgetExceededException, SquadFullException, OverseasLimitException {
        if (finalPrice > currentPurse) {
            throw new BudgetExceededException(name, finalPrice, currentPurse);
        }
        if (squad.size() >= maxSquad) {
            throw new SquadFullException(name, squad.size(), maxSquad);
        }
        if (player.isOverseas() && getOverseasCount() >= maxOverseas) {
            throw new OverseasLimitException(name, getOverseasCount(), maxOverseas);
        }

        player.setSoldPrice(finalPrice);
        player.setTeamId(id);
        squad.add(player);
        currentPurse -= finalPrice;
    }

    public synchronized int getOverseasCount() {
        int count = 0;
        for (Player p : squad) {
            if (p.isOverseas()) {
                count++;
            }
        }
        return count;
    }

    public synchronized int getRoleCount(Role role) {
        int count = 0;
        for (Player p : squad) {
            if (p.getRole() == role) {
                count++;
            }
        }
        return count;
    }

    public synchronized double getAverageRating() {
        if (squad.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (Player p : squad) {
            sum += p.getOverallRating();
        }
        return sum / squad.size();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getColorHex() {
        return colorHex;
    }

    public double getInitialPurse() {
        return initialPurse;
    }

    public synchronized double getCurrentPurse() {
        return currentPurse;
    }

    public synchronized void setCurrentPurse(double currentPurse) {
        this.currentPurse = currentPurse;
    }

    public int getMinSquad() {
        return minSquad;
    }

    public int getMaxSquad() {
        return maxSquad;
    }

    public int getMaxOverseas() {
        return maxOverseas;
    }

    public synchronized List<Player> getSquad() {
        return Collections.unmodifiableList(new ArrayList<Player>(squad));
    }

    public synchronized int getSquadSize() {
        return squad.size();
    }

    @Override
    public String toString() {
        return String.format("%s (%s) - Purse: ₹%,.2f, Squad: %d/%d",
                name, shortName, currentPurse, squad.size(), maxSquad);
    }
}
