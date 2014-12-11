package com.southdev.callguardian.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by igorvelho on 22/04/2014.
 */
public class MainService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    boolean isBottomUp = false;
    DevicePolicyManager deviceManger;
    ComponentName compName;

    @Override
    public IBinder onBind(Intent arg0) {


        return null;
    }

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size()!=0){
            Sensor s = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this,s, SensorManager.SENSOR_DELAY_NORMAL);
            Sensor sProx = sensorManager.getSensorList(Sensor.TYPE_PROXIMITY).get(0);
            sensorManager.registerListener(this,sProx, SensorManager.SENSOR_DELAY_NORMAL);
        }
        deviceManger = (DevicePolicyManager)getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        compName = new ComponentName(this, DeviceManager.class);
        registerReceiver(receiver, filter);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Toast.makeText(this, getString(R.string.service_started), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, getString(R.string.service_stopped), Toast.LENGTH_LONG).show();
        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isBottomUp) {
                Toast.makeText(context, getString(R.string.call_blocked), Toast.LENGTH_LONG).show();
                this.setResultData("");

                if(deviceManger.isAdminActive(compName))
                    deviceManger.lockNow();
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            getAccelerometer(event);
        }
        else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            getProximity(event);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void getProximity(SensorEvent sensorEvent) {
        float cms = sensorEvent.values[0];
        if(cms < 5)
        {
            isBottomUp = true;
        }
        else
            isBottomUp = false;
    }

    private void getAccelerometer(SensorEvent sensorEvent) {

        float azimuth = sensorEvent.values[0];
        float pitch = sensorEvent.values[1];
        float roll = sensorEvent.values[2];
        if (pitch < -45 && pitch > -135) {
            //orient.setText("Top side of the phone is Up!");
            isBottomUp = false;

        } else if (pitch > 45 && pitch < 135) {

            //orient.setText("Bottom side of the phone is Up!");
            isBottomUp = true;

        } else if (roll > 45) {

            //orient.setText("Right side of the phone is Up!");
            isBottomUp = false;

        } else if (roll < -45) {

            //orient.setText("Left side of the phone is Up!");
            isBottomUp = false;
        }
    }
}

