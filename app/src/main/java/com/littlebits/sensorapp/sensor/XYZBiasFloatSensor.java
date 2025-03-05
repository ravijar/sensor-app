package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.sensor.value.XYZBiasFloat;

public abstract class XYZBiasFloatSensor extends BaseSensor implements XYZBiasFloat {
    private float x,y,z,biasX,biasY,biasZ = 0;

    public XYZBiasFloatSensor(SensorManager sensorManager, int sensorType) {
        super(sensorManager, sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setX(event.values[0]);
        setY(event.values[1]);
        setZ(event.values[2]);
        setBiasX(event.values[3]);
        setBiasY(event.values[4]);
        setBiasZ(event.values[5]);
        notifyObservers();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public float getBiasX() {
        return biasX;
    }

    @Override
    public float getBiasY() {
        return biasY;
    }

    @Override
    public float getBiasZ() {
        return biasZ;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public void setBiasX(float biasX) {
        this.biasX = biasX;
    }

    @Override
    public void setBiasY(float biasY) {
        this.biasY = biasY;
    }

    @Override
    public void setBiasZ(float biasZ) {
        this.biasZ = biasZ;
    }
}
