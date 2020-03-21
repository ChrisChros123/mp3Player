package de.techfak.gse.cstiller.exceptions;

public class StreamInitializationException extends Exception {
    static final long serialVersionUID = 42L;
    public StreamInitializationException(final String message) {
        super(message);
    }
}
