package ca.alexland.renewpass;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.design.widget.Snackbar;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;

import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

public class SettingsActivity extends PreferenceActivity {
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
                PreferenceHelper pHelper = new PreferenceHelper(getActivity());
                String textToShow = "You will be updated on the " +
                        pHelper.getDate().get(Calendar.DAY_OF_MONTH)
                        + "th of every month";
                Snackbar.make(getView(), textToShow, Snackbar.LENGTH_SHORT).show();
                // AlarmUtil.setAlarm(getActivity(), false); TODO
                // Do we need to call setAlarm here??
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
        }
    }

}
