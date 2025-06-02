package com.littlebits.sensorapp.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XFloatSensor;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

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
    private int currentStepCount = 0;

    private boolean isPaused = false;

    private static final float ACCEL_UPPER_THRESHOLD = 11.0f; // m/s^2 (~1.8g)
    private static final float ACCEL_LOWER_THRESHOLD = 9.0f;  // m/s^2 (~1g gravity)
    private static final float GYRO_THRESHOLD = 1.2f;         // rad/s
    private static final int STEP_INTERVAL_MS = 350;          // Minimum delay between steps

    // For median filter
    private final Queue<Float> accelBuffer = new LinkedList<>();
    private static final int FILTER_WINDOW_SIZE = 5;

    // State variables for step detection
    private boolean accelRising = false;
    private float lastFilteredAccel = 9.8f; // gravity approx
    private float lastGyroMagnitude = 0;
    private long lastStepTime = 0;

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
            currentStepCount = total - initialStepCount;
            if (listener != null) listener.onStepDetected(currentStepCount);
        }

        else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            XYZFloatSensor a = (XYZFloatSensor) accelSensor;

            // Compute resultant acceleration magnitude
            float accMag = computeResultant(a.getX(), a.getY(), a.getZ());
            float filteredAcc = applyMedianFilter(accMag);

            boolean isRisingNow = filteredAcc > lastFilteredAccel;

            // Detect peak: acceleration crosses upper threshold going down
            if (!isRisingNow && accelRising && (lastFilteredAccel > ACCEL_UPPER_THRESHOLD)) {
                // Confirm step with gyro magnitude and step interval
                if (lastGyroMagnitude > GYRO_THRESHOLD && (currentTime - lastStepTime > STEP_INTERVAL_MS)) {
                    fallbackStepCount++;
                    lastStepTime = currentTime;
                    currentStepCount = fallbackStepCount;
                    if (listener != null) listener.onStepDetected(currentStepCount);
                }
            }

            // Only consider step if acceleration is above lower threshold (gravity baseline)
            if (filteredAcc < ACCEL_LOWER_THRESHOLD) {
                accelRising = false;
            } else {
                accelRising = isRisingNow;
            }

            lastFilteredAccel = filteredAcc;
        }

        else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            XYZFloatSensor g = (XYZFloatSensor) gyroSensor;
            lastGyroMagnitude = computeResultant(g.getX(), g.getY(), g.getZ());
        }
    }

    private float computeResultant(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private float applyMedianFilter(float newValue) {
        if (accelBuffer.size() >= FILTER_WINDOW_SIZE) {
            accelBuffer.poll();
        }
        accelBuffer.offer(newValue);

        Float[] arr = accelBuffer.toArray(new Float[0]);
        Arrays.sort(arr);
        return arr[arr.length / 2];
    }

    public int getCurrentStepCount() {
        return currentStepCount;
    }
}
