package geert.berkers.modeswitcher.InterruptionFilter;

/**
 * Created by Geert.
 */
public interface InterruptionFilterCallBack {
    void onInterruptionFilterChanged();

    void handleAlarmState();

    void handleNoneState();

    void handlePriorityState();

    void handleAllState();

    void handleUnknownState();
}