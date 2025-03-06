package com.littlebits.sensorapp.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private List<SensorObserver> observers = new ArrayList<>();
    protected View sensorView;
    protected boolean sensorViewInflated = false;

    public BaseSensor(SensorManager sensorManager, int sensorType) {
        this.sensorManager = sensorManager;
        sensor = sensorManager.getDefaultSensor(sensorType);
    }

    public void register() {
        if (sensor != null) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
        }
    }

    public void unregister() {
        sensorManager.unregisterListener(this);
    }

    public String getStringType() {
        String stringType = sensor.getStringType();

        String[] parts = stringType.split("\\.");
        String cleanType = parts[parts.length - 1];

        return cleanType.replace("_", " ").replace("-", " ").trim();
    }

    public void addObserver(SensorObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(SensorObserver observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (SensorObserver observer : observers) {
            observer.onSensorChanged();
        }
    }

    @Override
    public abstract void onSensorChanged(SensorEvent event);

    @Override
    public abstract void onAccuracyChanged(Sensor sensor, int accuracy);

    public abstract void inflateSensorView(Context context, LinearLayout sensorValueContainer);

    public abstract void updateSensorUI();
}
