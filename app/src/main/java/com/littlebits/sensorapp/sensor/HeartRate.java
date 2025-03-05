package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class HeartRate extends XFloatSensor {

    public HeartRate(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_HEART_RATE);
    }

}
