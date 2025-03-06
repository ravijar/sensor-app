package com.littlebits.sensorapp;

import android.app.Application;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.repository.SensorRepository;

public class SensorApp extends Application {
    private static SensorApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager != null) {
            SensorRepository.getInstance().initializeSensors(sensorManager);
            System.out.println("Sensors initialized in Application class");
        }
    }

    public static SensorApp getInstance() {
        return instance;
    }
}
