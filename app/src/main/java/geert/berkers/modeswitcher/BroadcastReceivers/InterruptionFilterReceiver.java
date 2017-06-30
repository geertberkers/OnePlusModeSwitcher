package geert.berkers.modeswitcher.BroadcastReceivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

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
        }
    }

    public void onInterruptionFilterChanged() {
        Log.i("ModeSwitcher", "onInterruptionFilterChanged()");
    }

    public void handleAlarmState() {
        Log.i("ModeSwitcher", "handleAlarmState()");
    }

    public void handleAllState() {
        Log.i("ModeSwitcher", "handleAllState()");
        setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public void handleNoneState() {
        Log.i("ModeSwitcher","handleNoneState()");
        setRingerMode(AudioManager.RINGER_MODE_SILENT);
    }

    public void handlePriorityState() {
        Log.i("ModeSwitcher","handlePriorityState()");
    }

    public void handleUnknownState() {
        Log.i("ModeSwitcher", "handleUnknownState()");
    }

    public void setRingerMode(int ringerMode) {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(ringerMode);
    }
}

