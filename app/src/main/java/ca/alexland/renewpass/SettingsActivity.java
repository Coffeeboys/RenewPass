package ca.alexland.renewpass;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference credits = findPreference("Credits");
            credits.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(getString(R.string.preference_credits));
                    alert.setMessage(Html.fromHtml(getString(R.string.preference_credits_description)));

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                    return true;
                }
            });

            LicenseResolver.registerLicense(new MozillaPublicLicense20());

            Preference licenses = findPreference("Licenses");
            licenses.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.licenses)
                            .build()
                            .show();
                    return true;
                }
            });
        }
    }

    public static class MozillaPublicLicense20 extends License {

        private static final long serialVersionUID = -5912500033007492703L;

        @Override
        public String getName() {
            return "Mozilla Public License 2.0";
        }

        @Override
        public String readSummaryTextFromResources(final Context context) {
            return getContent(context, R.raw.mpl_20_summary);
        }

        @Override
        public String readFullTextFromResources(final Context context) {
            return getContent(context, R.raw.mpl_20_full);
        }

        @Override
        public String getVersion() {
            return "2.0";
        }

        @Override
        public String getUrl() {
            return "http://mozilla.org/MPL/2.0/";
        }

    }

}
