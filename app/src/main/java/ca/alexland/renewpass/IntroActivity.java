package ca.alexland.renewpass;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import net.soulwolf.widget.materialradio.MaterialRadioButton;
import net.soulwolf.widget.materialradio.MaterialRadioGroup;
import net.soulwolf.widget.materialradio.listener.OnCheckedChangeListener;

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
                return inflater.inflate(R.layout.credential_slide, container, false);
            }
        };
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
    public void onSlideChanged() {

    }
}
