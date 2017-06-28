package geert.berkers.modeswitcher;

import android.content.Context;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import geert.berkers.modeswitcher.receivers.RingerModeReceiver;

public class MainActivity extends AppCompatActivity implements RingerModeReceiver.RingerModeCallBack {

    private final static boolean normalToSilent = true;

    private RingerModeReceiver ringerModeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initRingerModeReceiver();

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRingerModeReceiver() {
        ringerModeReceiver = new RingerModeReceiver(this);

        IntentFilter filter = new IntentFilter(Context.NOTIFICATION_SERVICE);
        ringerModeReceiver.registerReceiver(filter);
    }

//    @SuppressWarnings("unused")
//    private void initFloatingActionButton() {
//                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("ModeSwitcher", "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ModeSwitcher", "onResume()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("ModeSwitcher", "onRestart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("ModeSwitcher", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("ModeSwitcher", "onDestroy()");
        ringerModeReceiver.unRegisterReceiver();
    }

    @Override
    public void handleAlarmState() {
        Log.i("ModeSwitcher", "handleAlarmState()");
    }

    @Override
    public void handleAllState() {
        if (normalToSilent){
            AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

            switch (am.getRingerMode()) {
                case AudioManager.RINGER_MODE_NORMAL:
                    Log.i("ModeSwitcher","Normal mode");
                    break;
                case AudioManager.RINGER_MODE_VIBRATE:
                    Log.i("ModeSwitcher","Vibrate mode");
                    break;
                case AudioManager.RINGER_MODE_SILENT:
                    Log.i("ModeSwitcher","Silent mode");
                    am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
            }
        }
    }

    @Override
    public void handleNoneState() {
        Log.i("ModeSwitcher", "handleNoneState()");
    }

    @Override
    public void handlePriorityState() {
        Log.i("ModeSwitcher", "handlePriorityState()");
    }

    @Override
    public void handleUnknownState() {
        Log.i("ModeSwitcher", "handleUnknownState()");
    }
}
