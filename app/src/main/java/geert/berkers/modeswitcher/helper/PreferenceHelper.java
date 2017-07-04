package geert.berkers.modeswitcher.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static geert.berkers.modeswitcher.helper.NotificationState.NOTIFICATION_STATE;

/**
 * Created by Geert.
 */
public class PreferenceHelper {

    /**
     * Get SharedPreferences
     * @return defaultSharedPreferences
     */
    private static SharedPreferences getSharedPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * Set NotificationState to Enabled
     */
    public static void setNotificationState(Context context, String state) {
        getSharedPreferences(context).edit().putString(NOTIFICATION_STATE, state).apply();
    }

    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        return getSharedPreferences(context).getBoolean(key, defaultValue);
    }

    public static String getString(Context context, String key, String defaultValue) {
        return getSharedPreferences(context).getString(key, defaultValue);
    }

    public static void registerOnSharedPreferenceChangeListener(
            Context context,
            SharedPreferences.OnSharedPreferenceChangeListener listener) {
        getSharedPreferences(context).registerOnSharedPreferenceChangeListener(listener);
    }
}
