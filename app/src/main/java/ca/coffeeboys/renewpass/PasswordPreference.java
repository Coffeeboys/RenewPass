package ca.coffeeboys.renewpass;

import android.content.Context;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

import ca.coffeeboys.renewpass.utils.PreferenceHelper;

/**
 * Created by AlexLand on 2016-01-21.
 */
public class PasswordPreference extends EditTextPreference {
    private PreferenceHelper preferenceHelper;

    public PasswordPreference(Context context) {
        super(context);
        preferenceHelper = PreferenceHelper.getInstance(context);
    }

    public PasswordPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        preferenceHelper = PreferenceHelper.getInstance(context);
    }

    public PasswordPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        preferenceHelper = PreferenceHelper.getInstance(context);
    }

    @Override
    public void setText(String text) {
        final boolean wasBlocking = shouldDisableDependents();

        preferenceHelper.setPassword(text);

        final boolean isBlocking = shouldDisableDependents();
        if (isBlocking != wasBlocking) {
            notifyDependencyChange(isBlocking);
        }
    }

    @Override
    public String getText() {
        return preferenceHelper.getPassword();
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        // Override so the EditTextPreference doesn't overwrite our password
    }
}
