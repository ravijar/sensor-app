package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.sensor.value.ValueFloat;

public class Light extends BaseSensor implements ValueFloat {
    private float value = 0;

    public Light(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_LIGHT);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setValue(event.values[0]);
        notifyObservers();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public float getValue() {
        return value;
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }
}
