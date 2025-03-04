package com.littlebits.sensorapp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SensorDetailsActivity extends Activity implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private TextView valueX, valueY, valueZ, lightValue;
    private int sensorType;
    private Button backButton;
    private LinearLayout sensorValueContainer;

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

        sensorValueContainer = findViewById(R.id.sensorValueContainer);
        backButton = findViewById(R.id.backButton);

        // Inflate sensor-specific layout dynamically
        LayoutInflater inflater = LayoutInflater.from(this);
        View sensorView = null;

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            sensorView = inflater.inflate(R.layout.item_accelerometer, sensorValueContainer, false);
            valueX = sensorView.findViewById(R.id.accelX);
            valueY = sensorView.findViewById(R.id.accelY);
            valueZ = sensorView.findViewById(R.id.accelZ);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            sensorView = inflater.inflate(R.layout.item_gyroscope, sensorValueContainer, false);
            valueX = sensorView.findViewById(R.id.gyroX);
            valueY = sensorView.findViewById(R.id.gyroY);
            valueZ = sensorView.findViewById(R.id.gyroZ);
        } else if (sensorType == Sensor.TYPE_LIGHT) {
            sensorView = inflater.inflate(R.layout.item_light_sensor, sensorValueContainer, false);
            lightValue = sensorView.findViewById(R.id.lightValue);
        }

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
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            valueX.setText("X: " + event.values[0]);
            valueY.setText("Y: " + event.values[1]);
            valueZ.setText("Z: " + event.values[2]);
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            valueX.setText("X: " + event.values[0]);
            valueY.setText("Y: " + event.values[1]);
            valueZ.setText("Z: " + event.values[2]);
        } else if (sensorType == Sensor.TYPE_LIGHT) {
            lightValue.setText("Light: " + event.values[0]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }
}
