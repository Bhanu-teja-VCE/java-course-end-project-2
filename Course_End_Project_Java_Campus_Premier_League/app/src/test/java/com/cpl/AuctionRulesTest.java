package com.cpl;

import com.cpl.auction.AuctionLot;
import com.cpl.exception.AuctionClosedException;
import com.cpl.exception.BudgetExceededException;
import com.cpl.exception.OverseasLimitException;
import com.cpl.exception.SquadFullException;
import com.cpl.model.Batter;
import com.cpl.model.Player;
import com.cpl.model.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuctionRulesTest {

    private Team testTeam;
    private Player domesticPlayer;
    private Player overseasPlayer;

    @BeforeEach
    void setUp() {
        testTeam = new Team(1, "Test Knights", "TK", "#000000", 1000000.0, 11, 18, 4); // 10 Lakhs purse
        domesticPlayer = new Batter(101, "Domestic Star", 200000.0, false, 80, 40, 75);
        overseasPlayer = new Batter(102, "Overseas Star", 200000.0, true, 85, 40, 75);
    }

    @Test
    void testValidBidAccepted() throws Exception {
        AuctionLot lot = new AuctionLot(1, domesticPlayer);
        lot.startLot();

        boolean accepted = lot.submitBid(testTeam, 300000.0);
        assertTrue(accepted);
        assertEquals(300000.0, lot.getCurrentBid());
        assertEquals(testTeam, lot.getHighestBidder());
    }

    @Test
    void testBudgetExceededExceptionThrown() {
        AuctionLot lot = new AuctionLot(1, domesticPlayer);
        lot.startLot();

        // Bid 15 Lakhs when purse is only 10 Lakhs
        assertThrows(BudgetExceededException.class, () -> {
            lot.submitBid(testTeam, 1500000.0);
        });
    }

    @Test
    void testSquadFullExceptionThrown() {
        Team smallTeam = new Team(2, "Small Team", "ST", "#111111", 10000000.0, 2, 2, 4); // max 2
        Player p1 = new Batter(1, "P1", 200000.0, false, 75, 40, 70);
        Player p2 = new Batter(2, "P2", 200000.0, false, 75, 40, 70);

        assertDoesNotThrow(() -> smallTeam.addPlayer(p1, 200000.0));
        assertDoesNotThrow(() -> smallTeam.addPlayer(p2, 200000.0));
        assertEquals(2, smallTeam.getSquadSize());

        AuctionLot lot = new AuctionLot(1, domesticPlayer);
        lot.startLot();

        assertThrows(SquadFullException.class, () -> {
            lot.submitBid(smallTeam, 300000.0);
        });
    }

    @Test
    void testOverseasLimitExceptionThrown() {
        Team strictTeam = new Team(3, "Strict Team", "ST", "#222222", 10000000.0, 5, 18, 1); // max 1 overseas
        Player os1 = new Batter(10, "OS 1", 200000.0, true, 80, 40, 75);
        assertDoesNotThrow(() -> strictTeam.addPlayer(os1, 200000.0));
        assertEquals(1, strictTeam.getOverseasCount());

        AuctionLot lot = new AuctionLot(1, overseasPlayer);
        lot.startLot();

        assertThrows(OverseasLimitException.class, () -> {
            lot.submitBid(strictTeam, 300000.0);
        });
    }

    @Test
    void testAuctionClosedExceptionThrown() {
        AuctionLot lot = new AuctionLot(1, domesticPlayer);
        // Lot not started yet (state is WAITING)

        assertThrows(AuctionClosedException.class, () -> {
            lot.submitBid(testTeam, 250000.0);
        });
    }

    @Test
    void testLowerOrEqualBidRejected() throws Exception {
        AuctionLot lot = new AuctionLot(1, domesticPlayer);
        lot.startLot();
        lot.submitBid(testTeam, 300000.0);

        Team otherTeam = new Team(4, "Other Team", "OT", "#333333", 5000000.0);
        // Bid less than or equal to current bid
        boolean accepted = lot.submitBid(otherTeam, 250000.0);
        assertFalse(accepted);
        assertEquals(300000.0, lot.getCurrentBid());
        assertEquals(testTeam, lot.getHighestBidder());
    }
}
