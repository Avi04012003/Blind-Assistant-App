package com.example.ech;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
//mainactivity->langactivity->swipemenuactivity->moremenuactivity
public class MainActivity extends AppCompatActivity {
    MediaPlayer mp;
    private static final int SPLASH_TIME=7000;

    String[] PERMISSIONS = {
            android.Manifest.permission.READ_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    int PERMISSION_ALL = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playmenu();

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.finish();
                    Intent i = new Intent(MainActivity.this,LangActivity.class);
                    startActivity(i);
                }
            }, 7000);
        }
        else{
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    MainActivity.this.finish();
                    Intent i = new Intent(MainActivity.this,LangActivity.class);
                    startActivity(i);
                }
            }, 4000);
        }
    }

    private void playmenu() {
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.start);
            mp.start();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void launchApp(String packageName) {
        try {
            // Your code to launch the app goes here
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}