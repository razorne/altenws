package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class StayCannotBeLongerThan3DaysException extends Exception {

    public StayCannotBeLongerThan3DaysException() {
        super("You cannot book the room for more than 3 days");
    }
}
