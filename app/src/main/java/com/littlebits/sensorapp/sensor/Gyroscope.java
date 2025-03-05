package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Gyroscope extends XYZFloatSensor {

    public Gyroscope(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GYROSCOPE);
    }

}