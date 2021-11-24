package com.michnovka.dockontrol.androidgw.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.michnovka.dockontrol.androidgw.R;
import com.michnovka.dockontrol.androidgw.util.ForegroundService;
import com.michnovka.dockontrol.androidgw.util.SharedPreferenceHelper;

public class MainActivity extends AppCompatActivity {

    private SharedPreferenceHelper sharedPreference;
    private TextInputLayout mApiUrl, mApiSecret;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch mAppStatus, mStartOnReboot;
    private Button mSave;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreference = new SharedPreferenceHelper(this);
        sharedPreference.setStatus(ForegroundService.isRunning);

        initComponents();
        initFields();
        askForPermission(Manifest.permission.READ_CALL_LOG, 1001);
        askForPermission(Manifest.permission.READ_PHONE_STATE, 1002);
        askForPermission(Manifest.permission.ANSWER_PHONE_CALLS, 1003);
    }

    private void initComponents() {
        mApiUrl = findViewById(R.id.text_url);
        mApiSecret = findViewById(R.id.text_secret);
        mAppStatus = findViewById(R.id.switch_status);
        mSave = findViewById(R.id.btn_save);
        mStartOnReboot = findViewById(R.id.switch_boot);

    }

    private void initFields() {
        String url = sharedPreference.getUrl();
        String secret = sharedPreference.getSecret();
        boolean status = sharedPreference.getStatus();

        mApiUrl.getEditText().setText(url);
        mApiSecret.getEditText().setText(secret);
        mAppStatus.setChecked(status);
        mStartOnReboot.setChecked(sharedPreference.getBoot());
    }

    public void saveData(View view) {
        String url = mApiUrl.getEditText().getText().toString();
        String secret = mApiSecret.getEditText().getText().toString();
        boolean status = mAppStatus.isChecked();
        boolean boot = mStartOnReboot.isChecked();

        if (TextUtils.isEmpty(url)) {
            mApiUrl.setError("Please enter url");
        } else if (TextUtils.isEmpty(secret)) {
            mApiSecret.setError("Please enter secret");
        } else {
            if (sharedPreference.setUrl(url) && sharedPreference.setSecret(secret) && sharedPreference.setStatus(status) && sharedPreference.setBoot(boot)) {
                Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        }

        Intent intent = new Intent(MainActivity.this, ForegroundService.class);
        if (status) {
            ContextCompat.startForegroundService(MainActivity.this, intent);
        } else {
            stopService(intent);
        }
    }

    private void askForPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, permission)) {
                Toast.makeText(getApplicationContext(), "Please grant the requested permission to get your task done!", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission}, requestCode);
        }
    }
}