package com.southdev.callguardian.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;


public class MainActivity extends Activity {

    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    static final int RESULT_ENABLE = 1;
    public static final String configName = "Config" ;
    SharedPreferences sharedpreferences;
    public static final String lockScreenPref = "lockScreen";
    public static final String startOnBootPref = "startOnBoot";
    public static final String firstBoot = "firstBoot";
    private CheckBox chkLockscreen;
    private CheckBox chkStartOnBoot;
    private TextView txtStatus;
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceManger = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager)getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, DeviceManager.class);

        sharedpreferences = getSharedPreferences(configName, Context.MODE_PRIVATE);

        setContentView(R.layout.activity_main);

        chkLockscreen = (CheckBox) findViewById(R.id.chkLockscreen);
        if (sharedpreferences.contains(lockScreenPref))
        {
            chkLockscreen.setChecked(sharedpreferences.getBoolean(lockScreenPref, false));
        }

        chkStartOnBoot = (CheckBox) findViewById(R.id.chkStartOnBoot);
        if (sharedpreferences.contains(startOnBootPref))
        {
            chkStartOnBoot.setChecked(sharedpreferences.getBoolean(startOnBootPref, false));
        }

        if(sharedpreferences.getBoolean(firstBoot, true))
        {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle(getString(R.string.about_title));
            alert.setMessage(getString(R.string.about_message));
            alert.setCancelMessage(null);
            alert.setIcon(R.drawable.guardianicon);
            alert.show();

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(firstBoot, false);
            editor.commit();
        }

        txtStatus = (TextView) findViewById(R.id.txtStatus);
        UpdateStatus();

        CheckSensor();
    }

    private void CheckSensor()
    {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle(getString(R.string.check_dialog_title));
            builder.setMessage(getString(R.string.check_dialog_message));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();
        }
    }

    private void UpdateStatus() {
        if(isMyServiceRunning()) {
            txtStatus.setText(getString(R.string.service_started));
            txtStatus.setTextColor(Color.BLACK);
        }
        else {
            txtStatus.setText(getString(R.string.service_stopped));
            txtStatus.setTextColor(Color.RED);
        }
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MainService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //start the service
    public void onClickStartService(View V)
    {   if(!isMyServiceRunning())
        {
            startService(new Intent(this, MainService.class));
        }
        UpdateStatus();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Log.i(getString(R.string.app_name), getString(R.string.admin_enabled));
                } else {
                    Log.i(getString(R.string.app_name), getString(R.string.admin_disabled));
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //Stop the started service
    public void onClickStopService(View V)
    {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        if(isMyServiceRunning())
            stopService(new Intent(this, MainService.class));
        UpdateStatus();
    }

    public void onLockScreenClick(View v) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((CheckBox) v).isChecked()) {
            editor.putBoolean(lockScreenPref, true);
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.warn_message));
            startActivityForResult(intent, RESULT_ENABLE);
        }
        else {
            deviceManger.removeActiveAdmin(compName);
            editor.putBoolean(lockScreenPref, false);
        }
        editor.commit();
    }

    public void onStartBootClick(View v) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if (((CheckBox) v).isChecked()) {
            editor.putBoolean(startOnBootPref, true);
        }
        else {
            editor.putBoolean(startOnBootPref, false);
        }
        editor.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            AlertDialog alert = new AlertDialog.Builder(MainActivity.this).create();
            alert.setTitle(getString(R.string.about_title));
            alert.setMessage(getString(R.string.about_message));
            alert.setCancelMessage(null);
            alert.setIcon(R.drawable.guardianicon);
            alert.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
