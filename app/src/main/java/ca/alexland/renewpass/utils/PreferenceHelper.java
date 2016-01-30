package ca.alexland.renewpass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import ca.alexland.renewpass.R;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class PreferenceHelper {
    //TODO: perhaps find a better place for this extra variable such as a notification util class
    public static final String EXTRA_NOTIFICATIONS_ENABLED = "EXTRA_NOTIFICATIONS_ENABLED";
    private static final String DEFAULT_VALUE_STRING = "";
    private static final long DEFAULT_VALUE_LONG = -1;

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
        return settings.getString(context.getString(R.string.preference_key_username), DEFAULT_VALUE_STRING);
    }

    public String getPassword() {
        // TODO: Decrypt password
        return settings.getString(context.getString(R.string.preference_key_password), DEFAULT_VALUE_STRING);
    }

    public String getSchool() {
        return settings.getString(context.getString(R.string.preference_key_School), DEFAULT_VALUE_STRING);
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
        return !getUsername().equals(DEFAULT_VALUE_STRING) && !getPassword().equals(DEFAULT_VALUE_STRING);
    }

    public void setupKeys(Context context) {
        this.keyStoreUtil = new KeyStoreUtil(this.getUsername());
        keysExist = keyStoreUtil.createKeys(context);
    }

    public void setLastScheduledNotificationTime(long timeInMillis) {
        editor.putLong(context.getString(R.string.preference_key_notification_last_scheduled), timeInMillis);
    }

    public long getLastScheduledNotificationTime() {
        return settings.getLong(context.getString(R.string.preference_key_notification_last_scheduled), DEFAULT_VALUE_LONG);
    }

    public Calendar getNextNotificationDate() {
        String dateVal = settings.getString(
                context.getString(R.string.preference_key_notification_date),
                context.getString(R.string.preference_value_notification_default_date));
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = Integer.parseInt(dateVal);

        String timeVal = settings.getString(
                context.getString(R.string.preference_key_notification_time),
                context.getString(R.string.preference_value_notification_default_time));
        SimpleTimeFormat simpleTimeFormat = new SimpleTimeFormat(timeVal);
        int hour = simpleTimeFormat.getHour();
        int minute = simpleTimeFormat.getMinute();

        cal.set(year, month, day, hour, minute);

        boolean hasDateAlreadyPassed = System.currentTimeMillis() > cal.getTimeInMillis();
        if (hasDateAlreadyPassed) {
            cal.setTimeInMillis(CalendarUtil.getNextMonthTimeInMillis(cal.getTimeInMillis()));
        }

        return cal;
    }

    public boolean getNotificationsEnabled() {
        return settings.getBoolean(context.getString(R.string.preference_key_notifications_enabled), false);
    }
}
