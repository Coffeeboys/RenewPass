package ca.alexland.renewpass.exceptions;

/**
 * Created by AlexLand on 2016-01-13.
 */
public class DecryptionFailedException extends Exception {
    Exception originalException;

    public DecryptionFailedException(Exception originalException) {
        this.originalException = originalException;
    }

    public Exception getOriginalException() {
        return originalException;
    }
}
