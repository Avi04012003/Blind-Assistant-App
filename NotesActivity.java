package com.example.ech.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.ech.R;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;
import com.example.ech.OnSwipeTouchListener;
import java.util.ArrayList;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity implements NotesAdapter.onNoteListener {

    GridLayout touchscreen;
    SwipeRefreshLayout refreshLayout;
    MediaPlayer mp;
    DatabaseHelper myDb;
    RecyclerView notesview;
    private long pressedTime;
    TextView message;
    CardView notecard;
    private ArrayList<Notes> notesArrayList;
    private NotesAdapter notesAdapter;
    TextToSpeech textToSpeechhindi,textToSpeechEng;
    Intent intent;
    String lang = "";
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        myDb= new DatabaseHelper(getApplicationContext());
        notesview=(RecyclerView)findViewById(R.id.noteview);
        touchscreen=(GridLayout)findViewById(R.id.touchscreen);
        refreshLayout=(SwipeRefreshLayout) findViewById(R.id.refresh);
        intent = getIntent();
        lang = intent.getExtras().getString("Language");
        //to start the audio at the begining
        playmenu();

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

        textToSpeechEng = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

                // if No error is found then only it will run
                if(i!=TextToSpeech.ERROR){
                    // To Choose language of speech
                    textToSpeechEng.setLanguage(Locale.ENGLISH);
                    textToSpeechEng.setPitch(0.9f);
                    textToSpeechEng.setSpeechRate(0.8f);
                }
            }
        });

        touchscreen.setOnTouchListener(new OnSwipeTouchListener(NotesActivity.this) {
            public void onSwipeRight() {
                mp.pause();
                Intent i = new Intent(NotesActivity.this, NewNoteActivity.class);
                i.putExtra("Language",lang);
                startActivity(i);
                finish();

            }//Swipe Right to Add Notes
        });
        buildRecyclerView();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Intent i = new Intent(NotesActivity.this, NotesActivity.class);
                i.putExtra("Language",lang);
                startActivity(i);
                finish();
            }
        });
    }
    public void buildRecyclerView() {
        Intent intent = getIntent();
        lang = intent.getExtras().getString("Language");
        notesArrayList = new ArrayList<>();
        Cursor data = myDb.getAllNotes();
        if(data.getCount()==0)
        {
            if(lang.equals("hindi")){
                textToSpeechhindi.speak("आपके पास नोट nahi hain, नया नोट जोड़ने के लिए दाएं स्वाइप करें ", TextToSpeech.QUEUE_FLUSH, null);

            }
            else
            {
                textToSpeechEng.speak("You Have No New Notes, Swipe Right to Add a New Note.", TextToSpeech.QUEUE_FLUSH, null);

            }
        }
        while (data.moveToNext()) {
            notesArrayList.add(new Notes(data.getString(1),data.getInt(0)));
        }
        notesAdapter= new NotesAdapter(NotesActivity.this, notesArrayList, this);
        notesview.setHasFixedSize(true);
        notesview.setLayoutManager(new LinearLayoutManager(NotesActivity.this));
        notesview.setAdapter(notesAdapter);

    }
    public void playmenu()
    {    lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.hindinote);
                mp.start();
            }
        }
        else
        {
            if (mp == null) {
                mp = MediaPlayer.create(this,R.raw.englishnote);
                mp.start();
            }
        }
    }
    @Override
    public void onNoteLongClick(int position, View v) {
        TextView msgid = notesview.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.mid);
        String uniqueid=msgid.getText().toString();
        int mid=Integer.parseInt(uniqueid);

        lang = intent.getExtras().getString("Language");
            int res= myDb.deleteData(mid);
            if(res>0)
            {
                if(lang.equals("hindi")){
                    textToSpeechhindi.speak("नोट सफलतापूर्वक हटा दिया गया।", TextToSpeech.QUEUE_FLUSH, null);
                }//Todo Change this text to Hindi
                else
                {
                    textToSpeechEng.speak("Note Deleted SuccessFully.", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
    }

    @Override
    public void OnNoteClick(int position) {
        TextView text1 = notesview.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.message);
        String notespeech= text1.getText().toString();
        lang = intent.getExtras().getString("Language");
        if(lang.equals("hindi")){
            textToSpeechhindi.speak(notespeech, TextToSpeech.QUEUE_FLUSH, null);
        }
        else{
            textToSpeechEng.speak(notespeech,TextToSpeech.QUEUE_FLUSH,null);
        }
    }//Notes Speech code

    @Override
    protected void onUserLeaveHint()
    {
        mp.stop();
        super.onUserLeaveHint();
    }
    public void onBackPressed() {
        lang = intent.getExtras().getString("Language");
        if (pressedTime + 2000 > System.currentTimeMillis()) {
            mp.stop();
            super.onBackPressed();
            finish();
        } else {
            mp.stop();
            if(lang.equals("hindi")) {
                textToSpeechhindi.speak("बाहर निकलने के लिए फिर से दबाएं", TextToSpeech.QUEUE_FLUSH, null);
            }
            else
            {
                textToSpeechEng.speak("Press back again to exit", TextToSpeech.QUEUE_FLUSH, null);
            }}
        pressedTime = System.currentTimeMillis();
    }
}