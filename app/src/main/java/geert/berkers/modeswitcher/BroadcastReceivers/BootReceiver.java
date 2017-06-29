package geert.berkers.modeswitcher.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import geert.berkers.modeswitcher.Activity.MainActivity;
import geert.berkers.modeswitcher.Service.AlertSliderService;

/**
 * Created by Zorgkluis (geert).
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("geert.berkers.modeswitcher.Service.AlertSliderService");
        i.setClass(context, AlertSliderService.class);
        context.startService(i);
    }

}