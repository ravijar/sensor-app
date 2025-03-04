package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.littlebits.sensorapp.helper.SensorManagerHelper;

public class AccelerometerSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private SensorManagerHelper sensorHelper;

    public AccelerometerSensor(SensorManager sensorManager, SensorManagerHelper sensorHelper) {
        this.sensorManager = sensorManager;
        this.sensorHelper = sensorHelper;
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
        sensorHelper.updateAccelerometer(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
