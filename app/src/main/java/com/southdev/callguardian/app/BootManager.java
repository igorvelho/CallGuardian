package com.southdev.callguardian.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by igor.velho on 08/12/2014.
 */
public class BootManager extends BroadcastReceiver {

    SharedPreferences sharedpreferences;
    public static final String configName = "Config" ;
    public static final String startOnBootPref = "startOnBoot";

    @Override
    public void onReceive(Context context, Intent intent) {

        sharedpreferences = context.getSharedPreferences(configName, Context.MODE_PRIVATE);
        if (sharedpreferences.getBoolean(startOnBootPref, true)) {
            if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                Intent pushIntent = new Intent(context, MainService.class);
                context.startService(pushIntent);
            }
        }
    }
}