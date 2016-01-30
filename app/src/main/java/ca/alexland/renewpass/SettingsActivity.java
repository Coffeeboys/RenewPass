package ca.alexland.renewpass;

import android.content.SharedPreferences;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.Window;
import android.view.WindowManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;

import java.util.Calendar;

import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

import ca.alexland.renewpass.utils.LoggerUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;
import de.psdev.licensesdialog.LicenseResolver;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.License;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();

    }


    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(PreferenceHelper.NOTIFICATION_DATE_PREFERENCE)) {
                PreferenceHelper pHelper = PreferenceHelper.getInstance(getActivity());
                String textToShow = "You will be updated on the " +
                        pHelper.getDate().get(Calendar.DAY_OF_MONTH)
                        + "th of every month";
                Snackbar.make(getView(), textToShow, Snackbar.LENGTH_SHORT).show();
                // AlarmUtil.setAlarm(getActivity(), false); TODO
                // Do we need to call setAlarm here??
            } else if (key.equals(PreferenceHelper.NOTIFICATIONS_ENABLED_PREFERENCE)) {
                PreferenceHelper pHelper = PreferenceHelper.getInstance(getActivity());
                if (pHelper.getNotificationsEnabled()) {
                    String textToShow = "You will be updated on the " +
                            pHelper.getDate().get(Calendar.DAY_OF_MONTH)
                            + "th of every month";
                    Snackbar.make(getView(), textToShow, Snackbar.LENGTH_SHORT).show();
                    // AlarmUtil.setAlarm(getActivity(), false); TODO
                    // Do we need to call setAlarm here??
                } else {
                    Snackbar.make(getView(), getString(R.string.notifications_disabled), Snackbar.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceManager().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onPause() {
            getPreferenceManager().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
            super.onPause();

            Preference username = findPreference(PreferenceHelper.USERNAME_PREFERENCE);
            username.setOnPreferenceChangeListener(makeNotifier());

            Preference password = findPreference(PreferenceHelper.PASSWORD_PREFERENCE);
            password.setOnPreferenceChangeListener(makeNotifier());

            Preference school = findPreference(PreferenceHelper.SCHOOL_PREFERENCE);
            school.setOnPreferenceChangeListener(makeNotifier());

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

            Preference debug = findPreference("Debug");
            debug.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    LoggerUtil.launchSendLogWithAttachment(getActivity());
                    return true;
                }
            });

            Preference notificationDate = findPreference(PreferenceHelper.NOTIFICATION_DATE_PREFERENCE);
            notificationDate.setOnPreferenceChangeListener(makeDateNotifier());

            Preference notificationsEnabled = findPreference(PreferenceHelper.NOTIFICATIONS_ENABLED_PREFERENCE);
            notificationsEnabled.setOnPreferenceChangeListener(makeDateNotifier());
        }

        private Preference.OnPreferenceChangeListener makeNotifier() {
            return new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Snackbar.make(getView(), preference.getTitle() + " has been saved", Snackbar.LENGTH_LONG).show();
                    return true;
                }
            };
        }

        private Preference.OnPreferenceChangeListener makeDateNotifier() {
            return new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if (preference.getKey().equals(PreferenceHelper.NOTIFICATION_DATE_PREFERENCE) ||
                        preference.getKey().equals(PreferenceHelper.NOTIFICATIONS_ENABLED_PREFERENCE)) {

                        PreferenceHelper pHelper = PreferenceHelper.getInstance(getActivity());
                        String textToShow = "You will be updated on the " +
                                pHelper.getDate().get(Calendar.DAY_OF_MONTH)
                                + "th of every month";
                        Snackbar.make(getView(), textToShow, Snackbar.LENGTH_LONG).show();
                    }
                    else {
                        Snackbar.make(getView(), getString(R.string.notifications_disabled), Snackbar.LENGTH_SHORT).show();
                    }
                    return true;
                }
            };
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
