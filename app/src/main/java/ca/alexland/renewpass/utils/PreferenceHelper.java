package ca.alexland.renewpass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class PreferenceHelper {
    private static final String RENEWPASS_PREFERENCES = "RenewPass Preferences";
    private static final String FIRST_RUN_PREFERENCE = "First Run";
    private static final String SCHOOL_PREFERENCE = "School";
    private static final String USERNAME_PREFERENCE = "Username";
    private static final String PASSWORD_PREFERENCE = "Password";
    public static final String NOTIFICATION_DATE_PREFERENCE = "NotificationDate";
    public static final String NOTIFICATIONS_ENABLED_PREFERENCE = "NotificationsEnabled";

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private KeyStoreUtil keyStoreUtil;
    private boolean keysExist;
    private boolean preference;

    public PreferenceHelper(Context context) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = settings.edit();
    }

    public boolean getFirstRun() {
        return settings.getBoolean(FIRST_RUN_PREFERENCE, true);
    }

    public void setFirstRun(boolean firstRun) {
        editor.putBoolean(FIRST_RUN_PREFERENCE, firstRun);
        editor.commit();
    }

    public String getUsername() {
        return settings.getString(USERNAME_PREFERENCE, "");
    }

    public String getPassword() {
        // TODO: Decrypt password
        return settings.getString(PASSWORD_PREFERENCE, "");
    }

    public String getSchool() {
        return settings.getString(SCHOOL_PREFERENCE, "");
    }

    public void setUsername(String username) {
        editor.putString(USERNAME_PREFERENCE, username);
        editor.commit();
    }

    public void setPassword(String password) {
        // TODO: Encrypt password
        editor.putString(PASSWORD_PREFERENCE, password);
        editor.commit();
    }

    public void setSchool(String school) {
        editor.putString(SCHOOL_PREFERENCE, school);
        editor.commit();
    }

    public boolean credentialsEntered() {
        return !getUsername().equals("") && !getPassword().equals("");
    }

    public void setupKeys(Context context) {
        this.keyStoreUtil = new KeyStoreUtil(this.getUsername());
        keysExist = keyStoreUtil.createKeys(context);
    }

    public Calendar getDate() {
        String dateVal = settings.getString(NOTIFICATION_DATE_PREFERENCE, "2016-01-21");
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = getDay(dateVal);
        cal.set(year, month, day);
        return cal;
    }

    private int getDay(String dateval) {
        String[] pieces = dateval.split("-");
        return (Integer.parseInt(pieces[2]));
    }

    public boolean getNotificationsEnabled() {
        return settings.getBoolean(NOTIFICATIONS_ENABLED_PREFERENCE, false);
    }
}
