package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MotionDetect extends ValueFloatSensor {

    public MotionDetect(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_MOTION_DETECT);
    }

}
