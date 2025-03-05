package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class LowLatencyOffBodyDetect extends XFloatSensor {

    public LowLatencyOffBodyDetect(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT);
    }

}
