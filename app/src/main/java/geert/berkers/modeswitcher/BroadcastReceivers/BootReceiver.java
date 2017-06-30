package geert.berkers.modeswitcher.broadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import geert.berkers.modeswitcher.service.AlertSliderService;

/**
 * Created by Geert Berkers.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "BootReceiver - onReceive()");

        String action = intent.getAction();
        Log.i("ModeSwitcher", action);

        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean startOnBoot = preferences.getBoolean("startOnBoot", false);

            if (startOnBoot) {
                Intent i = new Intent(context, AlertSliderService.class);
                context.startService(i);
            } else {
                Log.i("ModeSwitcher", "startOnBoot not Enabled!");
            }
        }
    }

}