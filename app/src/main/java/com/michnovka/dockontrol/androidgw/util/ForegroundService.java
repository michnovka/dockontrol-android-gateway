package com.michnovka.dockontrol.androidgw.util;

import static com.michnovka.dockontrol.androidgw.App.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.michnovka.dockontrol.androidgw.R;
import com.michnovka.dockontrol.androidgw.ui.MainActivity;

public class ForegroundService extends Service {

    public static boolean isRunning = false;
    public static String number = "null";
    private ComponentName component;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        component = new ComponentName(getApplicationContext(), IncomingCallReceiver.class);
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

        getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        startForeground(1, notification);

        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        isRunning = false;
        getApplicationContext().getPackageManager().setComponentEnabledSetting(component, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        super.onDestroy();
    }
}
