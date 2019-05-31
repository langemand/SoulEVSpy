package com.evranger.elm327.exceptions;

/**
 * Thrown when there is a "NO DATA" message.
 */
public class NoDataException extends ResponseException {

    public NoDataException() {
        super("NO DATA");
    }

}
