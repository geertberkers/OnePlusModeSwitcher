package geert.berkers.modeswitcher.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.helper.ToastHelper;

import static geert.berkers.modeswitcher.helper.NotificationState.*;

/**
 * Created by Geert Berkers.
 */
public class HideReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "HideReceiver: onReceive()");
        int notification = intent.getIntExtra("notification", 0);

        if (notification != 0) {
            Log.i("ModeSwitcher", "HideReceiver: onHideNotification()");
            cancelNotification(context, notification);
            setNotificationState(context);
            showToast(context);
        }
    }

    private void cancelNotification(Context context, int notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification);
    }

    private void setNotificationState(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putString(NOTIFICATION_STATE, HIDDEN).apply();
    }

    private void showToast(Context context) {
        ToastHelper.makeText(context, context.getString(R.string.notification_hidden), Toast.LENGTH_LONG).show();
    }
}