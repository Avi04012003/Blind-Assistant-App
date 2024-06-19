package com.example.ech.currency;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.example.ech.LangActivity;
import com.example.ech.MoreMenuActivity;
import com.example.ech.R;
import com.example.ech.SwipeMenuActivity;

import java.util.ArrayList;
import java.util.Locale;

public class Currency_Activity extends AppCompatActivity {
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    public static final Integer RecordAudioRequestCode = 1;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;
    TextToSpeech textToSpeechhindi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);
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
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
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
                    } else if (t1.getText().toString().equals("detect currency")) {
                        Intent i = new Intent(Currency_Activity.this, ClassifierActivity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }else if (t1.getText().toString().equals("back")) {
                        Intent i = new Intent(Currency_Activity.this, MoreMenuActivity.class);
                        i.putExtra("Language",lang);
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
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindicurrency);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishcurrency);
                mp.start();
            }
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
            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");
            if(lang.equals("hindi")) {
                textToSpeechhindi.speak("बाहर निकलने के लिए फिर से दबाएं", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                textToSpeechhindi.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
            }
        }
        pressedTime = System.currentTimeMillis();
    }
}