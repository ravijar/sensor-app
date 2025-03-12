package com.littlebits.sensorapp.activity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.SensorObserver;

public class SensorDetailsActivity extends AppCompatActivity implements SensorObserver {
    private BaseSensor sensorInstance;
    private TextView sensorTitle;
    private LinearLayout sensorValueContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        // Retrieve sensor instance from SensorRepository
        sensorInstance = SensorRepository.getInstance().getCurrentSensor();

        if (sensorInstance == null) {
            finish(); // Close activity if sensor is unavailable
            return;
        }

        // Initialize UI components
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sensor Readings");
        }
        sensorTitle = findViewById(R.id.sensorTitle);
        sensorValueContainer = findViewById(R.id.sensorValueContainer);

        // Set sensor name as the title
        sensorTitle.setText(sensorInstance.getName());

        // Inflate and add the sensor-specific UI dynamically
        sensorInstance.inflateSensorView(this, sensorValueContainer);
        sensorInstance.addObserver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sensorInstance != null) {
            sensorInstance.register();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sensorInstance != null) {
            sensorInstance.unregister();
        }
    }

    @Override
    public void onSensorChanged(int sensorType) {
        sensorInstance.updateSensorUI();
    }
}
