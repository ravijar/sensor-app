package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class LinearAcceleration extends XYZFloatSensor {

    public LinearAcceleration(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_LINEAR_ACCELERATION);
    }

}
