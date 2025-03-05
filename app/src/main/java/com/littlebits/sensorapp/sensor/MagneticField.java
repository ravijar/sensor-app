package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MagneticField extends XYZFloatSensor {

    public MagneticField(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_MAGNETIC_FIELD);
    }

}
