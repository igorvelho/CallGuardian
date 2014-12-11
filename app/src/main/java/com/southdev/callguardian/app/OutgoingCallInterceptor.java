package com.southdev.callguardian.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by igorvelho on 22/04/2014.
 */
public class OutgoingCallInterceptor extends BroadcastReceiver {                            // 1

    @Override
    public void onReceive(Context context, Intent intent) {                                 // 2
        final String oldNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);          // 3
        this.setResultData("0123456789");                                                   // 4
        final String newNumber = this.getResultData();
        String msg = "Intercepted outgoing call. Old number " + oldNumber + ", new number " + newNumber;
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

}