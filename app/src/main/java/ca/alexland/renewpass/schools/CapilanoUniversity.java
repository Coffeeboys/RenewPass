package ca.alexland.renewpass.schools;

import android.content.Context;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;

import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;
import ca.alexland.renewpass.utils.LoggerUtil;

/**
 * Created by AlexLand on 2016-01-25.
 */
public class CapilanoUniversity implements School {
    private final String ID = "cu";

    @Override
    public HtmlDocument login(HtmlDocument authPage, String username, String password, Context context) throws SchoolAuthenticationFailedException {
        Form authForm = authPage.form("aspnetForm");

        Text usernameField = (Text) authForm.get("ctl00$ContentPlaceHolder1$UsernameTextBox");
        Password passwordField = (Password) authForm.get("ctl00$ContentPlaceHolder1$PasswordTextBox");

        usernameField.setValue(username);
        passwordField.setValue(password);
        HtmlDocument sfuRedirect = authForm.submit();

        HtmlDocument submittedPage;
        try {
            HtmlDocument translinkRedirect = sfuRedirect.forms().get(0).submit();
            LoggerUtil.appendLog(context, "translinkRedirect: " + translinkRedirect.getUri());
            submittedPage = translinkRedirect.forms().get(0).submit();
            LoggerUtil.appendLog(context, "submittedPage: " + submittedPage.getUri());
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
        return this.ID;
    }
}
