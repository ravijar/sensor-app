package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AccelerometerUncalibrated extends XYZABCFloatSensor {

    public AccelerometerUncalibrated(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ACCELEROMETER_UNCALIBRATED);
    }

}
