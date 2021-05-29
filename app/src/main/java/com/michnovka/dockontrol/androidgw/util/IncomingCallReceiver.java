package com.michnovka.dockontrol.androidgw.util;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class IncomingCallReceiver extends BroadcastReceiver {

    private final Context mContext;
    private static CallStateListener phoneListener;

    public IncomingCallReceiver(Context mContext) {
        this.mContext = mContext;
    }

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (phoneListener == null) {
            phoneListener = new CallStateListener(mContext);
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
