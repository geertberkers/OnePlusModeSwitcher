package geert.berkers.modeswitcher.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Zorgkluis (geert).
 */
public class AlertSliderService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        Log.i("AlertSliderService", "Service Bound");

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "AlertSliderService Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "AlertSliderService Destroyed!", Toast.LENGTH_LONG).show();
    }
}
