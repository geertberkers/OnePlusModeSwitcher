package geert.berkers.modeswitcher.InterruptionFilter;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Geert.
 */
public class InterruptionFilterReceiver {

    private final Context context;
    private final IntentFilter intentFilter;

    private Receiver receiver;
    private InterruptionFilterCallBack callBack;

    public InterruptionFilterReceiver(Context context) {
        this.context = context;
        intentFilter = new IntentFilter(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED);
    }

    public void setCallBack(InterruptionFilterCallBack listener) {
        callBack = listener;
        receiver = new Receiver();
    }

    public void registerReceiver() {
        if (receiver != null) {
            context.registerReceiver(receiver, intentFilter);
        }
    }

    public void unregisterReceiver() {
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
    }

    class Receiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)) {
                if (callBack != null) {
                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                    switch (nm.getCurrentInterruptionFilter()) {
                        case NotificationManager.INTERRUPTION_FILTER_ALL:       callBack.handleAllState();      break;
                        case NotificationManager.INTERRUPTION_FILTER_NONE:      callBack.handleNoneState();     break;
                        case NotificationManager.INTERRUPTION_FILTER_PRIORITY:  callBack.handlePriorityState(); break;
                        case NotificationManager.INTERRUPTION_FILTER_ALARMS:    callBack.handleAlarmState();    break;
                        case NotificationManager.INTERRUPTION_FILTER_UNKNOWN:   callBack.handleUnknownState();  break;
                        default: callBack.onInterruptionFilterChanged();                                        break;
                    }
                }
            }
        }
    }
}