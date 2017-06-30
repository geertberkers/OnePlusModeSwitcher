package geert.berkers.modeswitcher.broadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import geert.berkers.modeswitcher.service.AlertSliderService;

/**
 * Created by Geert.
 */
public class InterruptionFilterReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String action = intent.getAction();
        if (action.equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            switch (nm.getCurrentInterruptionFilter()) {
                case NotificationManager.INTERRUPTION_FILTER_ALL:       handleAllState();      break;
                case NotificationManager.INTERRUPTION_FILTER_NONE:      handleNoneState();     break;
                case NotificationManager.INTERRUPTION_FILTER_PRIORITY:  handlePriorityState(); break;
                case NotificationManager.INTERRUPTION_FILTER_ALARMS:    handleAlarmState();    break;
                case NotificationManager.INTERRUPTION_FILTER_UNKNOWN:   handleUnknownState();  break;
                default: onInterruptionFilterChanged();                                        break;
            }

            Intent i = new Intent(context, AlertSliderService.class);
            context.startService(i);
        }
    }

    private void onInterruptionFilterChanged() {
        Log.i("ModeSwitcher", "onInterruptionFilterChanged()");
    }

    private void handleAlarmState() {
        Log.i("ModeSwitcher", "handleAlarmState()");
    }

    private void handleAllState() {
        Log.i("ModeSwitcher", "handleAllState()");
        setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    private void handleNoneState() {
        Log.i("ModeSwitcher","handleNoneState()");
        setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    private void handlePriorityState() {
        Log.i("ModeSwitcher","handlePriorityState()");
    }

    private void handleUnknownState() {
        Log.i("ModeSwitcher", "handleUnknownState()");
    }

    private void setRingerMode(int ringerMode) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(ringerMode);
    }
}

