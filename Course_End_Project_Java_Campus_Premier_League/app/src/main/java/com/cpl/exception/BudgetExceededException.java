package com.cpl.exception;

/**
 * Thrown when a team attempts to place a bid exceeding its remaining purse.
 * // Unit II: Custom Exception Hierarchy
 */
public class BudgetExceededException extends AuctionException {
    private static final long serialVersionUID = 1L;

    public BudgetExceededException(String teamName, double bid, double purse) {
        super(String.format("Team %s bid ₹%,.2f but only has ₹%,.2f purse remaining", teamName, bid, purse));
    }
}
