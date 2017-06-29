package geert.berkers.modeswitcher.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import geert.berkers.modeswitcher.Activity.MainActivity;

/**
 * Created by Zorgkluis (geert).
 */
public class LaunchReceiver extends BroadcastReceiver {

    private final static String LAUNCHER_NUMBER = "*9999#";

    private static final ComponentName LAUNCHER_COMPONENT_NAME = new ComponentName(
            "geert.berkers.modeswitcher", "geert.berkers.modeswitcher.Activity.MainActivity");

    @Override
    public void onReceive(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

        if (LAUNCHER_NUMBER.equals(phoneNumber)) {
            unHideIcon(context);
            startApp(context);
        }
    }

    private void startApp(Context context) {
        Intent appIntent = new Intent(context, MainActivity.class);
        appIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(appIntent);
    }

    private void unHideIcon(Context context) {
        if (!isLauncherIconVisible(context)) {
            context.getPackageManager().setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    private boolean isLauncherIconVisible(Context context) {
        int enabledSetting = context.getPackageManager().getComponentEnabledSetting(LAUNCHER_COMPONENT_NAME);
        return enabledSetting != PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
    }
}