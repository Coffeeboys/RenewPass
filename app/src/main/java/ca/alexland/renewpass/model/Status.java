package ca.alexland.renewpass.model;

/**
 * Created by AlexLand on 2016-01-04.
 */
public class Status {
    public static final String SCHOOL_NOT_FOUND = "School not found.";
    public static final String AUTHENTICATION_ERROR = "Authentication failed.";
    public static final String NETWORK_ERROR = "Network error.";
    public static final String NOTHING_TO_RENEW = "You already have the latest UPass!";
    public static final String RENEW_SUCCESSFUL = "UPass successfully requested!";
    public static final String UPASS_AVAILABLE = "New UPass available!";
    public static final String RENEW_FAILED = "Failed to renew UPass.";


    private final String statusText;
    private final boolean successful;

    public Status(String statusText, boolean successful) {
        this.statusText = statusText;
        this.successful = successful;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getStatusText() {
        return statusText;
    }
}
