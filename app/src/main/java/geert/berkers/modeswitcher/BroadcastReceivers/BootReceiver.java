package geert.berkers.modeswitcher.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import geert.berkers.modeswitcher.Service.AlertSliderService;

/**
 * Created by Zorgkluis (geert).
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "BootReceiver - onReceive()");
        Intent i = new Intent(context, AlertSliderService.class);
        context.startService(i);
    }

}