package com.littlebits.sensorapp.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.sensor.value.XYFloat;

public abstract class XYFloatSensor extends BaseSensor implements XYFloat {
    private float x,y = 0;
    private TextView xTextView,yTextView;

    public XYFloatSensor(SensorManager sensorManager, int sensorType) {
        super(sensorManager, sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setX(event.values[0]);
        setY(event.values[1]);
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
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public void inflateSensorView(Context context, LinearLayout sensorValueContainer) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        sensorView = layoutInflater.inflate(R.layout.item_xy_float_sensor, sensorValueContainer, false);
        sensorValueContainer.addView(sensorView);

        xTextView = sensorView.findViewById(R.id.x);
        yTextView = sensorView.findViewById(R.id.y);

        sensorViewInflated = true;
    }

    @Override
    protected void updateSensorUI() {
        xTextView.setText(String.format("X: %s", x));
        yTextView.setText(String.format("Y: %s", y));
    }
}
