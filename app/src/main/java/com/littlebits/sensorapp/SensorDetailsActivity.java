package com.littlebits.sensorapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.littlebits.sensorapp.helper.SensorUIHelper;

public class SensorDetailsActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private int sensorType;
    private Button backButton;
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
        sensorValueContainer = findViewById(R.id.sensorValueContainer);
        backButton = findViewById(R.id.backButton);
        sensorUIHelper = new SensorUIHelper();

        // Inflate and add the sensor-specific UI
        View sensorView = sensorUIHelper.inflateSensorView(this, sensorType, sensorValueContainer);
        if (sensorView != null) {
            sensorValueContainer.addView(sensorView);
        }

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
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
