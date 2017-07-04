package geert.berkers.modeswitcher.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.fragment.MyPreferenceFragment;
import geert.berkers.modeswitcher.helper.PreferenceHelper;
import geert.berkers.modeswitcher.service.AlertSliderService;

import static geert.berkers.modeswitcher.helper.NotificationState.*;

/**
 * Created by Geert.
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
        initService();
        handleServiceButtons();
    }

    private void handleNotification() {
        boolean startOnOpenApp = PreferenceHelper.getBoolean(this, "startOnOpenApp", true);
        setNotificationState(startOnOpenApp ? ENABLED : getCurrentNotificationState());
    }

    /**
     * Handle button background color
     */
    private void handleServiceButtons() {
        String notificationState = getCurrentNotificationState();

        if(notificationState.equals(STOPPED) || notificationState.equals(DISABLED)){
            setServiceStoppedLayout();
        } else{
            setServiceStartedLayout();
        }
    }

    private void setServiceStartedLayout(){
        btnStopService.setEnabled(true);
        btnStartService.setEnabled(false);
        btnStopService.getBackground().setColorFilter(0xFF3F51B5, PorterDuff.Mode.MULTIPLY);
        btnStartService.getBackground().setColorFilter(0x803F51B5, PorterDuff.Mode.MULTIPLY);
    }

    private void setServiceStoppedLayout(){
        btnStopService.setEnabled(false);
        btnStartService.setEnabled(true);
        btnStopService.getBackground().setColorFilter(0x803F51B5, PorterDuff.Mode.MULTIPLY);
        btnStartService.getBackground().setColorFilter(0xFF3F51B5, PorterDuff.Mode.MULTIPLY);
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
                setNotificationState(ENABLED);
                initService();
                handleServiceButtons();
            }
        });

        btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNotificationState(STOPPED);
                stopService();
                handleServiceButtons();
            }
        });

        registerOnSharedPreferenceChangeListener();
    }

    /**
     * Set the NotificationState
     * @param state NotificationState to set
     */
    private void setNotificationState(String state) {
        PreferenceHelper.setNotificationState(MainActivity.this, state);
    }

    /**
     * Set OnSharedPreferenceListener
     */
    private void registerOnSharedPreferenceChangeListener() {
        PreferenceHelper.registerOnSharedPreferenceChangeListener(this, this);
    }

    /**
     * Init AlertSliderService
     */
    private void initService() {
        startService(getServiceIntent());
    }

    /**
     * Stop AlertSliderService
     */
    private void stopService() {
        stopService(getServiceIntent());
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "showNotification":
                initShowNotificationPreference();
                initService();
                break;
            case "notificationState":
                handleServiceButtons();
                break;
        }
    }

    private void initShowNotificationPreference() {
        boolean showNotification = PreferenceHelper.getBoolean(this, "showNotification", false);
        setNotificationState(showNotification ? ENABLED : getCurrentNotificationState());
    }

    private String getCurrentNotificationState(){
        return PreferenceHelper.getString(this, NOTIFICATION_STATE, UNKNOWN);
    }
}
