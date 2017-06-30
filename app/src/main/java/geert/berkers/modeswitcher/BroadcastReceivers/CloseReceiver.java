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
        Intent service = new Intent(context, AlertSliderService.class);
        context.stopService(service);
        ToastHelper.makeText(context, context.getString(R.string.notification_closed), Toast.LENGTH_SHORT).show();
    }
}