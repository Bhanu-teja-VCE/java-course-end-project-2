package com.cpl;

import com.cpl.model.AllRounder;
import com.cpl.model.Batter;
import com.cpl.model.Bowler;
import com.cpl.model.Player;
import com.cpl.model.Role;
import com.cpl.model.WicketKeeper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerModelTest {

    @BeforeEach
    void setup() {
        Player.resetIdSequence();
    }

    @Test
    void testBatterPropertiesAndImpact() {
        Batter batter = new Batter("Rohit Sharma", 1000000.0, false, 90, 40, 85);
        assertEquals(Role.BATTER, batter.getRole());
        assertEquals("Rohit Sharma", batter.getName());
        assertEquals(1000000.0, batter.getBasePrice());
        assertFalse(batter.isOverseas());
        // Impact = 90*0.85 + 85*0.15 = 76.5 + 12.75 = 89.25
        assertEquals(89.25, batter.calculateImpactScore(), 0.01);
        assertTrue(batter.getOverallRating() > 80.0);
    }

    @Test
    void testBowlerPropertiesAndImpact() {
        Bowler bowler = new Bowler("Jasprit Bumrah", 1000000.0, false, 30, 95, 80);
        assertEquals(Role.BOWLER, bowler.getRole());
        // Impact = 95*0.85 + 80*0.15 = 80.75 + 12.0 = 92.75
        assertEquals(92.75, bowler.calculateImpactScore(), 0.01);
        assertTrue(bowler.getSpecialtyDescription().contains("Wicket-taker"));
    }

    @Test
    void testAllRounderBalancedRating() {
        AllRounder allRounder = new AllRounder("Hardik Pandya", 900000.0, false, 85, 85, 85);
        assertEquals(Role.ALL_ROUNDER, allRounder.getRole());
        assertEquals(85.0, allRounder.calculateImpactScore(), 0.01);
        assertEquals(85.0, allRounder.getOverallRating(), 0.01);
    }

    @Test
    void testWicketKeeperImpact() {
        WicketKeeper keeper = new WicketKeeper("MS Dhoni", 1000000.0, false, 88, 20, 95);
        assertEquals(Role.WICKET_KEEPER, keeper.getRole());
        // Impact = 88*0.70 + 95*0.30 = 61.6 + 28.5 = 90.1
        assertEquals(90.1, keeper.calculateImpactScore(), 0.01);
    }

    @Test
    void testOverseasFlag() {
        Batter overseasBatter = new Batter("Travis Head", 1000000.0, true, 92, 60, 80);
        assertTrue(overseasBatter.isOverseas());
        assertNull(overseasBatter.getTeamId());
        assertFalse(overseasBatter.isSold());
    }

    @Test
    void testConstructorOverloading() {
        // Constructor 1: auto ID
        Batter p1 = new Batter("Player 1", 500000.0, false, 80, 50, 75);
        assertEquals(1001, p1.getId());

        // Constructor 2: explicit ID
        Batter p2 = new Batter(5555, "Player Explicit", 500000.0, false, 80, 50, 75);
        assertEquals(5555, p2.getId());
    }

    @Test
    void testDynamicMethodDispatchPolymorphism() {
        Player[] players = new Player[]{
            new Batter("B1", 200000.0, false, 85, 30, 80),
            new Bowler("B2", 200000.0, false, 25, 88, 75),
            new AllRounder("B3", 200000.0, false, 75, 75, 75),
            new WicketKeeper("B4", 200000.0, false, 80, 20, 85)
        };

        for (Player p : players) {
            assertTrue(p.calculateImpactScore() > 0.0);
            assertNotNull(p.getSpecialtyDescription());
        }
    }
}
