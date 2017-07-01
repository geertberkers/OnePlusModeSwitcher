package geert.berkers.modeswitcher.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import geert.berkers.modeswitcher.helper.ToastHelper;
import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.service.AlertSliderService;

/**
 * Created by Geert Berkers.
 */
public class CloseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "CloseReceiver - onReceive()");

        stopService(context);
        showStoppedNotification(context);

//        if(ContextManager.isAppProcessRunning(context)){
//            Log.i("CloseReceiver", "Process is Running!!");
//            Intent activity = new Intent(context, MainActivity.class);
//            activity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            activity.putExtra("receiverTask", "close");
//            context.startActivity(activity);
//        }
    }


    private void stopService(Context context) {
        Intent service = new Intent(context, AlertSliderService.class);
        context.stopService(service);
    }

    private void showStoppedNotification(Context context) {
        ToastHelper.makeText(context, context.getString(R.string.notification_closed), Toast.LENGTH_SHORT).show();
    }

}