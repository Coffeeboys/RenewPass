package ca.alexland.renewpass.Schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;

import ca.alexland.renewpass.Utils.SchoolAuthenticationFailedException;

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

        HtmlDocument translinkRedirect = sfuRedirect.forms().get(0).submit();
        HtmlDocument submittedPage = translinkRedirect.forms().get(0).submit();

        if(submittedPage.getUri().contains("https://upassbc.translink.ca")) {
            return submittedPage;
        }
        else {
            throw new SchoolAuthenticationFailedException();
        }
    }

    public String getID() {
        return this.ID;
    }
}
