package com.example.ech.alarm;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ech.R;
import com.example.ech.OnSwipeTouchListener;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class Alarm_Notification extends AppCompatActivity {
RelativeLayout touch;
MediaPlayer mp;
    @SuppressLint({"ClickableViewAccessibility", "InvalidWakeLockTag"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_notification);
        PowerManager.WakeLock wakeLock = null;
        KeyguardManager kgMgr = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        boolean locked = kgMgr.inKeyguardRestrictedInputMode();
        PowerManager pm = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MyWakeLock");
            wakeLock.acquire();
        }
        if (locked) {
            Window mWindow = getWindow();
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            mWindow.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindialarm_noti);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishalarm_noti);
                mp.start();
                mp.setLooping(true);
            }
        }

        touch=(RelativeLayout) findViewById(R.id.touchscreen);
        touch.setOnTouchListener(new OnSwipeTouchListener(Alarm_Notification.this) {
            @Override
            public void onSwipeBottom() {
                mp.stop();
                finishAffinity();
                super.onSwipeBottom();
            }
        });

        }
    public void startWakeup() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                touch.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LOW_PROFILE |
                                View.SYSTEM_UI_FLAG_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                );
            }
        });
    }

    public void stopWakeup() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                touch.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_VISIBLE
                );
            }
        });
    }

    }