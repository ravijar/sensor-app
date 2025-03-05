package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class GyroscopeUncalibrated extends XYZABCFloatSensor {

    public GyroscopeUncalibrated(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GYROSCOPE_UNCALIBRATED);
    }

}
