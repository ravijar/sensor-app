package com.littlebits.sensorapp.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.sensor.value.XYZABCFloat;

public abstract class XYZABCFloatSensor extends BaseSensor implements XYZABCFloat {
    private float x,y,z,a,b,c = 0;
    private TextView xTextView,yTextView,zTextView,aTextView,bTextView,cTextView;

    public XYZABCFloatSensor(SensorManager sensorManager, int sensorType) {
        super(sensorManager, sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setX(event.values[0]);
        setY(event.values[1]);
        setZ(event.values[2]);
        setA(event.values[3]);
        setB(event.values[4]);
        setC(event.values[5]);
        if (sensorViewInflated) updateSensorUI();
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
    public float getC() {
        return c;
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

    @Override
    public void setC(float c) {
        this.c = c;
    }

    @Override
    public void inflateSensorView(Context context, LinearLayout sensorValueContainer) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        sensorView = layoutInflater.inflate(R.layout.item_xyzabc_float_sensor, sensorValueContainer, false);
        sensorValueContainer.addView(sensorView);

        xTextView = sensorView.findViewById(R.id.x);
        yTextView = sensorView.findViewById(R.id.y);
        zTextView = sensorView.findViewById(R.id.z);
        aTextView = sensorView.findViewById(R.id.a);
        bTextView = sensorView.findViewById(R.id.b);
        cTextView = sensorView.findViewById(R.id.c);

        sensorViewInflated = true;
    }

    @Override
    protected void updateSensorUI() {
        xTextView.setText(String.format("X: %s", x));
        yTextView.setText(String.format("Y: %s", y));
        zTextView.setText(String.format("Z: %s", z));
        aTextView.setText(String.format("A: %s", a));
        bTextView.setText(String.format("B: %s", b));
        cTextView.setText(String.format("C: %s", c));
    }
}
