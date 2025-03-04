package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import com.littlebits.sensorapp.helper.SensorManagerHelper;

public class LightSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor lightSensor;
    private SensorManagerHelper sensorHelper;

    public LightSensor(SensorManager sensorManager, SensorManagerHelper sensorHelper) {
        this.sensorManager = sensorManager;
        this.sensorHelper = sensorHelper;
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
        sensorHelper.updateLight(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}
}
