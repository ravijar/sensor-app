package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

public class LightSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private TextView activityText;

    public LightSensor(SensorManager sensorManager, TextView activityText) {
        this.sensorManager = sensorManager;
        this.activityText = activityText;
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
    }

    public void register() {
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.values[0] > 10000) {
            activityText.setText("Out in the Sun");
            Log.d("LightSensor", "Out in the Sun");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
