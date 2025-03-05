package com.littlebits.sensorapp.helper;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.TextView;
import com.littlebits.sensorapp.sensor.Accelerometer;
import com.littlebits.sensorapp.sensor.Gyroscope;
import com.littlebits.sensorapp.sensor.Light;
import com.littlebits.sensorapp.sensor.SensorObserver;

import java.util.Map;

public class SensorManagerHelper implements SensorObserver {
    private Accelerometer accelerometer;
    private Gyroscope gyroscope;
    private Light light;

    private float accelX = 0, accelY = 0, accelZ = 0;
    private float gyroX = 0, gyroY = 0, gyroZ = 0;
    private float lightValue = 0;

    private Map<String, TextView> sensorTextViews;

    public SensorManagerHelper(SensorManager sensorManager, Map<String, TextView> sensorTextViews, Context context) {
        this.sensorTextViews = sensorTextViews;

        accelerometer = new Accelerometer(sensorManager);
        gyroscope = new Gyroscope(sensorManager);
        light = new Light(sensorManager);

        accelerometer.addObserver(this);
        gyroscope.addObserver(this);
        light.addObserver(this);
    }

    public void registerSensors() {
        accelerometer.register();
        gyroscope.register();
        light.register();
    }

    public void unregisterSensors() {
        accelerometer.unregister();
        gyroscope.unregister();
        light.unregister();
    }

    // Process sensor values to predict activity
    private void predictActivity() {
        String activity = "Unknown";

        // Check if the phone is stationary (very low acceleration and gyroscope values)
        if (Math.abs(accelX) < 0.3 && Math.abs(accelY) < 0.3 && Math.abs(accelZ - 9.8) < 0.3 &&
                Math.abs(gyroX) < 0.1 && Math.abs(gyroY) < 0.1 && Math.abs(gyroZ) < 0.1) {
            activity = "Stationary";
        }
        // Check if the phone is rotating (significant gyroscope movement)
        else if (Math.abs(gyroX) > 1.0 || Math.abs(gyroY) > 1.0 || Math.abs(gyroZ) > 1.0) {
            activity = "Rotating";
        }
        // Otherwise, classify as moving (noticeable acceleration changes)
        else {
            activity = "Moving";
        }

        // Update the UI with the detected activity
        updateText("activity", activity);
    }



    // Utility method to update UI text dynamically
    private void updateText(String key, String text) {
        if (sensorTextViews.containsKey(key)) {
            sensorTextViews.get(key).setText(text);
        }
    }

    @Override
    public void onSensorChanged() {
        accelX = accelerometer.getX();
        accelY = accelerometer.getY();
        accelZ = accelerometer.getZ();

        gyroX = gyroscope.getX();
        gyroY = gyroscope.getY();
        gyroZ = gyroscope.getZ();

        lightValue = light.getValue();

        updateText("accelX", "Accel X: " + accelX);
        updateText("accelY", "Accel Y: " + accelY);
        updateText("accelZ", "Accel Z: " + accelZ);

        updateText("gyroX", "Gyro X: " + gyroX);
        updateText("gyroY", "Gyro Y: " + gyroY);
        updateText("gyroZ", "Gyro Z: " + gyroZ);

        updateText("light", "Light: " + lightValue);

        predictActivity();
    }
}
