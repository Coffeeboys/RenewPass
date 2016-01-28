package ca.alexland.renewpass.exceptions;

/**
 * Created by AlexLand on 2016-01-04.
 */
public class SchoolAuthenticationFailedException extends Exception {
    Exception originalException;

    public SchoolAuthenticationFailedException(Exception originalException) {
        this.originalException = originalException;
    }

    public Exception getOriginalException() {
        return originalException;
    }
}
