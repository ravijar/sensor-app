package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Gravity extends XYZFloatSensor {

    public Gravity(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GRAVITY);
    }

}