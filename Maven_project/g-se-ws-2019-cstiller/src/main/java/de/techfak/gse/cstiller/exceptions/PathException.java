package de.techfak.gse.cstiller.exceptions;

/**
 * Exception thrown when the directory of a path does not contain any MP3-files or if the path is invalid.
 */
public class PathException extends Exception {
    static final long serialVersionUID = 42L;
    public PathException(final String message) {
        super(message);
    }
}
