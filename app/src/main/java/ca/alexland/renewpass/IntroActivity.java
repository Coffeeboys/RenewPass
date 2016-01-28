package ca.alexland.renewpass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import ca.alexland.renewpass.utils.AlarmUtil;
import ca.alexland.renewpass.utils.PreferenceHelper;

public class IntroActivity extends AppIntro2 {

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }*/

    @Override
    public void onBackPressed() {
        getPager().setCurrentItem(0);
    }

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
            PreferenceHelper preferenceHelper = new PreferenceHelper(this);
            preferenceHelper.setUsername(usernameString);
            preferenceHelper.setPassword(passwordString);
            preferenceHelper.setSchool(schoolString);
            preferenceHelper.setupKeys(getApplicationContext());
            AlarmUtil.setAlarm(getApplicationContext(), false);
            finish();
        }
    }
/*
    TODO
    enable done only when all fields are filled
    Also try connection once to make sure all fields inputted are correct
 */
    @Override
    public void onSlideChanged(){

    }
}
