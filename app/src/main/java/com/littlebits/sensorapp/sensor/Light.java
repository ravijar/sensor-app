package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class Light extends XFloatSensor {

    public Light(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_LIGHT);
    }

}
