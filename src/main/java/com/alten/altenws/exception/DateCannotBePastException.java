package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class DateCannotBePastException extends Exception {

    public DateCannotBePastException() {
        super("Nor FROM date nor TO date can be past");
    }
}
