    package com.example.ech.alarm;

    import androidx.appcompat.app.AppCompatActivity;
    import androidx.cardview.widget.CardView;
    import android.annotation.SuppressLint;
    import android.app.AlarmManager;
    import android.app.PendingIntent;
    import android.content.Context;
    import android.content.Intent;
    import android.media.MediaPlayer;
    import android.os.Bundle;
    import android.speech.RecognitionListener;
    import android.speech.RecognizerIntent;
    import android.speech.SpeechRecognizer;
    import android.speech.tts.TextToSpeech;
    import android.view.MotionEvent;
    import android.view.View;
    import android.widget.GridLayout;
    import android.widget.TextView;

    import com.example.ech.SwipeMenuActivity;

    import java.util.ArrayList;
    import java.util.Calendar;

    import com.example.ech.R;

    import java.util.Locale;


    public class AlarmActivity extends AppCompatActivity {
        AlarmManager alarmManager;
        PendingIntent pendingIntent;
        MediaPlayer mp;
        GridLayout touchscreen;
        CardView card1;
        TextView t1;
        SpeechRecognizer speechrecog;
        Intent speechrecogintent;
        TextToSpeech textToSpeechhindi;
        int mhr , mmin = 0;
        private long pressedTime;

        @SuppressLint("ClickableViewAccessibility")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_alarm);
            playmenu();

            //texttospeech//
            textToSpeechhindi = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                @Override
                public void onInit(int i) {

                    // if No error is found then only it will run
                    if (i != TextToSpeech.ERROR) {
                        // To Choose language of speech
                        textToSpeechhindi.setLanguage(new Locale("hi", "IN"));
                        textToSpeechhindi.setPitch(0.8f);
                        textToSpeechhindi.setSpeechRate(1.1f);
                    }
                }
            });
            touchscreen = (GridLayout) findViewById(R.id.touchscreen);


            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");

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
                    if(matches==null)
                    {
                        if (lang.equals("hindi")) {
                            textToSpeechhindi.speak("अलार्म set karne ke liye sahi samay bole", TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            textToSpeechhindi.speak("Invalid Input", TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }
                    if (matches != null) {
                        t1.setText(matches.get(0).toLowerCase());
                        if (t1.getText().toString().contains("set alarm")) {
                            String s = t1.getText().toString();
                            String alarm_string = "";

                            for (int i = 0; i < s.length(); i++) {
                                if (Character.isDigit(s.charAt(i))) {
                                    alarm_string = alarm_string + s.charAt(i);
                                }
                            }
                            mhr = Integer.parseInt(alarm_string.substring(0, 2));
                            mmin = Integer.parseInt(alarm_string.substring(2, alarm_string.length()));
                            if(mhr>24 | mmin > 60)
                            {
                                if (lang.equals("hindi")) {
                                    textToSpeechhindi.speak("इस समय के लिए अलार्म सेट नहीं कर sakti", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    textToSpeechhindi.speak("Cannot Set Alarm for this time", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                            else{
                                setAlarm(mhr, mmin);
                            }

                        } else if (t1.getText().toString().equals("cancel alarm")) {
                            cancelAlarm();
                        } else if (t1.getText().toString().equals("back")) {
                            Intent i = new Intent(AlarmActivity.this, SwipeMenuActivity.class);
                            i.putExtra("Language", lang);
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
                            if (!mp.isPlaying()) {
                                mp.start();
                            }
                            speechrecog.stopListening();
                            t1.setText(" Press and Speak ");
                            break;
                        case MotionEvent.ACTION_DOWN:
                            if (mp.isPlaying()) {
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

        private void cancelAlarm() {

            Intent intent1 = new Intent(AlarmActivity.this, AlarmBroadcastReciever.class);
            pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, 0, intent1, 0);
            if (alarmManager == null) {
                alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            }
            alarmManager.cancel(pendingIntent);

            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");
            System.out.println(lang);
            if (lang.equals("hindi")) {
                textToSpeechhindi.speak("अलार्म सफलतापूर्वक रद्द किया गया", TextToSpeech.QUEUE_FLUSH, null);
            } else {
                textToSpeechhindi.speak("Alarm Cancelled Successfully", TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        private void setAlarm(int hour, int min) {
            Calendar calendar = Calendar.getInstance();
            long now = System.currentTimeMillis();
            calendar.setTimeInMillis( now );
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, min);
            calendar.set(Calendar.SECOND, 0);
            if( now > calendar.getTimeInMillis() ){
                calendar.add( Calendar.DAY_OF_MONTH, 1 );
            }
            AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");
            Intent intent1 = new Intent(this,  AlarmBroadcastReciever.class);
            intent1.putExtra("Language",lang);
            pendingIntent = PendingIntent.getBroadcast(this, 0, intent1, 0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            mp.pause();

            if (lang.equals("hindi")) {
                textToSpeechhindi.speak(hour + "घंटे और " + min + " मिनट के लिए अलार्म सेट", TextToSpeech.QUEUE_FLUSH, null);
            } else {
                textToSpeechhindi.speak("Alarm Set for" + hour + " Hour & " + min + " Minutes", TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        public void playmenu() {
            Intent intent = getIntent();
            String lang = intent.getExtras().getString("Language");
            if (lang.equals("hindi")) {
                if (mp == null) {
                    mp = MediaPlayer.create(this, R.raw.hindi_alarm);
                    mp.start();
                }
            } else {
                if (mp == null) {
                    mp = MediaPlayer.create(this, R.raw.englishalarm);
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
                if (lang.equals("hindi")) {
                    textToSpeechhindi.speak("बाहर निकलने के लिए फिर से दबाएं", TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    textToSpeechhindi.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
            pressedTime = System.currentTimeMillis();
        }
    }