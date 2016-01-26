package ca.alexland.renewpass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import ca.alexland.renewpass.R;
import ca.alexland.renewpass.TimePreference;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class PreferenceHelper {
    //TODO: perhaps find a better place for this extra variable such as a notification util class
    public static final String EXTRA_NOTIFICATIONS_ENABLED = "EXTRA_NOTIFICATIONS_ENABLED";
    private static final String DEFAULT_VALUE = "";

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private KeyStoreUtil keyStoreUtil;
    private Context context;
    private boolean keysExist;
    private boolean preference;

    public PreferenceHelper(Context context) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = settings.edit();
        this.context = context;
    }

    public String getUsername() {
        return settings.getString(context.getString(R.string.preference_key_username), DEFAULT_VALUE);
    }

    public String getPassword() {
        // TODO: Decrypt password
        return settings.getString(context.getString(R.string.preference_key_password), DEFAULT_VALUE);
    }

    public String getSchool() {
        return settings.getString(context.getString(R.string.preference_key_School), DEFAULT_VALUE);
    }

    public void setUsername(String username) {
        editor.putString(context.getString(R.string.preference_key_username), username);
        editor.commit();
    }

    public void setPassword(String password) {
        // TODO: Encrypt password
        editor.putString(context.getString(R.string.preference_key_password, password), password);
        editor.commit();
    }

    public void setSchool(String school) {
        editor.putString(context.getString(R.string.preference_key_School), school);
        editor.commit();
    }

    public boolean credentialsEntered() {
        return !getUsername().equals(DEFAULT_VALUE) && !getPassword().equals(DEFAULT_VALUE);
    }

    public void setupKeys(Context context) {
        this.keyStoreUtil = new KeyStoreUtil(this.getUsername());
        keysExist = keyStoreUtil.createKeys(context);
    }

    public Calendar getDate() {
        String dateVal = settings.getString(context.getString(R.string.preference_key_notification_date), "2016-01-21");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = getDay(dateVal);

        String timeVal = settings.getString(context.getString(R.string.preference_key_notification_time), TimePreference.DEFAULT_VALUE);
        SimpleTimeFormat simpleTimeFormat = new SimpleTimeFormat(timeVal);
        int hour = simpleTimeFormat.getHour();
        int minute = simpleTimeFormat.getMinute();

        cal.set(year, month, day, hour, minute);
        return cal;
    }

    private int getDay(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[2]));
    }

    public boolean getNotificationsEnabled() {
        return settings.getBoolean(context.getString(R.string.preference_key_notifications_enabled), false);
    }
}
