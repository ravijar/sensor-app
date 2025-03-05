package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class RelativeHumidity extends ValueFloatSensor {

    public RelativeHumidity(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_RELATIVE_HUMIDITY);
    }

}
