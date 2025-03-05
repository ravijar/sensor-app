package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Temperature extends ValueFloatSensor {

    public Temperature(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_TEMPERATURE);
    }

}
