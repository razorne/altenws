package com.alten.altenws.exception;

/**
 *
 * @author aconti
 */
public class FromDateAndToDateCannotBeEqualException extends Exception {

    public FromDateAndToDateCannotBeEqualException() {
        super("FROM date and TO date cannot be equal");
    }
}
