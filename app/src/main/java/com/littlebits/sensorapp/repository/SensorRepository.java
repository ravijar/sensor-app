package com.littlebits.sensorapp.repository;

import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorFactory;

import java.util.HashMap;
import java.util.Map;

public class SensorRepository {
    private static SensorRepository instance;
    private final Map<Integer, BaseSensor> sensorMap = new HashMap<>();
    private BaseSensor currentSensor;

    private SensorRepository() {}

    public static SensorRepository getInstance() {
        if (instance == null) {
            instance = new SensorRepository();
        }
        return instance;
    }

    public void initializeSensors(SensorManager sensorManager) {
        if (!sensorMap.isEmpty()) return; // Prevent re-initialization

        for (Sensor sensor : sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            BaseSensor baseSensor = SensorFactory.createSensor(sensorManager, sensor.getType());
            if (baseSensor != null) {
                sensorMap.put(sensor.getType(), baseSensor);
            }
        }
    }

    public BaseSensor getSensor(int sensorType) {
        return sensorMap.get(sensorType);
    }

    public Map<Integer, BaseSensor> getAllSensors() {
        return sensorMap;
    }

    public void setCurrentSensor(BaseSensor sensor) {
        this.currentSensor = sensor;
    }

    public BaseSensor getCurrentSensor() {
        return currentSensor;
    }
}

