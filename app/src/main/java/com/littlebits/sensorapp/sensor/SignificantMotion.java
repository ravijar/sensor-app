package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SignificantMotion extends ValueFloatSensor {

    public SignificantMotion(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_SIGNIFICANT_MOTION);
    }

}
