package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Proximity extends ValueFloatSensor {

    public Proximity(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_PROXIMITY);
    }

}
