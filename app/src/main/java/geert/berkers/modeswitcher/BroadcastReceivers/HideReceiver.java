package geert.berkers.modeswitcher.broadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import geert.berkers.modeswitcher.helper.ToastHelper;
import geert.berkers.modeswitcher.R;

/**
 * Created by Geert Berkers.
 */
public class HideReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        int notification = intent.getIntExtra("notification", 0);

        if (notification != 0) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notification);
            Log.i("ModeSwitcher", "onHideNotification()");
            ToastHelper.makeText(context, context.getString(R.string.notification_hidden), Toast.LENGTH_SHORT).show();
        }
    }
}