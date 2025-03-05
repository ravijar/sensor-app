package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AccelerometerLimitedAxesUncalibrated extends XYZBiasFloatSensor {

    public AccelerometerLimitedAxesUncalibrated(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED);
    }

}
