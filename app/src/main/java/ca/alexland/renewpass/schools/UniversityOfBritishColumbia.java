package ca.alexland.renewpass.schools;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;

import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;

/**
 * Created by AlexLand on 2016-01-14.
 */
public class UniversityOfBritishColumbia implements School {
    public final String ID = "ubc";

    @Override
    public HtmlDocument login(HtmlDocument authPage, String username, String password) throws SchoolAuthenticationFailedException {
        Form authForm = authPage.form("loginForm");

        Text usernameField = (Text) authForm.get("j_username");
        Password passwordField = (Password) authForm.get("password");

        usernameField.setValue(username);
        passwordField.setValue(password);
        HtmlDocument ubcRedirect = authForm.submit();

        HtmlDocument submittedPage;
        try {
            HtmlDocument translinkRedirect = ubcRedirect.forms().get(0).submit();
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

    @Override
    public String getID() {
        return ID;
    }
}
