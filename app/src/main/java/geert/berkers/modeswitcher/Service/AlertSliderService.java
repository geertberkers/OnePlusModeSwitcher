package geert.berkers.modeswitcher.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import geert.berkers.modeswitcher.Activity.MainActivity;
import geert.berkers.modeswitcher.BroadcastReceivers.BootReceiver;
import geert.berkers.modeswitcher.BroadcastReceivers.CloseReceiver;
import geert.berkers.modeswitcher.BroadcastReceivers.HideReceiver;
import geert.berkers.modeswitcher.BroadcastReceivers.InterruptionFilterReceiver;
import geert.berkers.modeswitcher.R;

/**
 * Created by Zorgkluis (geert).
 */
public class AlertSliderService extends Service {

    private NotificationManager mNM;

    private InterruptionFilterReceiver interruptionFilterReceiver;

    private final IBinder mBinder = new LocalBinder();

    private final int NOTIFICATION = R.string.alertslider_service;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public AlertSliderService getService() {
            return AlertSliderService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i("AlertSliderService", "Service Bound");
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("AlertSliderService", "onStartCommand()");

        initNotificationManager();
        initInterruptionFilterReceiver();
        showNotification();

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
        interruptionFilterReceiver = new InterruptionFilterReceiver();
        registerReceiver(interruptionFilterReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showStoppedNotification();
        unregisterReceiver(interruptionFilterReceiver);
        Log.i("AlertSliderService", "onDestroy()");
    }

    /**
     * Show a notification when this service stopped.
     */
    private void showStoppedNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(NOTIFICATION);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Hide notification action
        Intent restartServiceIntent = new Intent(this, BootReceiver.class);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, restartServiceIntent, 0);
        NotificationCompat.Action restartAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "Restart", restartPendingIntent).build();

        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)                 // The status icon
                .setTicker(text)                                    // The status text
                .setWhen(System.currentTimeMillis())                // The time stamp
                .setContentTitle(getText(R.string.service_stopped)) // The label of the entry
                .setContentText(text)                               // The contents of the entry
                .setContentIntent(contentIntent)                    // The intent to send when the entry is clicked
                .addAction(restartAction)                           // Add action to restart service
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

    /**
     * Show a notification while this service is running.
     */
    public void showNotification() {
        Log.i("AlertSliderService", "showNotification()");

        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(NOTIFICATION);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Hide notification action
        Intent hideIntent = new Intent(this, HideReceiver.class);
        hideIntent.putExtra("notification", NOTIFICATION);
        PendingIntent hidePendingIntent = PendingIntent.getBroadcast(this, 0, hideIntent, 0);
        NotificationCompat.Action hideAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "Hide", hidePendingIntent).build();

        // Close application and service
        Intent closeIntent = new Intent(this, CloseReceiver.class);
        PendingIntent closePendingIntent = PendingIntent.getBroadcast(this, 0, closeIntent, 0);
        NotificationCompat.Action closeAction = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher_round, "Close", closePendingIntent).build();


        // Set the info for the views that show in the notification panel.
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)                 // The status icon
                .setTicker(text)                                    // The status text
                .setWhen(System.currentTimeMillis())                // The time stamp
                .setContentTitle(getText(R.string.service_running)) // The label of the entry
                .setContentText(text)                               // The contents of the entry
                .setContentIntent(contentIntent)                    // The intent to send when the entry is clicked
                .addAction(hideAction)                              // Add action to hide notification
                .addAction(closeAction)                             // Add action to close app
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }

}