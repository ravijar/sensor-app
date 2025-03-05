package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Accelerometer extends XYZFloatSensor {

    public Accelerometer(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ACCELEROMETER);
    }

}
