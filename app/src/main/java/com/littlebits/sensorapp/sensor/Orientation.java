package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Orientation extends XYZFloatSensor {

    public Orientation(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ORIENTATION);
    }

}