package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AmbientTemperature extends ValueFloatSensor {

    public AmbientTemperature(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE);
    }

}
