package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

public class AccelerometerSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TextView activityText;

    public AccelerometerSensor(SensorManager sensorManager, TextView activityText) {
        this.sensorManager = sensorManager;
        this.activityText = activityText;
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void register() {
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float acceleration = (float) Math.sqrt(x*x + y*y + z*z);

        String detectedActivity = "Unknown";
        if (acceleration > 12) {
            detectedActivity = "Walking / Moving";
        } else if (acceleration < 9.8) {
            detectedActivity = "Stationary";
        }

        activityText.setText("Detected Activity: " + detectedActivity);
        Log.d("Accelerometer", detectedActivity);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
