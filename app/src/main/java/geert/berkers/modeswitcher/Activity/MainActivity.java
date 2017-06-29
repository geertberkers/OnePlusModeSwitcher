package geert.berkers.modeswitcher.Activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import geert.berkers.modeswitcher.InterruptionFilter.InterruptionFilterCallBack;
import geert.berkers.modeswitcher.InterruptionFilter.InterruptionFilterReceiver;
import geert.berkers.modeswitcher.R;

public class MainActivity extends AppCompatActivity {

    public static int CALL_PERMISSION = 1;

    private static final ComponentName LAUNCHER_COMPONENT_NAME =
            new ComponentName(
                    "geert.berkers.modeswitcher",
                    "geert.berkers.modeswitcher.Activity.MainActivity");

    private InterruptionFilterReceiver interruptionFilterReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initControls();
        initFloatingActionButton();
        initInterruptionFilterReceiver();
    }

    private void initControls() {
        Button btnHide = (Button) findViewById(R.id.btnHide);
        btnHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideIcon();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initInterruptionFilterReceiver() {
        interruptionFilterReceiver = new InterruptionFilterReceiver(this);
        interruptionFilterReceiver.setCallBack(new InterruptionFilterCallBack() {
            @Override
            public void onInterruptionFilterChanged() {
                Log.i("ModeSwitcher", "onInterruptionFilterChanged()");
            }

            @Override
            public void handleAlarmState() {
                Log.i("ModeSwitcher", "handleAlarmState()");
            }

            @Override
            public void handleAllState() {
                Log.i("ModeSwitcher", "handleAllState()");
                setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }

            @Override
            public void handleNoneState() {
                Log.i("ModeSwitcher","handleNoneState()");
                setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }

            @Override
            public void handlePriorityState() {
                Log.i("ModeSwitcher","handlePriorityState()");
            }

            @Override
            public void handleUnknownState() {
                Log.i("ModeSwitcher", "handleUnknownState()");
            }
        });

        interruptionFilterReceiver.registerReceiver();
    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                minimizeApplication();
            }
        });
    }

    /**
     * Minimize the application so the code still works.
     */
    private void minimizeApplication() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    /**
     * Show AlertDialog with information to call number
     */
    private void hideIcon(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.important);
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage(R.string.reopen_app);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doHide();
            }
        });
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.show();
    }

    private void doHide() {
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.PROCESS_OUTGOING_CALLS);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS}, CALL_PERMISSION);
        } else {

            minimizeApplication();

            getPackageManager().setComponentEnabledSetting(LAUNCHER_COMPONENT_NAME,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALL_PERMISSION) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doHide();
            } else {
                Toast.makeText(getApplicationContext(), R.string.hide_condition, Toast.LENGTH_LONG).show();
            }
        }
    }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("ModeSwitcher", "onDestroy()");
        interruptionFilterReceiver.unregisterReceiver();
    }

    /**
     * Set AudioMangers RingerMode
     * @param ringerMode AudioManager.RingerMode
     */
    private void setRingerMode(int ringerMode){

        // TODO: Decide if we want to change do not disturb mode
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
//                && !notificationManager.isNotificationPolicyAccessGranted()) {
//            Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
//            startActivity(intent);
//        }

        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        am.setRingerMode(ringerMode);

    }

    /**
     * Handle onCackPressed. Ask user to close or minimize app
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.warning);
        alertDialogBuilder.setIcon(R.drawable.ic_warning);
        alertDialogBuilder.setMessage(R.string.close_message);
        alertDialogBuilder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alertDialogBuilder.setPositiveButton(R.string.minimize, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                minimizeApplication();
                dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.show();
    }

}
