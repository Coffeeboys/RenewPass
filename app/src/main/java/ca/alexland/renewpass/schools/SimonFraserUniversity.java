package ca.alexland.renewpass.schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;

import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class SimonFraserUniversity implements School {
    public final String ID = "sfu";

    @Override
    public HtmlDocument login(HtmlDocument authPage, String username, String password) throws SchoolAuthenticationFailedException {
        Form authForm = authPage.form("fm1");

        Text usernameField = (Text) authForm.get("username");
        Password passwordField = (Password) authForm.get("password");

        usernameField.setValue(username);
        passwordField.setValue(password);
        HtmlDocument sfuRedirect = authForm.submit();

        HtmlDocument submittedPage;
        try {
            HtmlDocument translinkRedirect = sfuRedirect.forms().get(0).submit();
            submittedPage = translinkRedirect.forms().get(0).submit();
        }
        catch (Exception e) {
            throw new SchoolAuthenticationFailedException();
        }

        if (submittedPage.getUri().contains("https://upassbc.translink.ca")) {
            return submittedPage;
        } else {
            throw new SchoolAuthenticationFailedException();
        }
    }

    public String getID() {
        return this.ID;
    }
}
