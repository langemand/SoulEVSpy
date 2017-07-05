package org.hexpresso.elm327.exceptions;

/**
 * Thrown when there is a "CAN ERROR" message.
 */
public class CanErrorException extends ResponseException {

    public CanErrorException() {
        super("CAN ERROR");
    }

}
