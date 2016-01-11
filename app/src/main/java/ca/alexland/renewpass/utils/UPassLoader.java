package ca.alexland.renewpass.utils;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Checkbox;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Select;
import com.gistlabs.mechanize.exceptions.MechanizeException;
import com.gistlabs.mechanize.impl.MechanizeAgent;

import java.util.List;

import ca.alexland.renewpass.exceptions.NothingToRenewException;
import ca.alexland.renewpass.exceptions.SchoolAuthenticationFailedException;
import ca.alexland.renewpass.exceptions.SchoolNotFoundException;
import ca.alexland.renewpass.model.Status;
import ca.alexland.renewpass.schools.School;
import ca.alexland.renewpass.views.LoadingFloatingActionButton;

/**
 * Created by AlexLand on 2015-12-28.
 */
public class UPassLoader {
    LoadingFloatingActionButton fab;
    private final String UPASS_SITE_URL = "http://upassbc.translink.ca";

    public void renewUPass(LoadingFloatingActionButton fab, School school, String username, String password) {
        this.fab = fab;
        fab.startLoading();
        startRenew(school, username, password);
    }

    private void startRenew(School school, String username, String password) {
        new RenewTask(school, username, password).execute(UPASS_SITE_URL);
    }

    private class RenewTask extends AsyncTask<String, Void, Status> {
        private final int MESSAGE_DURATION = 5000;
        School school;
        String username;
        String password;

        public RenewTask(School school, String username, String password) {
            this.school = school;
            this.username = username;
            this.password = password;
        }

        @Override
        protected ca.alexland.renewpass.model.Status doInBackground(String... params) {
            try {
                HtmlDocument authPage = selectSchool(UPASS_SITE_URL, school.getID());
                HtmlDocument upassPage = authorizeAccount(authPage);
                requestUpass(upassPage);
            }
            catch(SchoolNotFoundException e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.SCHOOL_NOT_FOUND, false);
            }
            catch(SchoolAuthenticationFailedException e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.AUTHENTICATION_ERROR, false);
            }
            catch(NothingToRenewException e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.NOTHING_TO_RENEW, true);
            }
            catch(MechanizeException e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.NETWORK_ERROR, false);
            }
            catch(Exception e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.UNKNOWN_ERROR, false);
            }

            return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.RENEW_SUCCESSFUL, true);
        }

        private HtmlDocument selectSchool(String siteURL, String schoolId) throws SchoolNotFoundException {
            MechanizeAgent agent = new MechanizeAgent();
            HtmlDocument page = agent.get(siteURL);
            Form schoolSelectionForm = page.forms().get(0);
            Select schoolDropdown = (Select) schoolSelectionForm.get("PsiId");
            List<Select.Option> schools = schoolDropdown.getOptions();
            Select.Option schoolOption = null;
            for(Select.Option school : schools) {
                if (school.getValue().equals(schoolId)) {
                    schoolOption = school;
                }
            }
            if (schoolOption != null) {
                schoolOption.setSelected(true);
            }
            else {
                throw new SchoolNotFoundException();
            }
            return schoolSelectionForm.submit();
        }

        private HtmlDocument authorizeAccount(HtmlDocument authPage) throws SchoolAuthenticationFailedException {
            return this.school.login(authPage, this.username, this.password);
        }

        private void requestUpass(HtmlDocument upassPage) throws NothingToRenewException {
            Form requestForm = upassPage.form("form-request");
            Checkbox requestCheckbox = requestForm.findCheckbox("Selected");
            if (requestCheckbox != null) {
                requestCheckbox.check();
                HtmlDocument resultPage = requestForm.submit();
                // TODO: Check table for successful request
            }
            else {
                throw new NothingToRenewException();
            }
        }

        @Override
        protected void onPostExecute(ca.alexland.renewpass.model.Status result) {
            if (result.isSuccessful()) {
                fab.finishSuccess();
            }
            else {
                fab.finishFailure();
            }
            if (result.getStatusText().equals(ca.alexland.renewpass.model.Status.NETWORK_ERROR)) {
                Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startRenew(school, username, password);
                            }
                        })
                        .show();
            }
            else {
                Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_LONG).show();
            }

        }
    }
}
