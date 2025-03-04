package com.littlebits.sensorapp;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.littlebits.sensorapp.helper.SensorManagerHelper;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorManagerHelper sensorHelper;
    private TextView activityText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityText = findViewById(R.id.activityText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initialize sensor manager helper
        sensorHelper = new SensorManagerHelper(sensorManager, activityText, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHelper.registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHelper.unregisterSensors();
    }
}
