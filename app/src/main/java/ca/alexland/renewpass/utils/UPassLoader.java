package ca.alexland.renewpass.utils;

import android.content.Context;
import android.os.AsyncTask;

import com.gistlabs.mechanize.document.html.HtmlDocument;
import com.gistlabs.mechanize.document.html.form.Checkbox;
import com.gistlabs.mechanize.document.html.form.Form;
import com.gistlabs.mechanize.document.html.form.Hidden;
import com.gistlabs.mechanize.document.html.form.Select;
import com.gistlabs.mechanize.document.html.form.SubmitButton;
import com.gistlabs.mechanize.exceptions.MechanizeException;
import com.gistlabs.mechanize.impl.MechanizeAgent;

import java.util.Iterator;
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
import ca.alexland.renewpass.views.LoadingFloatingActionButton;

/**
 * Created by AlexLand on 2015-12-28.
 */
public class UPassLoader {
    private Callback callback;
    private final String UPASS_SITE_URL = "https://upassbc.translink.ca";
    private Context context;

    public static void renewUPass(Context context, Callback callback) {
        UPassLoader mService = new UPassLoader();
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

    public static void checkUPassAvailable(Context context, Callback callback) {
        UPassLoader mService = new UPassLoader();
        PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(context);
        String username = preferenceHelper.getUsername();
        String password = preferenceHelper.getPassword();
        String schoolID = preferenceHelper.getSchool();
        School school = makeNewSchool(schoolID);
        boolean doRenew = false;
        mService.startRenew(doRenew, school, username, password, callback, context);
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

        public RenewTask(School school, String username, String password) {
            this.school = school;
            this.username = username;
            this.password = password;
        }

        @Override
        protected ca.alexland.renewpass.model.Status doInBackground(Boolean... params) {
            Boolean doRenew = params[0];
            ca.alexland.renewpass.model.Status returnStatus;
            try {
                HtmlDocument authPage = selectSchool(UPASS_SITE_URL, school.getID());
                HtmlDocument upassPage = authorizeAccount(authPage);
                checkUpass(upassPage);
                if (doRenew) {
                    boolean requestSuccess = requestUpass(upassPage);
                    if (requestSuccess) {
                        returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.RENEW_SUCCESSFUL, true);
                    }
                    else {
                        returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.RENEW_FAILED, false);
                    }
                }
                else {
                    returnStatus = new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.UPASS_AVAILABLE, true);
                }
            }
            catch(SchoolNotFoundException e) {
                LoggerUtil.appendLogWithStacktrace(context, "School not found: ", e);
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.SCHOOL_NOT_FOUND, false);
            }
            catch(SchoolAuthenticationFailedException e) {
                LoggerUtil.appendLogWithStacktrace(context, "School authentication failed: ", e.getOriginalException());
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.AUTHENTICATION_ERROR, false);
            }
            catch(NothingToRenewException e) {
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.NOTHING_TO_RENEW, true);
            }
            catch(MechanizeException e) {
                LoggerUtil.appendLogWithStacktrace(context, "Mechanize exception: ", e);
                return new ca.alexland.renewpass.model.Status(ca.alexland.renewpass.model.Status.NETWORK_ERROR, false);
            }
            catch(Exception e) {
                LoggerUtil.appendLogWithStacktrace(context, "Unknown exception: ", e);
                return new ca.alexland.renewpass.model.Status(e.getMessage(), false);
            }

            return returnStatus;
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
            return this.school.login(authPage, this.username, this.password, context);
        }

        private boolean checkUpass(HtmlDocument upassPage) throws NothingToRenewException {
            Form requestForm = upassPage.form("form-request");
            Checkbox requestCheckbox = null;
            for (Object element : requestForm) {
                if (element instanceof Checkbox) {
                    requestCheckbox = (Checkbox) element;
                }
            }
            if (requestCheckbox != null) {
                return true;
            }
            else {
                throw new NothingToRenewException();
            }
        }

        private boolean requestUpass(HtmlDocument upassPage) {
            List prevRequestedUpasses = upassPage.findAll(".status");

            Form requestForm = upassPage.form("form-request");
            Checkbox requestCheckbox = requestForm.findCheckbox("input");
            requestCheckbox.check();
            String boxName = requestCheckbox.getName();
            Iterator elementIter = requestForm.iterator();
            while (elementIter.hasNext()) {
                Object element = elementIter.next();
                if (element instanceof Hidden) {
                    Hidden hiddenElement = (Hidden) element;
                    if (boxName.equals(hiddenElement.getName())) {
                        elementIter.remove();
                    }
                }
            }
            SubmitButton requestButton = requestForm.findSubmitButton("input");
            HtmlDocument resultPage = requestButton.submit();

            List requestedUpasses = resultPage.findAll(".status");
            if (requestedUpasses.size() > prevRequestedUpasses.size()) {
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        protected void onPostExecute(ca.alexland.renewpass.model.Status result) {
            callback.onUPassLoaded(result);
        }
    }
}
