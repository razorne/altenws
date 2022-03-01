package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class UsernameCannotBeBlankException extends Exception {

    public UsernameCannotBeBlankException() {
        super("Nor FROM date nor TO date can be past");
    }
}
