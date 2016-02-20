package ca.alexland.renewpass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import ca.alexland.renewpass.exceptions.DecryptionFailedException;
import ca.alexland.renewpass.exceptions.EncryptionFailedException;

import ca.alexland.renewpass.R;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class PreferenceHelper {
    public static final String EXTRA_NOTIFICATIONS_ENABLED = "EXTRA_NOTIFICATIONS_ENABLED";

    private static final String DEFAULT_VALUE_STRING = "";
    private static final long DEFAULT_VALUE_LONG = -1;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private KeyStoreUtil keyStoreUtil;
    private static PreferenceHelper instance = null;
    private Context context;

    private PreferenceHelper(Context context) {
        this.context = context;
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = settings.edit();
        this.context = context;
        this.keyStoreUtil = new KeyStoreUtil(getKeyAlias());
    }

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
    }

    public String getUsername() {
        return settings.getString(context.getString(R.string.preference_key_username), DEFAULT_VALUE_STRING);
    }

    public String getPassword() {
        String password = settings.getString(context.getString(R.string.preference_key_password), DEFAULT_VALUE_STRING);
        if (getKeysExist() && getPasswordExcrypted()) {
            try {
                password = keyStoreUtil.decryptPassword(password);
            } catch (DecryptionFailedException e) {
                // TODO: Deal with failure, possibly ask for credentials and fall back to unencrypted?
                LoggerUtil.appendLogWithStacktrace(context, "Password decryption failed: ", e.getOriginalException());
            }
        }
        return password;
    }

    public String getSchool() {
        return settings.getString(context.getString(R.string.preference_key_School), DEFAULT_VALUE_STRING);
    }

    public void setUsername(String username) {
        editor.putString(context.getString(R.string.preference_key_username), username);
        editor.commit();
    }

    public void setPassword(String password) {
        if (getKeysExist()) {
            try {
                password = keyStoreUtil.encryptPassword(password);
                setPasswordEncrypted(true);
            } catch (EncryptionFailedException e) {
                // TODO: Notify user of failed encryption
                LoggerUtil.appendLogWithStacktrace(context, "Password encryption failed: ", e.getOriginalException());
                setPasswordEncrypted(false);
            }
        }
        editor.putString(context.getString(R.string.preference_key_password), password);
        editor.commit();
    }

    public void setSchool(String school) {
        editor.putString(context.getString(R.string.preference_key_School), school);
        editor.commit();
    }

    public boolean credentialsEntered() {
        return !getUsername().equals(DEFAULT_VALUE_STRING) && !getPassword().equals(DEFAULT_VALUE_STRING);
    }

    private void setupKeys(Context context) {
        setKeysExist(keyStoreUtil.createKeys(context));
    }

    public void setupEncryption(Context context) {
        String alias = getKeyAlias();
        if (alias.equals(DEFAULT_VALUE_STRING)) {
            alias = this.getUsername();
            setKeyAlias(alias);
            keyStoreUtil.setAlias(alias);
        }
        setKeysExist(keyStoreUtil.keysExist());
        if (!getKeysExist()) {
            setupKeys(context);
        }
    }

    private String getKeyAlias() {
        return settings.getString(context.getString(R.string.preference_key_key_alias), "");
    }

    private void setKeyAlias(String alias) {
        editor.putString(context.getString(R.string.preference_key_key_alias), alias);
        editor.commit();
    }

    public void setLastScheduledNotificationTime(long timeInMillis) {
        editor.putLong(context.getString(R.string.preference_key_autorenew_last_scheduled), timeInMillis);
        editor.commit();
    }

    /**
     * Store the last scheduled notification. This is used for restoring alarms after the device reboots
     */
    public long getLastScheduledNotificationTime() {
        return settings.getLong(context.getString(R.string.preference_key_autorenew_last_scheduled), DEFAULT_VALUE_LONG);
    }

    /**
     * Returns a calendar object holding the next date to schedule for notifications.
     * If the user set day and time have already passed for the current month, the date will be for the next month
     * Ex: if it is January 22nd and the user has set their renew date to January 21st, the calendar date will be set to Feb. 21st
     */
    public Calendar getNextNotificationDate() {
        return getNextNotificationDateHelp(false);
    }

    /**
     * @return the date for notifications that is a month after the currently saved notification date preference
     */
    public Calendar getNextMonthNotificationDate() {
        return getNextNotificationDateHelp(true);
    }

    private Calendar getNextNotificationDateHelp(boolean forceNextMonthDate) {
        Calendar cal = Calendar.getInstance();
        long currTime = cal.getTimeInMillis();

        String dateVal = settings.getString(
                context.getString(R.string.preference_key_autorenew_date),
                context.getString(R.string.preference_value_notification_default_date));
        //year and month are the same as the current year and month
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = Integer.parseInt(dateVal);

        String timeVal = settings.getString(
                context.getString(R.string.preference_key_autorenew_time),
                context.getString(R.string.preference_value_notification_default_time));
        SimpleTimeFormat simpleTimeFormat = new SimpleTimeFormat(timeVal);
        int hour = simpleTimeFormat.getHour();
        int minute = simpleTimeFormat.getMinute();

        cal.set(year, month, day, hour, minute);

        boolean hasDateAlreadyPassedThisMonth = currTime > cal.getTimeInMillis();
        if (forceNextMonthDate || hasDateAlreadyPassedThisMonth) {
            cal.setTimeInMillis(CalendarUtil.getNextMonthTimeInMillis(cal.getTimeInMillis()));
        }

        return cal;
    }

    public boolean getNotificationsEnabled() {
        return settings.getBoolean(context.getString(R.string.preference_key_autorenew_enabled), false);
    }

    private void setKeysExist(boolean keysExist) {
        editor.putBoolean(context.getString(R.string.preference_key_keys), keysExist);
        editor.commit();
    }

    private boolean getKeysExist() {
        return settings.getBoolean(context.getString(R.string.preference_key_keys), true);
    }

    private void setPasswordEncrypted(boolean passwordEncrypted) {
        editor.putBoolean(context.getString(R.string.preference_key_encrypted), passwordEncrypted);
        editor.commit();
    }

    private boolean getPasswordExcrypted() {
        return settings.getBoolean(context.getString(R.string.preference_key_encrypted), false);
    }

    public int getPreviousVersionCode() {
        return settings.getInt(context.getString(R.string.preference_key_prevversion), 0);
    }

    public void setPreviousVersionCode(int currVersion) {
        editor.putInt(context.getString(R.string.preference_key_prevversion), currVersion);
        editor.commit();
    }
}
