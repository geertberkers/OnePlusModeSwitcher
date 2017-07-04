package geert.berkers.modeswitcher.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import geert.berkers.modeswitcher.helper.PreferenceHelper;
import geert.berkers.modeswitcher.service.AlertSliderService;

import static geert.berkers.modeswitcher.helper.NotificationState.*;

/**
 * Created by Geert.
 */
public class UpdateReceiver extends BroadcastReceiver {

    private final static String PACKAGE_NAME = "package:geert.berkers.modeswitcher";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("ModeSwitcher", "UpdateReceiver: onReceive()");
        Uri data = intent.getData();
        String dataString = data.toString();

        if(dataString.equals(PACKAGE_NAME)) {
            setNotificationState(context);
            startService(context);
        }
    }

    private void setNotificationState(Context context) {
        PreferenceHelper.setNotificationState(context, UPDATED);
    }

    /**
     * Start AlertSliderService
     * @param context for starting service
     */
    private void startService(Context context){
        Intent i = new Intent(context, AlertSliderService.class);
        context.startService(i);
    }
}