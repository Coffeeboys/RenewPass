package ca.alexland.renewpass;

import android.content.Context;
import android.preference.EditTextPreference;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import ca.alexland.renewpass.utils.PreferenceHelper;

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
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        CheckBox checkbox = makePasswordCheckbox(view);

        ViewParent oldParent = checkbox.getParent();
        if (oldParent != view) {
            if (oldParent != null) {
                ((ViewGroup) oldParent).removeView(checkbox);
            }
            onAddCheckBoxToDialogView(view, checkbox);
        }
    }

    private CheckBox makePasswordCheckbox(View view) {
        final EditText editText = getEditText();

        CheckBox checkBox = new CheckBox(view.getContext());
        checkBox.setText(R.string.checkbox_show_password);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editText.setTransformationMethod(null);
                }
                else {
                    editText.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        return checkBox;
    }

    /**
     * Adds the EditText widget of this preference to the dialog's view.
     *
     * @param dialogView The dialog view.
     */
    protected void onAddCheckBoxToDialogView(View dialogView, CheckBox checkbox) {
        ScrollView scrollView = (ScrollView) dialogView;
        LinearLayout layout = (LinearLayout) scrollView.getChildAt(0);
        if (layout != null) {
            layout.addView(checkbox, ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
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
