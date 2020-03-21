package de.techfak.gse.cstiller.exceptions;

/**
 * Is thrown when JSON to Song or to Playlist transformation fails.
 */
public class DeserializationException extends Exception {
    static final long serialVersionUID = 42L;
    public DeserializationException(final String message) {
        super(message);
    }
}
