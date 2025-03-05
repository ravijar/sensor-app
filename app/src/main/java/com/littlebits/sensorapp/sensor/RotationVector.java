package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class RotationVector extends XYZABFloatSensor {

    public RotationVector(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ROTATION_VECTOR);
    }

}
