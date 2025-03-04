package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

public class GyroscopeSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private TextView activityText;

    public GyroscopeSensor(SensorManager sensorManager, TextView activityText) {
        this.sensorManager = sensorManager;
        this.activityText = activityText;
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void register() {
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (Math.abs(event.values[0]) > 2 || Math.abs(event.values[1]) > 2 || Math.abs(event.values[2]) > 2) {
            activityText.setText("Phone Held in Hand");
            Log.d("Gyroscope", "Phone Held in Hand");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
