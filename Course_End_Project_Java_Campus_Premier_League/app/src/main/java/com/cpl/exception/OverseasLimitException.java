package com.cpl.exception;

/**
 * Thrown when a team attempts to bid on an overseas player but has reached the overseas limit.
 * // Unit II: Custom Exception Hierarchy
 */
public class OverseasLimitException extends AuctionException {
    private static final long serialVersionUID = 1L;

    public OverseasLimitException(String teamName, int currentOverseas, int maxOverseas) {
        super(String.format("Team %s has reached the overseas limit (%d / %d overseas players)",
                teamName, currentOverseas, maxOverseas));
    }
}
