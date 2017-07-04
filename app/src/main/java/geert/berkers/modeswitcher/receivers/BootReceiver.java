package geert.berkers.modeswitcher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import geert.berkers.modeswitcher.helper.PreferenceHelper;
import geert.berkers.modeswitcher.service.AlertSliderService;

import static geert.berkers.modeswitcher.helper.NotificationState.*;

/**
 * Created by Geert Berkers.
 */
public class BootReceiver extends BroadcastReceiver {

    public final static String RESTART_APP = "geert.berkers.modeswitcher.restartApp";
    private final static String BOOT_COMPLETE = Intent.ACTION_BOOT_COMPLETED;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "BootReceiver: onReceive()");
        String action = intent.getAction();

        Log.i("ModeSwitcher", "BootReceiver action: " + action);
        if (action.equals(BOOT_COMPLETE)) {
            setNotificationState(context, BOOTED);
            checkStartBoot(context);
        } else if (action.equals(RESTART_APP)) {
            setNotificationState(context, RESTART);
            startService(context);
        }
    }

    /**
     * Check if app has to start on boot
     * @param context for accessing SharedPreferences
     */
    private void checkStartBoot(Context context) {
        boolean startOnBoot = PreferenceHelper.getBoolean(context, "startOnBoot", false);

        if (startOnBoot) {
            startService(context);
        } else {
            Log.i("ModeSwitcher", "startOnBoot not Enabled!");
        }
    }

    /**
     * Start AlertSliderService
     * @param context for starting service
     */
    private void startService(Context context){
        Intent i = new Intent(context, AlertSliderService.class);
        context.startService(i);
    }

    private void setNotificationState(Context context, String state){
        PreferenceHelper.setNotificationState(context, state);
    }
}