package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.littlebits.sensorapp.helper.SensorManagerHelper;

public class GyroscopeSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor gyroscope;
    private SensorManagerHelper sensorHelper;

    public GyroscopeSensor(SensorManager sensorManager, SensorManagerHelper sensorHelper) {
        this.sensorManager = sensorManager;
        this.sensorHelper = sensorHelper;
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
        sensorHelper.updateGyroscope(event.values[0], event.values[1], event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
