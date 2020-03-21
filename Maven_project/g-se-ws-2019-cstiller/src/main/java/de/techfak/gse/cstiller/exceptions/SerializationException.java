package de.techfak.gse.cstiller.exceptions;

/**
 * Exception to be thrown when the port number is out of the valid port number bounds.
 */
public class SerializationException extends Exception {
    static final long serialVersionUID = 42L;
    public SerializationException(final String message) {
        super(message);
    }
}
