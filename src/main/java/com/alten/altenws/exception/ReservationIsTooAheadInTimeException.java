package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class ReservationIsTooAheadInTimeException extends Exception {

    public ReservationIsTooAheadInTimeException() {
        super("You have picked a date that is too ahead in time (> 30 days)");
    }
}
