package geert.berkers.modeswitcher.helper;

import geert.berkers.modeswitcher.R;

/**
 * Created by Geert.
 */
public abstract class NotificationState {

    public final static int NOTIFICATION = R.string.alert_slider_service;

    public final static String NOTIFICATION_STATE = "notificationState";
    public final static String DISABLED = "disabled";
    public final static String ENABLED = "enabled";
    public final static String STOPPED = "stopped";
    public final static String RESTART = "restart";
    public final static String UPDATED = "updated";
    public final static String UNKNOWN = "unknown";
    public final static String BOOTED = "booted";
    public final static String HIDDEN = "hidden";
}
