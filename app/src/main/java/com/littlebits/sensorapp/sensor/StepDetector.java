package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class StepDetector extends XFloatSensor {

    public StepDetector(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_STEP_DETECTOR);
    }

}
