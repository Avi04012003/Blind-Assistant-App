package com.example.ech;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SOS extends AppCompatActivity implements SensorEventListener {
    private static final String DATABASE_NAME = "NumbersDB";
    private static final String TABLE_NAME = "Numbers";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NUMBER = "number";

    private EditText editTextNumber;
    private SQLiteDatabase database;
    private static final int SMS_PERMISSION_CODE = 101;
    private int shakeCount = 0;
    private long lastShakeTime = 0;

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private boolean isSendingSMS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        editTextNumber = findViewById(R.id.editTextNumber);
        checkSMSPermission();
        database = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
        createTable();

        // Initialize sensor manager and accelerometer sensor
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void createTable() {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NUMBER + " TEXT)";
        database.execSQL(createTableQuery);
    }

    public void saveNumber(View view) {
        String number = editTextNumber.getText().toString().trim();
        if (!number.isEmpty()) {
            // Code to save the number in the database
            ContentValues values = new ContentValues();
            values.put(COLUMN_NUMBER, number);
            long result = database.insert(TABLE_NAME, null, values);
            if (result != -1) {
                Toast.makeText(this, "Emergency number save: " + number, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save number", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkSMSPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    SMS_PERMISSION_CODE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        long currentTime = System.currentTimeMillis();
        if ((currentTime - lastShakeTime) > 1000) {
            shakeCount = 0;
        }
        lastShakeTime = currentTime;

        shakeCount++;

        if (shakeCount == 2 && !isSendingSMS) {
            sendEmergencySMS();
        }
    }

    @SuppressLint("Range")
    private void sendEmergencySMS() {
        // Retrieve emergency number from the database
        String emergencyNumber = "";
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            emergencyNumber = cursor.getString(cursor.getColumnIndex(COLUMN_NUMBER));
        }
        cursor.close();

        // Replace with your emergency message
        String emergencyMessage = "Emergency! I am avinash harne I need your help.";

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(emergencyNumber, null, emergencyMessage, null, null);
                Toast.makeText(this, "Emergency SMS sent", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                // Log any exceptions that occur during sending
                Log.e("SendSMS", "Error sending SMS: " + e.getMessage());
                Toast.makeText(this, "Error sending SMS", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "SMS permission not granted", Toast.LENGTH_SHORT).show();
        }

        // Set isSendingSMS to true and reset after a delay
        isSendingSMS = true;
        new android.os.Handler().postDelayed(
                () -> isSendingSMS = false,
                5000 // Delay in milliseconds before enabling shake detection again
        );
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the database connection when the activity is destroyed
        if (database != null) {
            database.close();
        }
        // Unregister sensor listener
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Not used in this example
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used in this example
    }
}