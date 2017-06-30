package geert.berkers.modeswitcher.Activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import geert.berkers.modeswitcher.R;
import geert.berkers.modeswitcher.Service.AlertSliderService;

public class MainActivity extends AppCompatActivity {

//    public static int CALL_PERMISSION = 1;
//
//    private static final ComponentName LAUNCHER_COMPONENT_NAME =
//            new ComponentName(
//                    "geert.berkers.modeswitcher",
//                    "geert.berkers.modeswitcher.Activity.MainActivity");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initControls();
        initFloatingActionButton();
//        initInterruptionFilterReceiver();

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

    private void initService() {
        if(isAlertSliderServiceRunning()){
            showServiceNotification();
        } else{
            startService(getServiceIntent());
        }
    }

    private void showServiceNotification() {
        
    }

    private Intent getServiceIntent(){
        return new Intent(this, AlertSliderService.class);
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

    //    Standard methods
//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.i("ModeSwitcher", "onStart()");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i("ModeSwitcher", "onResume()");
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        Log.i("ModeSwitcher", "onRestart()");
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Log.i("ModeSwitcher", "onStop()");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        Log.i("ModeSwitcher", "onDestroy()");
//    }

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
