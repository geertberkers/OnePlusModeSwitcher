package geert.berkers.modeswitcher.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import geert.berkers.modeswitcher.activity.MainActivity;
import geert.berkers.modeswitcher.broadcastReceivers.BootReceiver;
import geert.berkers.modeswitcher.broadcastReceivers.CloseReceiver;
import geert.berkers.modeswitcher.broadcastReceivers.HideReceiver;
import geert.berkers.modeswitcher.broadcastReceivers.InterruptionFilterReceiver;
import geert.berkers.modeswitcher.R;

/**
 * Created by Geert Berkers.
 */
public class AlertSliderService extends Service {

    private boolean isRegistered = false;

    private NotificationManager mNM;

    private InterruptionFilterReceiver interruptionFilterReceiver;

    private final int NOTIFICATION = R.string.alert_slider_service;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AlertSliderService", "onStartCommand()");

        initNotificationManager();
        initInterruptionFilterReceiver();

        boolean showNotification = getPreferenceBoolean("showNotification", true);
        if(showNotification) {
            showNotification();
        } else {
            stopNotification();
        }

        return START_STICKY;
    }

    /**
     * Initialize Notification for Service
     */
    private void initNotificationManager() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * Initialize InterruptionFilterReceiver for Service
     */
    private void initInterruptionFilterReceiver() {
        IntentFilter intentFilter = new IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);

        if(!isRegistered){
            interruptionFilterReceiver = new InterruptionFilterReceiver();
            registerReceiver(interruptionFilterReceiver, intentFilter);
            isRegistered = true;
        }
    }

    private boolean getPreferenceBoolean(String key, boolean defaultValue){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean(key, defaultValue);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("AlertSliderService", "onDestroy()");

        boolean showNotification = getPreferenceBoolean("showStoppedNotification", true);
        if(showNotification) {
            showStoppedNotification();
        } else {
            stopNotification();
        }

        if(isRegistered) {
            unregisterReceiver(interruptionFilterReceiver);
            isRegistered = false;
        }
    }

    /**
     * Close current notification
     */
    private void stopNotification() {
        mNM.cancel(NOTIFICATION);
    }

    /**
     * Show a notification when this service stopped.
     */
    private void showStoppedNotification() {
        // Launch intent
        PendingIntent contentIntent = createContentIntent();

        // Hide notification action
        Intent restartServiceIntent = new Intent(this, BootReceiver.class);
        restartServiceIntent.setAction(BootReceiver.RESTART_APP);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, restartServiceIntent, 0);
        NotificationCompat.Action restartAction = new NotificationCompat.Action.Builder(R.drawable.ic_restart, "Restart", restartPendingIntent).build();

        // Build notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.service_stopped))
                .setContentText(getString(R.string.click_open_app))
                .setContentIntent(contentIntent)
                .addAction(restartAction)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        Log.i("AlertSliderService", "showNotification()");
        CharSequence text = getString(R.string.current_mode) + " " + getRingerMode();
        
        PendingIntent contentIntent = createContentIntent();
        NotificationCompat.Action hideAction = createHideAction();
        NotificationCompat.Action closeAction = createCloseAction();

        setHiddenPreference();

        // Build notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.service_running))
                .setContentText(text)
                .setContentIntent(contentIntent)
                .addAction(hideAction)
                .addAction(closeAction)
                .setOngoing(true)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    private PendingIntent createContentIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
    }

    private NotificationCompat.Action createCloseAction() {
        Intent closeIntent = new Intent(this, CloseReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, 0);
        return new NotificationCompat.Action.Builder(R.drawable.ic_stop, getString(R.string.close), closePendingIntent).build();
    }

    private NotificationCompat.Action createHideAction() {
        Intent hideIntent = new Intent(this, HideReceiver.class);
        hideIntent.putExtra("notification", NOTIFICATION);
        PendingIntent hidePendingIntent = PendingIntent.getBroadcast(this, 0, hideIntent, 0);
        return new NotificationCompat.Action.Builder(R.drawable.ic_hide, getString(R.string.hide), hidePendingIntent).build();
    }

    private void setHiddenPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.edit().putBoolean("hidden", false).apply();
    }

    private String getRingerMode() {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        switch (nm.getCurrentInterruptionFilter()) {
            case NotificationManager.INTERRUPTION_FILTER_ALL:
                return getString(R.string.ring);
            case NotificationManager.INTERRUPTION_FILTER_NONE:
                return getString(R.string.silent);
            case NotificationManager.INTERRUPTION_FILTER_PRIORITY:
                return getString(R.string.do_not_disturb);
            case NotificationManager.INTERRUPTION_FILTER_ALARMS:
                return getString(R.string.alarm);
            case NotificationManager.INTERRUPTION_FILTER_UNKNOWN:
                return getString(R.string.unknown);
            default:
                return getString(R.string.unknown);
        }
    }
}