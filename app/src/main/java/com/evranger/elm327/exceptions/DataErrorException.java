package com.evranger.elm327.exceptions;

/**
 * Thrown when there is a "NO DATA" message.
 */
public class DataErrorException extends ResponseException {

    public DataErrorException() {
        super("DATA ERROR");
    }

}
