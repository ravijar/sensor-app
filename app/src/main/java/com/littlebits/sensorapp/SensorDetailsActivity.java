package com.littlebits.sensorapp;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.helper.SensorUIHelper;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int sensorType;
    private TextView sensorTitle;
    private LinearLayout sensorValueContainer;
    private SensorUIHelper sensorUIHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        // Get sensor type from intent
        sensorType = getIntent().getIntExtra("sensorType", -1);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);

        if (sensor == null) {
            finish(); // Close activity if sensor is unavailable
            return;
        }

        // Initialize UI components
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sensor Readings");
        }
        sensorTitle = findViewById(R.id.sensorTitle);
        sensorValueContainer = findViewById(R.id.sensorValueContainer);
        sensorUIHelper = new SensorUIHelper();
        sensorTitle.setText(sensor.getName());

        // Inflate and add the sensor-specific UI
        View sensorView = sensorUIHelper.inflateSensorView(this, sensorType, sensorValueContainer);
        if (sensorView != null) {
            sensorValueContainer.addView(sensorView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        sensorUIHelper.updateSensorValues(sensorType, event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }
}
