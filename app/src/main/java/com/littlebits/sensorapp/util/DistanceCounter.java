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

public class DistanceCounter implements SensorObserver {

    public interface DistanceListener {
        void onDistanceChanged(double meters);
    }

    private final Context context;
    private final SensorRepository repository;
    private DistanceListener listener;

    private BaseSensor stepSensor;
    private BaseSensor accelerometerSensor;

    private int initialSteps = -1;
    private int estimatedSteps = 0;
    private double currentDistance = 0.0;

    private long lastStepTime = 0;
    private static final long STEP_DEBOUNCE_MS = 300;

    // For dynamic step length estimation
    private double lastStepFrequency = 0;
    private long lastFrequencyCalcTime = 0;

    // Median filter buffer for acceleration magnitude
    private final Queue<Float> accelBuffer = new LinkedList<>();
    private static final int FILTER_WINDOW_SIZE = 5;

    // Peak detection variables
    private boolean accelRising = false;
    private float lastFilteredAccel = 9.8f; // gravity baseline approx
    private static final float ACCEL_PEAK_THRESHOLD = 11.0f;  // Peak detection threshold

    private boolean isPaused = false;

    public DistanceCounter(Context context) {
        this.context = context;
        this.repository = SensorRepository.getInstance();
        repository.initializeSensors((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));

        stepSensor = repository.getSensor(Sensor.TYPE_STEP_COUNTER);
        accelerometerSensor = repository.getSensor(Sensor.TYPE_ACCELEROMETER);
    }

    public void start(DistanceListener listener) {
        this.listener = listener;

        if (stepSensor != null) {
            stepSensor.addObserver(this);
            stepSensor.register();
        } else if (accelerometerSensor != null) {
            accelerometerSensor.addObserver(this);
            accelerometerSensor.register();
        } else {
            if (listener != null) listener.onDistanceChanged(-1); // Not supported
        }
    }

    public void stop() {
        if (stepSensor != null) {
            stepSensor.unregister();
            stepSensor.removeObserver(this);
        }
        if (accelerometerSensor != null) {
            accelerometerSensor.unregister();
            accelerometerSensor.removeObserver(this);
        }

        initialSteps = -1;
        estimatedSteps = 0;
        currentDistance = 0.0;
        lastStepFrequency = 0;
        lastFrequencyCalcTime = 0;
    }

    public void pause() {
        isPaused = true;
        if (stepSensor != null) stepSensor.unregister();
        if (accelerometerSensor != null) accelerometerSensor.unregister();
    }

    public void resume() {
        isPaused = false;
        if (stepSensor != null) stepSensor.register();
        if (accelerometerSensor != null) accelerometerSensor.register();
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (isPaused) return;

        if (sensorType == Sensor.TYPE_STEP_COUNTER) {
            int totalSteps = (int) ((XFloatSensor) stepSensor).getX();
            if (initialSteps == -1) initialSteps = totalSteps;
            int deltaSteps = totalSteps - initialSteps;
            currentDistance = deltaSteps * 0.78; // average step length fallback
            if (listener != null) listener.onDistanceChanged(currentDistance);
        }

        else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            XYZFloatSensor acc = (XYZFloatSensor) accelerometerSensor;

            // Compute resultant acceleration magnitude
            float accMag = computeResultant(acc.getX(), acc.getY(), acc.getZ());
            float filteredAcc = applyMedianFilter(accMag);

            long currentTime = System.currentTimeMillis();

            boolean isRisingNow = filteredAcc > lastFilteredAccel;

            // Peak detection: from rising to falling over threshold
            if (!isRisingNow && accelRising && (lastFilteredAccel > ACCEL_PEAK_THRESHOLD)) {
                // Debounce: count step if enough time elapsed
                if (currentTime - lastStepTime > STEP_DEBOUNCE_MS) {
                    estimatedSteps++;
                    lastStepTime = currentTime;

                    // Calculate step frequency dynamically every second
                    if (lastFrequencyCalcTime == 0) lastFrequencyCalcTime = currentTime;
                    double timeDiffSec = (currentTime - lastFrequencyCalcTime) / 1000.0;

                    if (timeDiffSec >= 1.0) {
                        lastStepFrequency = estimatedSteps / timeDiffSec; // steps per second
                        lastFrequencyCalcTime = currentTime;
                        estimatedSteps = 0;
                    }

                    // Dynamic step length based on step frequency
                    double dynamicStepLength = calculateStepLength(lastStepFrequency);

                    currentDistance += dynamicStepLength;
                    if (listener != null) listener.onDistanceChanged(currentDistance);
                }
            }

            accelRising = isRisingNow;
            lastFilteredAccel = filteredAcc;
        }
    }

    private double calculateStepLength(double stepFrequency) {
        // Simple model from the paper: step length grows with sqrt(step frequency)
        // Constants can be calibrated for better accuracy
        if (stepFrequency <= 0) return 0.7; // fallback step length (meters)

        return 0.45 + 0.35 * Math.sqrt(stepFrequency); // example coefficients
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

    public double getCurrentDistance() {
        return currentDistance;
    }
}
