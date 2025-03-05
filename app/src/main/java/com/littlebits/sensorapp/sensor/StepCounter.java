package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class StepCounter extends XFloatSensor {

    public StepCounter(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_STEP_COUNTER);
    }

}
