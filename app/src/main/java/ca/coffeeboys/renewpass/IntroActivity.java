package ca.coffeeboys.renewpass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import ca.coffeeboys.renewpass.utils.PreferenceHelper;

public class IntroActivity extends AppIntro2 {

    @Override
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance(getString(R.string.app_welcome),
                getString(R.string.app_description),
                R.drawable.ic_autorenew,
                ContextCompat.getColor(this, R.color.colorPrimary)));

        addSlide(getCredentialSlide());

        showStatusBar(false);

        setVibrate(false);
        setVibrateIntensity(0);

        setFadeAnimation();
    }

    private Fragment getCredentialSlide() {
        return new Fragment() {
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.credential_slide, container, false);

                Spinner spinner = (Spinner) view.findViewById(R.id.school_selection_spinner);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.school_list, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setPrompt(getString(R.string.school_spinner_default));
                spinner.setAdapter(
                        new NothingSelectedSpinnerAdapter(
                                adapter,
                                R.layout.contact_spinner_row_nothing_selected,
                                // R.layout.contact_spinner_nothing_selected_dropdown, // Optional
                                getApplicationContext()));

                return view;
            }
        };
    }
    @Override
    public void onNextPressed() {

    }

    @Override
    public void onDonePressed() {
        boolean isValid = true;

        Spinner school = (Spinner)findViewById(R.id.school_selection_spinner);
        EditText username = (EditText)findViewById(R.id.username_field);
        EditText password = (EditText)findViewById(R.id.password_field);

        String schoolString = (String)school.getSelectedItem();
        if (schoolString == null) {
            TextView errorText = (TextView)school.getSelectedView();
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

    @Override
    public void onSlideChanged(){

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
}
