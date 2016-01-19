package ca.alexland.renewpass;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

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
    public void init(Bundle savedInstanceState) {

        addSlide(AppIntroFragment.newInstance(getString(R.string.app_welcome),
                getString(R.string.app_description),
                R.drawable.ic_autorenew,
                ContextCompat.getColor(this, R.color.colorPrimaryDark)));

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

        EditText username = (EditText)findViewById(R.id.username_field);
        EditText password = (EditText)findViewById(R.id.password_field);

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
            preferenceHelper.addUsername(usernameString);
            preferenceHelper.addPassword(passwordString);
            finish();
        }
    }

    @Override
    public void onSlideChanged(){

    }
}
