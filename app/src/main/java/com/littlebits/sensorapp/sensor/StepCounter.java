package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class StepCounter extends ValueFloatSensor {

    public StepCounter(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_STEP_COUNTER);
    }

}
