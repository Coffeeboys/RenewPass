package ca.alexland.renewpass.utils;

import android.content.Context;
import android.os.AsyncTask;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.List;

import ca.alexland.renewpass.exceptions.NothingToRenewException;
import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;
import ca.alexland.renewpass.exceptions.SchoolNotFoundException;
import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;
import ca.alexland.renewpass.schools.BCIT;
import ca.alexland.renewpass.schools.CapilanoUniversity;
import ca.alexland.renewpass.schools.DouglasCollege;
import ca.alexland.renewpass.schools.EmilyCarrUniversity;
import ca.alexland.renewpass.schools.KwantlenUniversity;
import ca.alexland.renewpass.schools.LangaraCollege;
import ca.alexland.renewpass.schools.NVIT;
import ca.alexland.renewpass.schools.School;
import ca.alexland.renewpass.schools.SimonFraserUniversity;
import ca.alexland.renewpass.schools.UniversityOfBritishColumbia;
import ca.alexland.renewpass.schools.VancouverCommunityCollege;

/**
 * Created by AlexLand on 2016-05-09.
 */
public class UPassLoaderSelenium {
    private Callback callback;
    private final String UPASS_SITE_URL = "http://upassbc.translink.ca";
    private Context context;

    public static void renewUPass(Context context, Callback callback) {
        UPassLoaderSelenium mService = new UPassLoaderSelenium();
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        String username = preferenceHelper.getUsername();
        String password = preferenceHelper.getPassword();
        String schoolID = preferenceHelper.getSchool();
        School school = makeNewSchool(schoolID);
        boolean doRenew = true;
        mService.startRenew(doRenew, school, username, password, callback, context);
    }

    private static School makeNewSchool(String schoolID) {
        switch(schoolID) {
            case "BCIT":
                return new BCIT();
            case "Cap U":
                return new CapilanoUniversity();
            case "Douglas":
                return new DouglasCollege();
            case "Emily Carr":
                return new EmilyCarrUniversity();
            case "Kwantlen":
                return new KwantlenUniversity();
            case "Langara":
                return new LangaraCollege();
            case "NVIT":
                return new NVIT();
            case "SFU":
                return new SimonFraserUniversity();
            case "UBC":
                return new UniversityOfBritishColumbia();
            case "VCC":
                return new VancouverCommunityCollege();
            default:
                return null;
        }
    }

    private void startRenew(boolean doRenew, School school, String username, String password, Callback callback, Context context) {
        this.callback = callback;
        this.context = context;
        new RenewTask(school, username, password).execute(doRenew);
    }

    private class RenewTask extends AsyncTask<Boolean, Void, Status> {
        School school;
        String username;
        String password;
        WebDriver webDriver;

        public RenewTask(School school, String username, String password) {
            this.school = school;
            this.username = username;
            this.password = password;
        }

        @Override
        protected ca.alexland.renewpass.model.Status doInBackground(Boolean... params) {
            this.webDriver = new HtmlUnitDriver(true);

            Boolean doRenew = params[0];
            ca.alexland.renewpass.model.Status returnStatus;

            webDriver.get(UPASS_SITE_URL);

            try {
                selectSchool(this.school.getID());
                authorizeAccount();
                WebElement requestCheckbox = checkUpass();
                if (doRenew) {
                    boolean requestSucceeded = requestUpass(requestCheckbox);
                    if (requestSucceeded) {
                        returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.RENEW_SUCCESSFUL, true);
                    }
                    else {
                        returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.RENEW_FAILED, true);
                    }
                }
                else {
                    returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.UPASS_AVAILABLE, true);
                }
            } catch (SchoolNotFoundException e) {
                returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.SCHOOL_NOT_FOUND, true);
            } catch (SchoolAuthenticationFailedException e) {
                returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.AUTHENTICATION_ERROR, true);
            } catch (NoSuchElementException e) {
                returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.NOTHING_TO_RENEW, true);
            }


            return returnStatus;
        }

        private void selectSchool(String schoolId) throws SchoolNotFoundException {
            WebElement schoolDropdown = webDriver.findElement(By.id("PsiId"));
            List<WebElement> schoolOptions = schoolDropdown.findElements(By.tagName("option"));
            boolean foundSchool = false;
            for (WebElement option : schoolOptions) {
                if (option.getCssValue("value").equals(schoolId)) {
                    option.click();
                    option.submit();
                    foundSchool = true;
                    break;
                }
            }
            if (!foundSchool) {
                throw new SchoolNotFoundException();
            }
        }

        private void authorizeAccount() throws SchoolAuthenticationFailedException {
            this.school.login(null, this.username, this.password, context);
        }

        private WebElement checkUpass() throws NoSuchElementException {
            WebElement requestForm = webDriver.findElement(By.id("form-request"));
            WebElement requestCheckbox = requestForm.findElement(By.id("chk_1"));
            return requestCheckbox;
        }

        private boolean requestUpass(WebElement requestCheckbox) {
            List<WebElement> prevRequestedUpasses = webDriver.findElements(By.className("status"));

            requestCheckbox.click();
            requestCheckbox.submit();

            List<WebElement> requestedUpasses = webDriver.findElements(By.className("status"));
            if (requestedUpasses.size() > prevRequestedUpasses.size()) {
                return true;
            }
            else {
                return false;
            }        }

        @Override
        protected void onPostExecute(ca.alexland.renewpass.model.Status result) {
            callback.onUPassLoaded(result);
        }
    }
}
