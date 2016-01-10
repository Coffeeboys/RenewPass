package ca.alexland.renewpass.Model;

/**
 * Created by AlexLand on 2016-01-04.
 */
public class Status {
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
