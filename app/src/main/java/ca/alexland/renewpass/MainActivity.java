package ca.alexland.renewpass;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import ca.alexland.renewpass.model.Callback;
import ca.alexland.renewpass.model.Status;
import ca.alexland.renewpass.utils.LoggerUtil;
import ca.alexland.renewpass.views.LoadingFloatingActionButton;
import ca.alexland.renewpass.utils.PreferenceHelper;
import ca.alexland.renewpass.utils.UPassLoader;

public class MainActivity extends AppCompatActivity {
    PreferenceHelper preferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Clear the log on start
        LoggerUtil.deleteLog(getApplicationContext());

        this.preferenceHelper = PreferenceHelper.getInstance(getApplicationContext());
        startIntroActivity();

        final LoadingFloatingActionButton loadingFab = (LoadingFloatingActionButton) findViewById(R.id.loading_fab);
        loadingFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onRenew(loadingFab);
            }
        });
        Drawable completeIcon = loadingFab.getCompleteIconDrawable();
        if (completeIcon != null) {
            tintDrawable(completeIcon, Color.WHITE);
        }

        Drawable failureIcon = loadingFab.getFailureIconDrawable();
        if (failureIcon != null) {
            tintDrawable(failureIcon, Color.WHITE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem settingsItem = menu.findItem(R.id.action_settings);
        Drawable settingsIcon = DrawableCompat.wrap(settingsItem.getIcon());
        DrawableCompat.setTint(settingsIcon, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRenew(final LoadingFloatingActionButton fab) {
        fab.startLoading();
        UPassLoader.renewUPass(getApplicationContext(), new Callback() {
            @Override
            public void onUPassLoaded(Status result) {
                if (result.isSuccessful()) {
                    fab.finishSuccess();
                } else {
                    fab.finishFailure();
                }
                switch (result.getStatusText()) {
                    case Status.NETWORK_ERROR:
                        Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onRenew(fab);
                                    }
                                })
                                .show();
                        break;
                    case Status.AUTHENTICATION_ERROR:
                        Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Settings", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .show();
                        break;
                    case Status.RENEW_FAILED:
                        Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("Send Log", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        LoggerUtil.launchSendLogWithAttachment(v.getContext());
                                    }
                                }).show();
                        break;
                    default:
                        Snackbar.make(fab, result.getStatusText(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void startIntroActivity() {

        Thread introActivityThread = new Thread(new Runnable() {
            @Override
            public void run(){
                PreferenceHelper preferences = PreferenceHelper.getInstance(MainActivity.this);

                int prevVersionCode = preferences.getPreviousVersionCode();
                boolean credentialsEntered = preferences.credentialsEntered();

                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                if (prevVersionCode < BuildConfig.VERSION_CODE && credentialsEntered) {
                    i.putExtra(IntroActivity.EXTRA_TITLE, getString(R.string.app_welcome_upgrade));
                    i.putExtra(IntroActivity.EXTRA_DESCRIPTION, getString(R.string.app_welcome_upgrade_description));
                    preferences.setPreviousVersionCode(BuildConfig.VERSION_CODE);
                    startActivity(i);
                }

                if (!credentialsEntered) {
                    i.putExtra(IntroActivity.EXTRA_TITLE, getString(R.string.app_welcome));
                    i.putExtra(IntroActivity.EXTRA_DESCRIPTION,getString(R.string.app_welcome_description));
                    preferences.setPreviousVersionCode(BuildConfig.VERSION_CODE);
                    startActivity(i);
                }

            }
        });

        introActivityThread.start();

    }

    private void tintDrawable(Drawable drawable, int color) {
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, color);
    }
}
