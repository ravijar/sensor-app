package com.littlebits.sensorapp.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlebits.sensorapp.R;

public class SensorUIHelper {
    private TextView valueX, valueY, valueZ, lightValue;

    public View inflateSensorView(Context context, int sensorType, LinearLayout sensorValueContainer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View sensorView = null;

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorView = inflater.inflate(R.layout.item_accelerometer, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.accelX);
                valueY = sensorView.findViewById(R.id.accelY);
                valueZ = sensorView.findViewById(R.id.accelZ);
                break;

            case Sensor.TYPE_GYROSCOPE:
                sensorView = inflater.inflate(R.layout.item_gyroscope, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.gyroX);
                valueY = sensorView.findViewById(R.id.gyroY);
                valueZ = sensorView.findViewById(R.id.gyroZ);
                break;

            case Sensor.TYPE_LIGHT:
                sensorView = inflater.inflate(R.layout.item_light_sensor, sensorValueContainer, false);
                lightValue = sensorView.findViewById(R.id.lightValue);
                break;

            default:
                break;
        }

        return sensorView;
    }

    public void updateSensorValues(int sensorType, float[] values) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GYROSCOPE:
                if (valueX != null) valueX.setText("X: " + values[0]);
                if (valueY != null) valueY.setText("Y: " + values[1]);
                if (valueZ != null) valueZ.setText("Z: " + values[2]);
                break;

            case Sensor.TYPE_LIGHT:
                if (lightValue != null) lightValue.setText("Light: " + values[0]);
                break;

            default:
                break;
        }
    }
}
