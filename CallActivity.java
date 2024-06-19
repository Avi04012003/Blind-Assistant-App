package com.example.ech.call;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.ech.R;
import com.example.ech.SwipeMenuActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Locale;

public class  CallActivity extends AppCompatActivity {
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;

    TextToSpeech textToSpeechhindi;
    ArrayList<String> StoreContactsName ;
    ArrayList<String> StoreContactsNum;
    Cursor cursor ;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        playmenu();
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");

        card1 = (CardView) findViewById(R.id.card1);
        t1 = (TextView) findViewById(R.id.text1);
        StoreContactsName = new ArrayList<String>();
        StoreContactsNum = new ArrayList<String>();
        //texttospeech//
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
                    int len =t1.getText().toString().length();
                    boolean result = onlyDigits(t1.getText().toString(), len);

                    if (t1.getText().toString().equals("play again")) {
                        playmenu();
                    }
                    else if (t1.getText().toString().equals("back")) {
                        Intent i = new Intent(CallActivity.this, SwipeMenuActivity.class);
                        i.putExtra("Language",lang);
                        startActivity(i);
                        finish();
                    }else if(result==true){//String contains mobile no
                        if (t1.getText().toString().trim().length() > 0) {
                            //call
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(lang.equals("hindi")) {
                                        textToSpeechhindi.speak("आपने यह number कहा है" + t1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                    else
                                    {
                                        textToSpeechhindi.speak("The number you have said is " + t1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                    }
                                } },2000);
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + t1.getText().toString()));//change the number
                            startActivity(callIntent);

                        }
                    }
                    else if (result==false) {//contacts

                        //to repeat the voice
                            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);
                            while (cursor.moveToNext()) {
                                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String lowername = name.toLowerCase();
                                if(lowername.equals(t1.getText().toString())){
                                    @SuppressLint("Range") String phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(lang.equals("hindi")) {
                                                textToSpeechhindi.speak("आपने यह Contact name कहा है" + t1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                            else
                                            {
                                                textToSpeechhindi.speak("The Contact name you have said is " + t1.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                                            }
                                        } },3000);
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + phonenumber));//change the number
                                    startActivity(callIntent);
                                }
                            }
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

    public static boolean onlyDigits(String str, int n)
    {
        for (int i = 0; i < n; i++) {

            if (Character.isDigit(str.charAt(i))) {
                return true;
            }
            else {
                return false;
            }
        }
        return false;
    }// to check whether the input string is number or char

    public void playmenu() {
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindicall);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishcall);
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