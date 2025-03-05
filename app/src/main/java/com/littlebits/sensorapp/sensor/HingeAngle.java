package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class HingeAngle extends XFloatSensor {

    public HingeAngle(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_HINGE_ANGLE);
    }

}
