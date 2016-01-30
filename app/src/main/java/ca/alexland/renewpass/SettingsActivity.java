package ca.alexland.renewpass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

public class SettingsActivity extends PreferenceActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        private String preferenceKeyNotificationsEnabled;
        private String preferenceKeyNotificationDate;
        private String preferenceKeyNotificationTime;
        private PreferenceHelper preferenceHelper;

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            preferenceKeyNotificationsEnabled = getActivity().getString(R.string.preference_key_notifications_enabled);
            preferenceKeyNotificationDate = getActivity().getString(R.string.preference_key_notification_date);
            preferenceKeyNotificationTime = getActivity().getString(R.string.preference_key_notification_time);

            preferenceHelper = new PreferenceHelper(getActivity());
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

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(preferenceKeyNotificationsEnabled) ||
                    key.equals(preferenceKeyNotificationDate) ||
                    key.equals(preferenceKeyNotificationTime)) {
                if (preferenceHelper.getNotificationsEnabled()) {
                    AlarmUtil.setAlarm(getActivity(), false);
                } else {
                    AlarmUtil.cancelAlarm(getActivity());
                }
            }
        }
    }
}
