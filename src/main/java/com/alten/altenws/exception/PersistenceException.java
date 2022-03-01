package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class PersistenceException extends RuntimeException {

    public PersistenceException() {
        super("Could not execute persistence operation");
    }
}
