package com.littlebits.sensorapp.helper;

import android.content.Context;
import android.hardware.SensorManager;
import android.widget.TextView;
import com.littlebits.sensorapp.sensor.AccelerometerSensor;
import com.littlebits.sensorapp.sensor.GyroscopeSensor;
import com.littlebits.sensorapp.sensor.LightSensor;
import java.util.Map;

public class SensorManagerHelper {
    private AccelerometerSensor accelerometerSensor;
    private GyroscopeSensor gyroscopeSensor;
    private LightSensor lightSensor;

    private float accelX = 0, accelY = 0, accelZ = 0;
    private float gyroX = 0, gyroY = 0, gyroZ = 0;
    private float lightValue = 0;

    private Map<String, TextView> sensorTextViews;

    public SensorManagerHelper(SensorManager sensorManager, Map<String, TextView> sensorTextViews, Context context) {
        this.sensorTextViews = sensorTextViews;

        accelerometerSensor = new AccelerometerSensor(sensorManager, this);
        gyroscopeSensor = new GyroscopeSensor(sensorManager, this);
        lightSensor = new LightSensor(sensorManager, this);
    }

    public void registerSensors() {
        accelerometerSensor.register();
        gyroscopeSensor.register();
        lightSensor.register();
    }

    public void unregisterSensors() {
        accelerometerSensor.unregister();
        gyroscopeSensor.unregister();
        lightSensor.unregister();
    }

    // Receive raw values from sensors and update UI dynamically
    public void updateAccelerometer(float x, float y, float z) {
        accelX = x;
        accelY = y;
        accelZ = z;
        updateText("accelX", "Accel X: " + accelX);
        updateText("accelY", "Accel Y: " + accelY);
        updateText("accelZ", "Accel Z: " + accelZ);
        predictActivity();
    }

    public void updateGyroscope(float x, float y, float z) {
        gyroX = x;
        gyroY = y;
        gyroZ = z;
        updateText("gyroX", "Gyro X: " + gyroX);
        updateText("gyroY", "Gyro Y: " + gyroY);
        updateText("gyroZ", "Gyro Z: " + gyroZ);
        predictActivity();
    }

    public void updateLight(float value) {
        lightValue = value;
        updateText("light", "Light: " + lightValue);
        predictActivity();
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
}
