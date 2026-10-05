package com.cpl.exception;

/**
 * Thrown when a team attempts to bid but already has the maximum allowed squad size.
 * // Unit II: Custom Exception Hierarchy
 */
public class SquadFullException extends AuctionException {
    private static final long serialVersionUID = 1L;

    public SquadFullException(String teamName, int currentSize, int maxSize) {
        super(String.format("Team %s squad is already full (%d / %d players)", teamName, currentSize, maxSize));
    }
}
