package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class HeadTracker extends XYZABCFloatSensor {

    public HeadTracker(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_HEAD_TRACKER);
    }

}
