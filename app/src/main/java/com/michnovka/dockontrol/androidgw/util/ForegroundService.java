package com.michnovka.dockontrol.androidgw.util;

import static com.michnovka.dockontrol.androidgw.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.michnovka.dockontrol.androidgw.R;
import com.michnovka.dockontrol.androidgw.ui.MainActivity;

public class ForegroundService extends Service {

    public static boolean isRunning = false;
    public static String number = "null";

    private Context mContext;
    private Helper mHelper;
    private SharedPreferenceHelper sharedPreferenceHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.mContext = getApplicationContext();
        mHelper = new Helper();
        sharedPreferenceHelper = new SharedPreferenceHelper(mContext);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        isRunning = true;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), flags);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Service Running!")
                .addAction(new NotificationCompat.Action(R.drawable.ic_baseline_open_in_new_24, "View", pendingIntent))
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .build();

        startForeground(1, notification);

//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.intent.action.PHONE_STATE");
//        registerReceiver(new IncomingCallReceiver(), filter);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}
