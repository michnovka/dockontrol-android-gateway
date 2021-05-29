package com.michnovka.dockontrol.androidgw.util;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
            String time = mHelper.getCurrentTime();
            String api_secret = sharedPreferenceHelper.getSecret();

            HashMap<String, String> params = new HashMap<>();
            params.put("caller_number", mHelper.toSHA256(phoneNumber));
            params.put("time", mHelper.toSHA256(time));
            params.put("api_secret", mHelper.toSHA256(api_secret));

            sendToServer(params);
        }
    }

    private void sendToServer(HashMap<String, String> params) {
        StringRequest request = new StringRequest(Request.Method.POST, sharedPreferenceHelper.getUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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

}
