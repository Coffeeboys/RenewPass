package ca.alexland.renewpass;

import android.app.AlarmManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import ca.alexland.renewpass.interfaces.IPreference;
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

    public static class SettingsFragment extends PreferenceFragment implements IPreference.PreferenceSelectedListener
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            final CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference(getActivity().getString(R.string.preference_key_notifications_enabled));
            checkBoxPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    if (checkBoxPreference.isChecked()) {
                        AlarmUtil.setAlarm(getActivity(), false);
                    } else {
                        AlarmUtil.cancelAlarm(getActivity());
                    }
                    //update the preference value as usual
                    return true;
                }
            });
            ((IPreference)findPreference(
                    getActivity().getString(R.string.preference_key_notification_date)))
                    .setPreferenceSelectedListener(this);
            ((IPreference)findPreference(
                    getActivity().getString(R.string.preference_key_notification_time)))
                    .setPreferenceSelectedListener(this);
        }

        @Override
        public void onPreferenceSelected() {
            if (System.currentTimeMillis() < new PreferenceHelper(getActivity()).getDate().getTimeInMillis()) {
                AlarmUtil.setAlarm(getActivity(), false);
            }
        }
    }
}
