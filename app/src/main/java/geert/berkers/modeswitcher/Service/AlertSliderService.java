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
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Hide notification action
        Intent restartServiceIntent = new Intent(this, BootReceiver.class);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, restartServiceIntent, 0);
        NotificationCompat.Action restartAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Restart", restartPendingIntent).build();

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)                 // The status icon
                .setContentTitle(getText(R.string.service_stopped))           // The label of the entry
                .setContentText(getString(R.string.click_open_app)) // The contents of the entry
                .setContentIntent(contentIntent)                    // The intent to send when the entry is clicked
                .addAction(restartAction)                           // Add action to restart service
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        Log.i("AlertSliderService", "showNotification()");
        CharSequence text = getString(R.string.current_mode) + getRingerMode();

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Hide notification action
        Intent hideIntent = new Intent(this, HideReceiver.class);
        hideIntent.putExtra("notification", NOTIFICATION);
        PendingIntent hidePendingIntent = PendingIntent.getBroadcast(this, 0, hideIntent, 0);
        NotificationCompat.Action hideAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, getString(R.string.hide), hidePendingIntent).build();

        // Close application and service
        Intent closeIntent = new Intent(this, CloseReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, 0);
        NotificationCompat.Action closeAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, getString(R.string.close), closePendingIntent).build();

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)                 // The status icon
                .setContentTitle(getText(R.string.service_running))        // The label of the entry
                .setContentText(text)                               // The contents of the entry
                .setContentIntent(contentIntent)                    // The intent to send when the entry is clicked
                .addAction(hideAction)                              // Add action to hide notification
                .addAction(closeAction)                             // Add action to close app
                .setOngoing(true)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
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