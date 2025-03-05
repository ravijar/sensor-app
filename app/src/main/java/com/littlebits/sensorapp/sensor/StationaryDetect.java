package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class StationaryDetect extends XFloatSensor {

    public StationaryDetect(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_STATIONARY_DETECT);
    }

}
