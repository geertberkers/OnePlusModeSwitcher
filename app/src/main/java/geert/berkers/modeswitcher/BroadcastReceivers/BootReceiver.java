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

    public final static String RESTART_APP = "geert.berkers.modeswitcher.restartApp";
    private final static String BOOT_COMPLETE = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "BootReceiver - onReceive()");

        String action = intent.getAction();

        if (action.equals(BOOT_COMPLETE)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            boolean startOnBoot = preferences.getBoolean("startOnBoot", false);

            if (startOnBoot) {
                startService(context);
            } else {
                Log.i("ModeSwitcher", "startOnBoot not Enabled!");
            }
        }
        if (action.equals(RESTART_APP)) {
            startService(context);
        }
    }

    private void startService(Context context){
        Intent i = new Intent(context, AlertSliderService.class);
        context.startService(i);
    }

}