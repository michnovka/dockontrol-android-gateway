package com.michnovka.dockontrol.androidgw.util;

import static android.content.Context.TELECOM_SERVICE;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.michnovka.dockontrol.androidgw.model.ServerResponse;

import java.util.HashMap;
import java.util.Map;

public class CallStateListener extends PhoneStateListener {

    private final Context mContext;
    private final Helper mHelper;
    private final SharedPreferenceHelper sharedPreferenceHelper;

    public CallStateListener(Context mContext) {
        this.mContext = mContext;
        mHelper = new Helper();
        sharedPreferenceHelper = new SharedPreferenceHelper(mContext);
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
        super.onCallStateChanged(state, phoneNumber);
        if (state == TelephonyManager.CALL_STATE_RINGING) {
            long time = System.currentTimeMillis();
            String api_secret = sharedPreferenceHelper.getSecret();
            String hash = mHelper.toSHA256(phoneNumber, time, api_secret);

            HashMap<String, String> params = new HashMap<>();
            params.put("caller_number", phoneNumber);
            params.put("time", String.valueOf(time));
            params.put("hash", hash);

            sendToServer(params);
        }
    }

    private void sendToServer(HashMap<String, String> params) {
        StringRequest request = new StringRequest(Request.Method.POST, "https://" + sharedPreferenceHelper.getUrl(), new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                try {
                    ServerResponse serverResponse = gson.fromJson(response, ServerResponse.class);
                    if (serverResponse.getAction().equalsIgnoreCase("hung-up")) {
                        hangUpCall();
                    }
                } catch (Exception e) {
                    System.out.println(e.getLocalizedMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("onErrorResponse: ", error.getLocalizedMessage());
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                if (response.statusCode == 200) {
                    Log.d("parseNetworkResponse: ", "data posted!");
                }
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private void hangUpCall() {
        TelecomManager telecomManager = (TelecomManager) mContext.getSystemService(TELECOM_SERVICE);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (telecomManager.isInCall()) {
            telecomManager.endCall();
        }
    }

}
