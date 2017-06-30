package geert.berkers.modeswitcher.Activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.Service.AlertSliderService;

public class MainActivity extends AppCompatActivity {

    private boolean mIsBound;

    private AlertSliderService mBoundService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initControls();
        initFloatingActionButton();
        initService();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initControls() {
        Button btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(getServiceIntent());
            }
        });

        Button btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(getServiceIntent());
            }
        });
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initService() {
        startService(getServiceIntent());
    }

    private Intent getServiceIntent(){
        return new Intent(this, AlertSliderService.class);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ModeSwitcher", "onStart()");
        doBindService();
    }

    void doBindService() {
        Log.i("ModeSwitcher", "doBindService()");
        bindService(new Intent(MainActivity.this, AlertSliderService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ModeSwitcher", "onStop()");
        doUnbindService();
    }

    void doUnbindService() {
        if (mIsBound) {
            Log.i("ModeSwitcher", "doUnbindService()");
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("ModeSwitcher", "onServiceConnected()");
            mBoundService = ((AlertSliderService.LocalBinder)service).getService();

            if (isAlertSliderServiceRunning()) {
                mBoundService.showNotification();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            Log.i("ModeSwitcher", "onServiceDisconnected()");
            mBoundService = null;
        }
    };

    /**
     * Check if the AlertSliderService is running
     * @return true if running, false if not
     */
    private boolean isAlertSliderServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (AlertSliderService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
