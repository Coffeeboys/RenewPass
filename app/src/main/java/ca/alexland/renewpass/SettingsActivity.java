package ca.alexland.renewpass;

import android.app.AlarmManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.Window;
import android.view.WindowManager;

import ca.alexland.renewpass.interfaces.IPreference;
import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

public class SettingsActivity extends PreferenceActivity
{
    public static final String PREFERENCE_KEY_NOTIFICATION_DATE = "NotificationDate";
    public static final String PREFERENCE_KEY_NOTIFICATION_TIME = "NotificationTime";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements IPreference.PreferenceSelectedListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            ((IPreference)findPreference(PREFERENCE_KEY_NOTIFICATION_DATE)).setPreferenceSelectedListener(this);
            ((IPreference)findPreference(PREFERENCE_KEY_NOTIFICATION_TIME)).setPreferenceSelectedListener(this);
        }

        @Override
        public void onPreferenceSelected() {
            if (System.currentTimeMillis() < new PreferenceHelper(getActivity()).getDate().getTimeInMillis()) {
                AlarmUtil.setAlarm(getActivity(), false);
            }
        }
    }
}
