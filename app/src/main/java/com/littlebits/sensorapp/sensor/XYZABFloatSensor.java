package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.sensor.value.XYZABFloat;

public abstract class XYZABFloatSensor extends BaseSensor implements XYZABFloat {
    private float x,y,z,a,b = 0;

    public XYZABFloatSensor(SensorManager sensorManager, int sensorType) {
        super(sensorManager, sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setX(event.values[0]);
        setY(event.values[1]);
        setZ(event.values[2]);
        setA(event.values[3]);
        setB(event.values[4]);
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
    public float getA() {
        return a;
    }

    @Override
    public float getB() {
        return b;
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
    public void setA(float a) {
        this.a = a;
    }

    @Override
    public void setB(float b) {
        this.b = b;
    }
}
