package com.example.ech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class LangActivity extends AppCompatActivity {
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    public static final Integer RecordAudioRequestCode = 1;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;
    TextToSpeech textToSpeechhindi;
    @SuppressLint("ClickableViewAccessibility")
    private static final long DOUBLE_CLICK_INTERVAL = 300; // 300 milliseconds
    private long lastVolumeDownTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lang);

        //record audio permissions
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
            }
        }
        playmenu();

        //texttospeech//
        textToSpeechhindi = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeechhindi.setLanguage(new Locale("hi","IN"));
                    textToSpeechhindi.setPitch(0.8f);
                    textToSpeechhindi.setSpeechRate(1.1f);
                }
            }
        });

        card1 = (CardView) findViewById(R.id.card1);
        t1 = (TextView) findViewById(R.id.text1);
        //speech recognization code
        speechrecog = SpeechRecognizer.createSpeechRecognizer(this);
        speechrecogintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechrecogintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechrecogintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechrecog.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null) {
                    t1.setText(matches.get(0).toLowerCase());
                    if (t1.getText().toString().equals("play again")) {
                       mp.setLooping(true);//nothing happens
                    } else if (t1.getText().toString().equals("hindi")) {
                        Intent i = new Intent(LangActivity.this, SwipeMenuActivity.class);
                        i.putExtra("Language",t1.getText().toString());
                        startActivity(i);
                        finish();
                    }else if (t1.getText().toString().equals("english")) {
                        Intent i = new Intent(LangActivity.this, SwipeMenuActivity.class);
                        i.putExtra("Language",t1.getText().toString());
                        startActivity(i);
                        finish();
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        card1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        if(!mp.isPlaying()) {
                            mp.start();
                        }
                        speechrecog.stopListening();
                        t1.setText(" Press and Speak ");
                        break;
                    case MotionEvent.ACTION_DOWN:
                        if(mp.isPlaying()) {
                            mp.pause();
                        }
                        t1.setText("");
                        t1.setText(" Listening...");
                        speechrecog.startListening(speechrecogintent);
                        break;
                }
                return false;
            }
        });
    }

    public void playmenu() {
        if (mp == null) {
            mp = MediaPlayer.create(this, R.raw.langmenu);
            mp.start();
        }
    }


    @Override
    protected void onUserLeaveHint() {
        mp.stop();
        super.onUserLeaveHint();
    }

    public void onBackPressed() {
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            mp.stop();
            super.onBackPressed();
            finish();
        } else {
            mp.stop();
            textToSpeechhindi.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
        }
        pressedTime = System.currentTimeMillis();
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (action == KeyEvent.ACTION_DOWN) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastVolumeDownTime < DOUBLE_CLICK_INTERVAL) {
                    // Double click detected, start SOS activity here
                    startSOSActivity();
                    return true; // consume the event
                }
                lastVolumeDownTime = currentTime;
            }
        }

        return super.dispatchKeyEvent(event);
    }

    private void startSOSActivity() {
        // Start your SOS activity here
        Intent sosIntent = new Intent(this, SOS.class);
        startActivity(sosIntent);
    }
}