package com.littlebits.sensorapp.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XFloatSensor;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;

public class StepsCounter implements SensorObserver {

    public interface StepListener {
        void onStepDetected(int totalSteps);
    }

    private final SensorRepository repository;
    private StepListener listener;

    private BaseSensor stepSensor;
    private BaseSensor accelSensor;
    private BaseSensor gyroSensor;

    private int initialStepCount = -1;
    private int fallbackStepCount = 0;

    private float lastAccelZ = 0;
    private float lastGyroZ = 0;
    private boolean accelRising = false;
    private long lastStepTime = 0;

    private boolean isPaused = false;

    private static final float ACCEL_THRESHOLD = 1.8f; // m/s^2
    private static final float GYRO_THRESHOLD = 1.2f; // rad/s
    private static final int STEP_INTERVAL_MS = 350; // Minimum delay between steps

    public StepsCounter(Context context) {
        this.repository = SensorRepository.getInstance();
        repository.initializeSensors((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));

        stepSensor = repository.getSensor(Sensor.TYPE_STEP_COUNTER);
        accelSensor = repository.getSensor(Sensor.TYPE_ACCELEROMETER);
        gyroSensor = repository.getSensor(Sensor.TYPE_GYROSCOPE);
    }

    public void start(StepListener listener) {
        this.listener = listener;

        if (stepSensor != null) {
            stepSensor.addObserver(this);
            stepSensor.register();
        } else {
            if (accelSensor != null) {
                accelSensor.addObserver(this);
                accelSensor.register();
            }
            if (gyroSensor != null) {
                gyroSensor.addObserver(this);
                gyroSensor.register();
            }

            if (accelSensor == null && gyroSensor == null && listener != null) {
                listener.onStepDetected(-1); // No fallback possible
            }
        }
    }

    public void stop() {
        if (stepSensor != null) {
            stepSensor.unregister();
            stepSensor.removeObserver(this);
        }
        if (accelSensor != null) {
            accelSensor.unregister();
            accelSensor.removeObserver(this);
        }
        if (gyroSensor != null) {
            gyroSensor.unregister();
            gyroSensor.removeObserver(this);
        }
    }

    public void pause() {
        isPaused = true;

        if (stepSensor != null) stepSensor.unregister();
        if (accelSensor != null) accelSensor.unregister();
        if (gyroSensor != null) gyroSensor.unregister();
    }

    public void resume() {
        isPaused = false;

        if (stepSensor != null) stepSensor.register();
        if (accelSensor != null) accelSensor.register();
        if (gyroSensor != null) gyroSensor.register();
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (isPaused) return;

        long currentTime = System.currentTimeMillis();

        if (sensorType == Sensor.TYPE_STEP_COUNTER) {
            int total = (int) ((XFloatSensor) stepSensor).getX();
            if (initialStepCount == -1) initialStepCount = total;
            int relativeSteps = total - initialStepCount;
            if (listener != null) listener.onStepDetected(relativeSteps);
        }

        else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            float z = ((XYZFloatSensor) accelSensor).getZ();
            boolean isRising = z > lastAccelZ;

            // Detect peak
            if (!isRising && accelRising && (lastAccelZ > ACCEL_THRESHOLD)) {
                // Wait for gyro confirmation
                if (lastGyroZ > GYRO_THRESHOLD && (currentTime - lastStepTime > STEP_INTERVAL_MS)) {
                    fallbackStepCount++;
                    lastStepTime = currentTime;
                    if (listener != null) listener.onStepDetected(fallbackStepCount);
                }
            }

            accelRising = isRising;
            lastAccelZ = z;
        }

        else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            float z = ((XYZFloatSensor) gyroSensor).getZ();
            lastGyroZ = Math.abs(z);
        }
    }
}
