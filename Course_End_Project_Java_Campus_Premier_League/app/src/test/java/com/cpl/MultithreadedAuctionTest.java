package com.cpl;

import com.cpl.auction.AggressiveStrategy;
import com.cpl.auction.AuctionLot;
import com.cpl.auction.Auctioneer;
import com.cpl.auction.BiddingBot;
import com.cpl.auction.BudgetMindedStrategy;
import com.cpl.auction.NeedsBasedStrategy;
import com.cpl.auction.RandomStrategy;
import com.cpl.engine.SafetyChecker;
import com.cpl.model.Batter;
import com.cpl.model.Player;
import com.cpl.model.Team;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MultithreadedAuctionTest {

    @Test
    void testConcurrentBiddingBots() throws InterruptedException {
        // Setup 4 teams with bots
        List<Team> teams = new ArrayList<Team>();
        teams.add(new Team(1, "Alpha", "ALP", "#111111", 10000000.0));
        teams.add(new Team(2, "Beta", "BET", "#222222", 10000000.0));
        teams.add(new Team(3, "Gamma", "GAM", "#333333", 10000000.0));
        teams.add(new Team(4, "Delta", "DEL", "#444444", 10000000.0));

        List<BiddingBot> bots = new ArrayList<BiddingBot>();
        bots.add(new BiddingBot(teams.get(0), new AggressiveStrategy()));
        bots.add(new BiddingBot(teams.get(1), new BudgetMindedStrategy()));
        bots.add(new BiddingBot(teams.get(2), new NeedsBasedStrategy()));
        bots.add(new BiddingBot(teams.get(3), new RandomStrategy()));

        List<Player> auctionList = new ArrayList<Player>();
        for (int i = 1; i <= 6; i++) {
            auctionList.add(new Batter(i, "Star " + i, 200000.0, (i % 2 == 0), 82 + i, 40, 80));
        }

        // Start bot threads
        List<Thread> botThreads = new ArrayList<Thread>();
        for (BiddingBot bot : bots) {
            Thread t = new Thread(bot, "TestBot-" + bot.getTeam().getShortName());
            botThreads.add(t);
            t.start();
        }

        Auctioneer auctioneer = new Auctioneer(auctionList, bots);
        auctioneer.setTickIntervalMs(60); // Fast ticks for test
        auctioneer.start();

        // Wait for auctioneer to finish all lots
        auctioneer.join(8000);
        assertFalse(auctioneer.isAlive(), "Auctioneer should conclude within timeout");

        // Stop bots
        for (BiddingBot b : bots) {
            b.stopBot();
        }
        for (Thread t : botThreads) {
            t.interrupt();
            t.join(1000);
        }

        // Verify Invariants using SafetyChecker
        assertDoesNotThrow(() -> {
            SafetyChecker.verifyAuctionInvariants(teams, auctionList);
        });

        int totalAcquired = 0;
        for (Team t : teams) {
            totalAcquired += t.getSquadSize();
            assertTrue(t.getCurrentPurse() <= t.getInitialPurse());
            assertTrue(t.getCurrentPurse() >= 0.0);
        }
        assertTrue(totalAcquired > 0, "At least some players should have been acquired by competing bots");
    }

    @Test
    void testAuctioneerTimerResetOnBid() throws Exception {
        Player p = new Batter(50, "Timer Check Player", 200000.0, false, 85, 30, 80);
        AuctionLot lot = new AuctionLot(1, p);
        lot.startLot();

        Team t1 = new Team(1, "T1", "T1", "#000", 5000000.0);
        Team t2 = new Team(2, "T2", "T2", "#111", 5000000.0);

        lot.tickCountdown(); // countdown becomes 2
        assertEquals(2, lot.getCountdownSeconds());

        // Bid arrives: resets countdown to 3
        lot.submitBid(t1, 300000.0);
        assertEquals(3, lot.getCountdownSeconds());
        assertEquals(300000.0, lot.getCurrentBid());

        lot.tickCountdown(); // countdown becomes 2
        lot.submitBid(t2, 400000.0);
        assertEquals(3, lot.getCountdownSeconds());
    }
}
