package geert.berkers.modeswitcher.receivers;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by Zorgkluis (geert).
 */

public class RingerModeReceiver {

    public interface RingerModeCallBack {
        void handleAlarmState();

        void handleAllState();

        void handleNoneState();

        void handlePriorityState();

        void handleUnknownState();
    }

    private final Context context;
    private RingerModeCallBack ringerModeCallBack;
    private RingerModeBroadcastReceiver ringerModeReceiver;

    public RingerModeReceiver(Context context){
        this.context = context;
        this.ringerModeCallBack = (RingerModeCallBack) context;
        this.ringerModeReceiver = new RingerModeBroadcastReceiver();
    }

    public void registerReceiver(IntentFilter intentFilter){
        if (ringerModeReceiver != null) {
            context.registerReceiver(ringerModeReceiver, intentFilter);
        }
    }

    public void unRegisterReceiver(){
        if (ringerModeReceiver != null) {
            context.unregisterReceiver(ringerModeReceiver);
        }
    }

    public class RingerModeBroadcastReceiver extends BroadcastReceiver {

        public RingerModeBroadcastReceiver(){}

        @Override
        public void onReceive(Context context, Intent intent) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            switch (nm.getCurrentInterruptionFilter()) {
                case NotificationManager.INTERRUPTION_FILTER_ALARMS:    ringerModeCallBack.handleAlarmState();     break;
                case NotificationManager.INTERRUPTION_FILTER_ALL:       ringerModeCallBack.handleAllState();       break;
                case NotificationManager.INTERRUPTION_FILTER_NONE:      ringerModeCallBack.handleNoneState();      break;
                case NotificationManager.INTERRUPTION_FILTER_PRIORITY:  ringerModeCallBack.handlePriorityState();  break;
                case NotificationManager.INTERRUPTION_FILTER_UNKNOWN:   ringerModeCallBack.handleUnknownState();   break;
            }
        }
    }
}