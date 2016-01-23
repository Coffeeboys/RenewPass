package ca.alexland.renewpass.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import ca.alexland.renewpass.exceptions.DecryptionFailedException;
import ca.alexland.renewpass.exceptions.EncryptionFailedException;

/**
 * Created by AlexLand on 2015-12-30.
 */
public class PreferenceHelper {
    public static final String FIRST_RUN_PREFERENCE = "First Run";
    public static final String SCHOOL_PREFERENCE = "School";
    public static final String USERNAME_PREFERENCE = "Username";
    public static final String PASSWORD_PREFERENCE = "Password";
    public static final String PREFERENCE_KEY_ALIAS = "KeyAlias";

    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private KeyStoreUtil keyStoreUtil;
    private boolean keysExist;
    private boolean passwordEncrypted;
    private static PreferenceHelper instance = null;

    private PreferenceHelper(Context context) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = settings.edit();
    }

    public static PreferenceHelper getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceHelper(context);
        }
        return instance;
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
        String password = settings.getString(PASSWORD_PREFERENCE, "");
        if (keysExist && passwordEncrypted) {
            try {
                password = keyStoreUtil.decryptPassword(password);
            } catch (DecryptionFailedException e) {
                // TODO: Deal with failure, possibly ask for credentials and fall back to unencrypted?
                Log.d("RenewPass", "Password decryption failed: " + e.getLocalizedMessage());
            }
        }
        return password;
    }

    public String getSchool() {
        return settings.getString(SCHOOL_PREFERENCE, "");
    }

    public void setUsername(String username) {
        editor.putString(USERNAME_PREFERENCE, username);
        editor.commit();
    }

    public void setPassword(String password) {
        if (keysExist) {
            try {
                password = keyStoreUtil.encryptPassword(password);
                passwordEncrypted = true;
            } catch (EncryptionFailedException e) {
                // TODO: Notify user of failed encryption
                Log.d("RenewPass", "Password encryption failed: " + e.getLocalizedMessage());
                passwordEncrypted = false;
            }
        }
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

    private void setupKeys(Context context) {
        keysExist = keyStoreUtil.createKeys(context);
    }

    public void setupEncryption(Context context) {
        String alias = getKeyAlias();
        if (alias.equals("")) {
            alias = this.getUsername();
            setKeyAlias(alias);
        }
        this.keyStoreUtil = new KeyStoreUtil(alias);
        keysExist = keyStoreUtil.keysExist();
        if (!keysExist) {
            setupKeys(context);
        }
    }

    private String getKeyAlias() {
        return settings.getString(PREFERENCE_KEY_ALIAS, "");
    }

    private void setKeyAlias(String alias) {
        editor.putString(PREFERENCE_KEY_ALIAS, alias);
        editor.commit();
    }
}
