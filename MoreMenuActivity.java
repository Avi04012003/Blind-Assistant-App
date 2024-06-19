package com.example.ech;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ech.currency.Currency_Activity;
import com.example.ech.object.Object_Activity;
import com.example.ech.weather.WeatherActivity;

import java.util.ArrayList;
import java.util.Locale;
//TODO: change audio of more menu
public class MoreMenuActivity extends AppCompatActivity {
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;
    TextToSpeech textToSpeechhindi;
    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_menu);
        playmenu();

        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");

        textToSpeechhindi = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeechhindi.setLanguage(new Locale("hi","IN"));
                    textToSpeechhindi.setPitch(0.9f);
                    textToSpeechhindi.setSpeechRate(0.8f);
                }
            }
        });
        card1 = (CardView) findViewById(R.id.card1);
        t1=(TextView)findViewById(R.id.text1);
        //speech recognization code
        speechrecog=SpeechRecognizer.createSpeechRecognizer(this);
        speechrecogintent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechrecogintent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
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
                if (matches != null)
                {
                    int count=0;
                    t1.setText(matches.get(0));
                    if (t1.getText().toString().equals("play again") ){
                        playmenu();
                    }
                    else if(t1.getText().toString().equals("detect"))
                    {
                        Intent i = new Intent(MoreMenuActivity.this, Object_Activity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }
                    else if(t1.getText().toString().equals("currency"))
                    {
                        Intent i = new Intent(MoreMenuActivity.this, Currency_Activity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }

                    else if(t1.getText().toString().equals("weather"))
                    {
                        Intent i = new Intent(MoreMenuActivity.this, WeatherActivity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }
                    else if(t1.getText().toString().equals("emergency"))
                    {
                        Intent i = new Intent(MoreMenuActivity.this, SOS.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }
                    else if(t1.getText().toString().equals("back"))
                    {
                        Intent i = new Intent(MoreMenuActivity.this,SwipeMenuActivity.class);
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

    //media player
    public void playmenu()
    {
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindimore);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.engmore);
                mp.start();
            }
        }
    }


    @Override
    protected void onUserLeaveHint()
    {
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