package com.southdev.callguardian.app;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * Created by igorvelho on 22/04/2014.
 */
public class CallListener extends PhoneStateListener {


    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                //do what you want with the incoming number here:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:

                break;
            case TelephonyManager.CALL_STATE_IDLE:

                break;

        }
    }
}