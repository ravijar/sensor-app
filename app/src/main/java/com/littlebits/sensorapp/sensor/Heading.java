package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Heading extends XYFloatSensor {

    public Heading(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_HEADING);
    }

}
