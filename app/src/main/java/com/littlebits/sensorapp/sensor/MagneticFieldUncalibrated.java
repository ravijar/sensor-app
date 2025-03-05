package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MagneticFieldUncalibrated extends XYZABCFloatSensor {

    public MagneticFieldUncalibrated(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
    }

}
