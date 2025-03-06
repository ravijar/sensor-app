package com.littlebits.sensorapp.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.sensor.value.XFloat;

public abstract class XFloatSensor extends BaseSensor implements XFloat {
    private float x = 0;
    private TextView xTextView;

    public XFloatSensor(SensorManager sensorManager, int sensorType) {
        super(sensorManager, sensorType);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        setX(event.values[0]);
        notifyObservers();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public void inflateSensorView(Context context, LinearLayout sensorValueContainer) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        sensorView = layoutInflater.inflate(R.layout.item_x_float_sensor, sensorValueContainer, false);
        sensorValueContainer.addView(sensorView);

        xTextView = sensorView.findViewById(R.id.x);

        sensorViewInflated = true;
    }

    @Override
    public void updateSensorUI() {
        if (sensorViewInflated) {
            xTextView.setText(String.format("X: %s", x));
        }
    }
}
