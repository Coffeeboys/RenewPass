package ca.alexland.renewpass.schools;

import android.content.Context;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Password;
import com.gistlabs.mechanize.document.html.form.Text;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;
import ca.alexland.renewpass.utils.LoggerUtil;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class SimonFraserUniversity implements School {
    public final String ID = "sfu";

    @Override
    public void login(WebDriver webDriver, String username, String password, Context context) throws SchoolAuthenticationFailedException {
//        Form authForm = authPage.form("fm1");
//
//        Text usernameField = (Text) authForm.get("username");
//        Password passwordField = (Password) authForm.get("password");
//
//        usernameField.setValue(username);
//        passwordField.setValue(password);
//        HtmlDocument sfuRedirect = authForm.submit();
//
//        HtmlDocument submittedPage;
//        try {
//            HtmlDocument translinkRedirect = sfuRedirect.forms().get(0).submit();
//            LoggerUtil.appendLog(context, "translinkRedirect: " + translinkRedirect.getUri());
//            submittedPage = translinkRedirect.forms().get(0).submit();
//            LoggerUtil.appendLog(context, "submittedPage: " + submittedPage.getUri());
//        }
//        catch (Exception e) {
//            throw new SchoolAuthenticationFailedException(e);
//        }
//
//        if (submittedPage.getUri().contains("https://upassbc.translink.ca")) {
//            return submittedPage;
//        } else {
//            throw new SchoolAuthenticationFailedException(new Exception("Invalid submitted page URI"));
//        }
        // TODO: add logs
        WebElement usernameField = webDriver.findElement(By.id("username"));
        WebElement passwordField = webDriver.findElement(By.id("password"));

        usernameField.sendKeys(username);
        passwordField.sendKeys(password);
        passwordField.submit();

        webDriver.findElement(By.tagName("form")).submit();
        webDriver.findElement(By.tagName("form")).submit();

        if (!webDriver.getCurrentUrl().contains("https://upassbc.translink.ca")) {
            // TODO: refactor SchoolAuthenticationFailedException to just take a string
            throw new SchoolAuthenticationFailedException(new Exception("Invalid submitted page URI"));
        }
    }

    public String getID() {
        return this.ID;
    }
}
