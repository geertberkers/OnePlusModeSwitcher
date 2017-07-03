package geert.berkers.modeswitcher.broadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import geert.berkers.modeswitcher.helper.ToastHelper;
import geert.berkers.modeswitcher.R;

/**
 * Created by Geert Berkers.
 */
public class HideReceiver extends BroadcastReceiver {

    private Context context;
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        int notification = intent.getIntExtra("notification", 0);

        if (notification != 0) {
            Log.i("ModeSwitcher", "onHideNotification()");
            cancelNotification(notification);
            setHiddenPreference();
            showToast();
        }
    }

    private void cancelNotification(int notification) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notification);
    }

    private void setHiddenPreference() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().putBoolean("hidden", true).apply();
    }

    private void showToast() {
        ToastHelper.makeText(context, context.getString(R.string.notification_hidden), Toast.LENGTH_SHORT).show();
    }
}