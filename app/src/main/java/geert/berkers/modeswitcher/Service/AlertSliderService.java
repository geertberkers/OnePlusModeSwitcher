package geert.berkers.modeswitcher.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.activity.MainActivity;
import geert.berkers.modeswitcher.receivers.BootReceiver;
import geert.berkers.modeswitcher.receivers.CloseReceiver;
import geert.berkers.modeswitcher.receivers.HideReceiver;
import geert.berkers.modeswitcher.receivers.InterruptionFilterReceiver;
import geert.berkers.modeswitcher.helper.PreferenceHelper;

import static geert.berkers.modeswitcher.helper.NotificationState.*;

/**
 * Created by Geert.
 */
public class AlertSliderService extends Service {

    private final static String TAG = "ModeSwitcher";
    private final static String SERVICE = "AlertSliderService: ";

    private boolean isRegistered = false;

    private NotificationManager mNM;
    private InterruptionFilterReceiver interruptionFilterReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, SERVICE + "onStartCommand()");

        initNotificationManager();
        initInterruptionFilterReceiver();
        initNotification();

        return START_STICKY;
    }

    /**
     * Initialize Notification for Service
     */
    private void initNotificationManager() {
        if (mNM == null) {
            mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        }
    }

    private void initInterruptionFilterReceiver() {
        String state = PreferenceHelper.getString(this, NOTIFICATION_STATE, UNKNOWN);

        switch (state) {
            case UNKNOWN:
            case ENABLED:
            case RESTART:
            case BOOTED:
            case HIDDEN:
                registerInterruptionFilterReceiver();
                break;
            case DISABLED:
            case UPDATED:
            case STOPPED:
                Log.i(TAG, SERVICE + "Not registering InterruptionFilter for state: " + state);
                break;
        }
    }

    /**
     * Initialize InterruptionFilterReceiver for Service
     */
    private void registerInterruptionFilterReceiver() {
        if (!isRegistered) {
            interruptionFilterReceiver = new InterruptionFilterReceiver();
            IntentFilter intentFilter = new IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);
            registerReceiver(interruptionFilterReceiver, intentFilter);
            isRegistered = true;
        }
    }

    private void initNotification() {
        String state = PreferenceHelper.getString(this, NOTIFICATION_STATE, UNKNOWN);
        boolean showNotification = PreferenceHelper.getBoolean(this, "showNotification", true);

        if (showNotification) {
            switch (state) {
                case BOOTED:
                case UNKNOWN:
                case ENABLED:
                case RESTART:
                    showNotification();
                    break;
                case UPDATED:
                    showUpdatedNotification();
                    break;
                case STOPPED:
                    showStoppedNotification();
                    break;
                default:
                    Log.d(TAG, SERVICE + "InitNotification() state =" + state);
            }
        } else {
            stopNotification();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, SERVICE + "onDestroy()");
        handleOnDestroyNotification();
        handleOnDestroyUnregisterReceiver();
        PreferenceHelper.setNotificationState(this, STOPPED);
    }

    /**
     * Handle notification for onDestroy
     */
    private void handleOnDestroyNotification() {
        boolean showStoppedNotification = PreferenceHelper.getBoolean(this, "showStoppedNotification", true);

        if (showStoppedNotification) {
            showStoppedNotification();
        } else {
            stopNotification();
        }
    }

    /**
     * Handle unregisterReceiver for onDestroy
     */
    private void handleOnDestroyUnregisterReceiver() {
        if (isRegistered) {
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
     * Show a notification when this app is updated.
     */
    private void showUpdatedNotification() {
        Log.i(TAG, SERVICE + "showUpdatedNotification()");
        PendingIntent contentIntent = createContentIntent();

        // Hide notification action
        Intent restartServiceIntent = new Intent(this, BootReceiver.class);
        restartServiceIntent.setAction(BootReceiver.RESTART_APP);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, restartServiceIntent, 0);
        NotificationCompat.Action restartAction = new NotificationCompat.Action.Builder(R.drawable.ic_restart, "Restart", restartPendingIntent).build();

        // Build notification
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getText(R.string.service_updated))
                .setContentText(getString(R.string.click_open_app))
                .setContentIntent(contentIntent)
                .addAction(restartAction)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /**
     * Show a notification when this service stopped.
     */
    private void showStoppedNotification() {
        Log.i(TAG, SERVICE + "showStoppedNotification()");
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
        Log.i(TAG, SERVICE + "showNotification()");
        CharSequence text = getString(R.string.current_mode) + " " + getRingerMode();

        PendingIntent contentIntent = createContentIntent();
        NotificationCompat.Action hideAction = createHideAction();
        NotificationCompat.Action closeAction = createCloseAction();

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

    /**
     * Create content intent for opening notification
     *
     * @return intent for notification
     */
    private PendingIntent createContentIntent() {
        return PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
    }

    /**
     * Create close action for notification
     */
    private NotificationCompat.Action createCloseAction() {
        Intent closeIntent = new Intent(this, CloseReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, 0);
        return new NotificationCompat.Action.Builder(R.drawable.ic_stop, getString(R.string.close), closePendingIntent).build();
    }

    /**
     * Create hide action for notification
     */
    private NotificationCompat.Action createHideAction() {
        Intent hideIntent = new Intent(this, HideReceiver.class);
        hideIntent.putExtra("notification", NOTIFICATION);
        PendingIntent hidePendingIntent = PendingIntent.getBroadcast(this, 0, hideIntent, 0);
        return new NotificationCompat.Action.Builder(R.drawable.ic_hide, getString(R.string.hide), hidePendingIntent).build();
    }

    /**
     * Get the current RingerMode as String
     *
     * @return ringerMode
     */
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
