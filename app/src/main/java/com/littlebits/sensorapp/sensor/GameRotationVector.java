package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class GameRotationVector extends XYZFloatSensor {

    public GameRotationVector(SensorManager sensorManager) {
        super(sensorManager, Sensor.TYPE_GAME_ROTATION_VECTOR);
    }

}
