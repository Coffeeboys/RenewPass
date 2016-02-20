package ca.alexland.renewpass;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import net.soulwolf.widget.materialradio.MaterialRadioButton;
import net.soulwolf.widget.materialradio.MaterialRadioGroup;
import net.soulwolf.widget.materialradio.listener.OnCheckedChangeListener;

import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

public class IntroActivity extends AppIntro2 {
    public static final String EXTRA_TITLE = "Title";
    public static final String EXTRA_DESCRIPTION = "Description";

    @Override
    public void onBackPressed() {
        getPager().setCurrentItem(0);
    }

    @Override
    public void init(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String title = intent.getStringExtra(EXTRA_TITLE);
        String description = intent.getStringExtra(EXTRA_DESCRIPTION);

        addSlide(AppIntroFragment.newInstance(title,
                description,
                R.drawable.ic_autorenew,
                ContextCompat.getColor(this, R.color.colorPrimary)));

        addSlide(getCredentialSlide());

        showStatusBar(false);

        setVibrate(false);
        setVibrateIntensity(0);

        setFadeAnimation();
    }

    private Fragment getCredentialSlide() {
        return new IntroFragment();
    }

    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        boolean isValid = true;

        MaterialRadioGroup mrg = (MaterialRadioGroup) findViewById(R.id.school_radio_group);
        EditText username = (EditText) findViewById(R.id.username_field);
        EditText password = (EditText) findViewById(R.id.password_field);

        String schoolString = null;
        int id = mrg.getCheckedRadioButtonId();
        if (id != -1) {
            MaterialRadioButton rb = (MaterialRadioButton) findViewById(id);
            schoolString = rb.getText().toString();
        }


        if (schoolString == null) {
            TextView errorText = (TextView) findViewById(R.id.school_selection_text);
            errorText.setError("Invalid school selection");
            errorText.setTextColor(Color.RED);
            errorText.setText(R.string.error_school_selection);
            isValid = false;
        }

        String usernameString = username.getText().toString();
        if (usernameString.equals("")) {
            username.setError("Invalid username");
            isValid = false;
        }
        String passwordString = password.getText().toString();
        if (passwordString.equals("")) {
            password.setError("Invalid password");
            isValid = false;
        }

        if (isValid) {
            PreferenceHelper preferenceHelper = PreferenceHelper.getInstance(this);
            preferenceHelper.setUsername(usernameString);
            preferenceHelper.setupEncryption(getApplicationContext());
            preferenceHelper.setPassword(passwordString);
            preferenceHelper.setSchool(schoolString);
            finish();
        }
    }

    /*
        TODO
        enable done only when all fields are filled
        Also try connection once to make sure all fields inputted are correct
     */

    @Override
    public void onSlideChanged() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        // Don't let the user back out of the intro, they need to enter credentials.
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }
    }

public static class IntroFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.credential_slide, container, false);

            final EditText editText = (EditText) view.findViewById(R.id.password_field);

            CheckBox checkBox = (CheckBox) view.findViewById(R.id.password_checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        editText.setTransformationMethod(null);
                    } else {
                        editText.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            });

            return view;
        }
    }
}
