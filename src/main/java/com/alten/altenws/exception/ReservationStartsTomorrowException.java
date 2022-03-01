package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class ReservationStartsTomorrowException extends Exception {

    public ReservationStartsTomorrowException() {
        super("You cannot book the room today. Try to select tomorrow as the first day of your stay.");
    }
}
