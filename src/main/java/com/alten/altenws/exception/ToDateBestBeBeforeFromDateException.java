package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class ToDateBestBeBeforeFromDateException extends Exception {

    public ToDateBestBeBeforeFromDateException() {
        super("FROM date must be before TO date");
    }
}
