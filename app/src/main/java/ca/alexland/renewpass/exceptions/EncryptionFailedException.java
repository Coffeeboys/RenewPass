package ca.alexland.renewpass.exceptions;

/**
 * Created by AlexLand on 2016-01-13.
 */
public class EncryptionFailedException extends Exception {
    Exception originalException;

    public EncryptionFailedException(Exception originalException) {
        this.originalException = originalException;
    }

    public Exception getOriginalException() {
        return originalException;
    }
}
