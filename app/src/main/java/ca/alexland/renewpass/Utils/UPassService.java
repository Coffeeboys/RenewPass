package ca.alexland.renewpass.Utils;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Checkbox;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Select;
import com.gistlabs.mechanize.impl.MechanizeAgent;

import java.util.List;

import ca.alexland.renewpass.Schools.School;

/**
 * Created by AlexLand on 2015-12-28.
 */
public class UPassService {
    CustomFloatingActionButton fab;
    private final String UPASS_SITE_URL = "http://upassbc.translink.ca";

    public void renewUPass(CustomFloatingActionButton fab, School school, String username, String password) {
        this.fab = fab;
        fab.startLoading();
        new RenewTask(school, username, password).execute(UPASS_SITE_URL);
    }

    private class RenewTask extends AsyncTask<String, Void, Status> {
        School school;
        String username;
        String password;

        public RenewTask(School school, String username, String password) {
            this.school = school;
            this.username = username;
            this.password = password;
        }

        @Override
        protected ca.alexland.renewpass.Utils.Status doInBackground(String... params) {
            try {
                HtmlDocument authPage = selectSchool(UPASS_SITE_URL, school.getID());
                HtmlDocument upassPage = authorizeAccount(authPage);
                requestUpass(upassPage);
            }
            catch(SchoolNotFoundException e) {
                return new ca.alexland.renewpass.Utils.Status("School not found.", false);
            }
            catch(SchoolAuthenticationFailedException e) {
                return new ca.alexland.renewpass.Utils.Status("Authentication failed.", false);
            }
            catch(NothingToRenewException e) {
                return new ca.alexland.renewpass.Utils.Status("You already have the latest UPass!", true);
            }
            catch(Exception e) {
                return new ca.alexland.renewpass.Utils.Status("Unknown error.", false);
            }

            return new ca.alexland.renewpass.Utils.Status("UPass successfully requested!", true);
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
        protected void onPostExecute(ca.alexland.renewpass.Utils.Status result) {
            fab.stopLoading();
            Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_LONG).show();
        }
    }
}
