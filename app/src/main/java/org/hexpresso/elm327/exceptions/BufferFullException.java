package org.hexpresso.elm327.exceptions;

/**
 * Thrown when there is a "BUS INIT... ERROR" message
 */
public class BufferFullException extends ResponseException {

    public BufferFullException() {
        super("BUFFER FULL");
    }

}
