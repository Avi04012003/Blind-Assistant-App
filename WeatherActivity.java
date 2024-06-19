package com.example.ech.weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ech.MoreMenuActivity;
import com.example.ech.R;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class WeatherActivity extends AppCompatActivity {
    CardView card1;
    TextView t1;
    MediaPlayer mp;
    SpeechRecognizer speechrecog;
    Intent speechrecogintent;
    private long pressedTime;

    //weather //
    private GpsTracker gpsTracker;
    TextToSpeech textToSpeechhindi;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        playmenu();
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");

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

        playmenu();

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
                    t1.setText(matches.get(0).toLowerCase());
                    t1.setText(matches.get(0));
                    if (t1.getText().toString().equals("play again") ){
                        playmenu();
                    }
                    else if(t1.getText().toString().equals("what is today's weather"))
                    {
                        getWeather();
                    }
                    else if(t1.getText().toString().equals("back"))
                    {
                        Intent i = new Intent(WeatherActivity.this, MoreMenuActivity.class);
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
    public void playmenu()
    {Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindiweather);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishweather);
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


    //weather methods//
    public String getLocation(){
        gpsTracker = new GpsTracker(WeatherActivity.this);
        if(gpsTracker.canGetLocation()){
            double latitude = gpsTracker.getLatitude();
            double longitude = gpsTracker.getLongitude();
            String api="e2c501e029b654248cc4d78458dd3bd8";
            String url = "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=" +api+"&units=metric";
            return url;

        }else{
            gpsTracker.showSettingsAlert();
        }
        return null;
    }
    public void getWeather()
    {
        mp.pause();
        Intent intent = getIntent();
        String lang = intent.getExtras().getString("Language");
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url= getLocation();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONObject array= (JSONObject) response.get("main");
                    double tempvalue= array.getDouble("temp");
                    double Temp = Math.round(tempvalue);
                    String hum = String.valueOf(array.getDouble("humidity"));
                    String City= response.getString("name");

                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd-MMMM-YYYY");
                    String date = sdf.format(c.getTime());

                    String EngweatherText="Weather in "+City+" is as follows , Temperature is "+Temp+" Degree Celsius and humidity is "+hum+" Today is "+date ;
                    String HinweatherText=City+" में मौसम इस प्रकार है, तापमान "+Temp+" डिग्री सेल्सियस और नमी है "+hum+" , आज है "+date+" ";
                    if(lang.equals("hindi")) {
                        textToSpeechhindi.speak(HinweatherText, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else
                    {
                        textToSpeechhindi.speak(EngweatherText, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    Toast.makeText(getApplicationContext(),EngweatherText,Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    t1.setText(e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
                t1.setText(error.toString());
            }
        });
        queue.add(request);
    }
}
//weather methods//