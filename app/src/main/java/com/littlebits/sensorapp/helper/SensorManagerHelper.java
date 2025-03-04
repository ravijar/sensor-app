package com.littlebits.sensorapp.helper;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.TextView;
import com.littlebits.sensorapp.sensor.AccelerometerSensor;
import com.littlebits.sensorapp.sensor.GyroscopeSensor;
import com.littlebits.sensorapp.sensor.LightSensor;

public class SensorManagerHelper {
    private AccelerometerSensor accelerometerSensor;
    private GyroscopeSensor gyroscopeSensor;
    private LightSensor lightSensor;

    public SensorManagerHelper(SensorManager sensorManager, TextView activityText, Context context) {
        accelerometerSensor = new AccelerometerSensor(sensorManager, activityText);
        gyroscopeSensor = new GyroscopeSensor(sensorManager, activityText);
        lightSensor = new LightSensor(sensorManager, activityText);
    }

    public void registerSensors() {
        accelerometerSensor.register();
        gyroscopeSensor.register();
        lightSensor.register();
    }

    public void unregisterSensors() {
        accelerometerSensor.unregister();
        gyroscopeSensor.unregister();
        lightSensor.unregister();
    }
}
