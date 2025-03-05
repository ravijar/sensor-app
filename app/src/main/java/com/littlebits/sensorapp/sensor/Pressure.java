package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Pressure extends XFloatSensor {

    public Pressure(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_PRESSURE);
    }

}
