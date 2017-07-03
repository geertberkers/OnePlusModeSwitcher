package geert.berkers.modeswitcher.activity;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.fragment.MyPreferenceFragment;
import geert.berkers.modeswitcher.service.AlertSliderService;

import static geert.berkers.modeswitcher.helper.NotificationState.NOTIFICATION;
import static geert.berkers.modeswitcher.helper.NotificationState.NOTIFICATION_STATE;
import static geert.berkers.modeswitcher.helper.NotificationState.STOPPED;
import static geert.berkers.modeswitcher.helper.NotificationState.UNKNOWN;

/**
 * Created by Geert Berkers.
 */
public class MainActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Button btnStopService;
    private Button btnStartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initControls();
        initSettingsFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handleNotification();
        handleServiceButtons();
    }

    /**
     * Check if service has to start on boot or app is closed
     */
    private void handleNotification(){
        if(doStartOnOpenApp()){
            initService();
        } else {
            checkStopNotification();
        }
    }

    /**
     * Check if AlertSliderService has to start on opening app
     */
    private boolean doStartOnOpenApp() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("startOnOpenApp", true);
    }

    /**
     * Check if notification is stopped.
     * Opening on app hasn't to show notification.
     */
    private void checkStopNotification() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String notificationState = preferences.getString(NOTIFICATION_STATE, UNKNOWN);

        if (notificationState.equals(STOPPED)) {
            NotificationManager mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNM.cancel(NOTIFICATION);
        }
    }

    /**
     * Handle button background color
     */
    private void handleServiceButtons() {
        if(isAlertSliderServiceRunning()){
            btnStopService.setEnabled(true);
            btnStartService.setEnabled(false);
            btnStopService.getBackground().setColorFilter(0xFF3F51B5, PorterDuff.Mode.MULTIPLY);
            btnStartService.getBackground().setColorFilter(0x803F51B5, PorterDuff.Mode.MULTIPLY);
        } else{
            btnStopService.setEnabled(false);
            btnStartService.setEnabled(true);
            btnStopService.getBackground().setColorFilter(0x803F51B5, PorterDuff.Mode.MULTIPLY);
            btnStartService.getBackground().setColorFilter(0xFF3F51B5, PorterDuff.Mode.MULTIPLY);
        }
    }

    /**
     * Set SettingsFragment
     */
    private void initSettingsFragment() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.settingsFragment, new MyPreferenceFragment())
                .commit();
    }

    /**
     * Initialize Toolbar
     */
    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

    }

    /**
     * Initialize Controls for Buttons and SharedPreferenceChangeListener
     */
    private void initControls() {
        btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(getServiceIntent());
                handleServiceButtons();
            }
        });

        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(getServiceIntent());
                handleServiceButtons();
            }
        });

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    /**
     * Init AlertSliderService
     */
    private void initService() {
        startService(getServiceIntent());
    }

    /**
     * Get the current ServiceIntent
     * @return Intent of Service
     */
    private Intent getServiceIntent(){
        return new Intent(this, AlertSliderService.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                showAboutPopUp();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show about developer popup
     */
    private void showAboutPopUp() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.action_about);
        alertDialogBuilder.setIcon(R.drawable.ic_info_black);
        alertDialogBuilder.setMessage(R.string.about_developer);
        alertDialogBuilder.setNegativeButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        alertDialogBuilder.show();
    }

    /**
     * Check if Service is running currently
     * @return true if running else false
     */
    private boolean isAlertSliderServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {

            if (AlertSliderService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "showNotification":
                initService();
                break;
            case "notificationState":
                handleServiceButtons();
                break;
        }
    }
}
