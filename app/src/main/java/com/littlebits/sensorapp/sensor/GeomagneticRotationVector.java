package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class GeomagneticRotationVector extends XYZABFloatSensor {

    public GeomagneticRotationVector(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
    }

}
