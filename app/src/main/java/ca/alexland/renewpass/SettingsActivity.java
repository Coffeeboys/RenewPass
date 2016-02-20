package ca.alexland.renewpass;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.CalendarUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import java.util.Calendar;

import ca.alexland.renewpass.utils.LoggerUtil;
import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceChangeListener {
        private String preferenceKeyNotificationsEnabled;
        private String preferenceKeyNotificationDate;
        private String preferenceKeyNotificationTime;
        private PreferenceHelper preferenceHelper;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceKeyNotificationsEnabled = getString(R.string.preference_key_autorenew_enabled);
            preferenceKeyNotificationDate = getString(R.string.preference_key_autorenew_date);
            preferenceKeyNotificationTime = getString(R.string.preference_key_autorenew_time);

            preferenceHelper = PreferenceHelper.getInstance(getActivity());

            findPreference(getString(R.string.preference_key_username))
                    .setOnPreferenceChangeListener(this);
            findPreference(getString(R.string.preference_key_password))
                    .setOnPreferenceChangeListener(this);
            findPreference(getString(R.string.preference_key_School))
                    .setOnPreferenceChangeListener(this);

            Preference credits = findPreference(getString(R.string.preference_key_credits));
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

            Preference licenses = findPreference(getString(R.string.preference_key_licenses));
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

            Preference debug = findPreference(getString(R.string.preference_key_debug));
            debug.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LoggerUtil.launchSendLogWithAttachment(getActivity());
                    return true;
                }
            });
        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        /**
         * Get the value of a preference AFTER it has changed instead of before (like in OnPreferenceChangedListener)
         */
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(preferenceKeyNotificationsEnabled) ||
                    key.equals(preferenceKeyNotificationDate) ||
                    key.equals(preferenceKeyNotificationTime)) {
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setNextAlarm(getActivity());
                    String textToShow = String.format(getString(R.string.message_autorenew_enabled),
                            preferenceHelper.getNextNotificationDate().get(Calendar.DAY_OF_MONTH),
                            CalendarUtil.convertDateToString(getActivity(), preferenceHelper.getNextNotificationDate()));
                    Snackbar.make(getView(), textToShow, Snackbar.LENGTH_LONG).show();
                } else {
                    AlarmUtil.cancelAlarm(getActivity());
                    Snackbar.make(getView(), getString(R.string.message_autorenew_disabled), Snackbar.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Snackbar.make(getView(), preference.getTitle() + " has been saved", Snackbar.LENGTH_LONG).show();
            return true;
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
