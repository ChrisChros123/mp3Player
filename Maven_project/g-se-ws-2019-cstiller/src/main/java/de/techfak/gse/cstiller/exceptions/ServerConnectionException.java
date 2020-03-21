package de.techfak.gse.cstiller.exceptions;

/**
 * Exception to be thrown when the connection to the server fails.
 */
public class ServerConnectionException extends Exception {
    static final long serialVersionUID = 42L;
    public ServerConnectionException(final String message) {
        super(message);
    }
}
