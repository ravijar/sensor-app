package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class GyroscopeLimitedAxesUncalibrated extends XYZBiasFloatSensor {

    public GyroscopeLimitedAxesUncalibrated(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED);
    }

}
