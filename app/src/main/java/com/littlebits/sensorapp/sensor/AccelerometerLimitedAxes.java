package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class AccelerometerLimitedAxes extends XYZFloatSensor {

    public AccelerometerLimitedAxes(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_ACCELEROMETER_LIMITED_AXES);
    }

}
