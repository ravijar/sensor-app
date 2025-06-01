package com.littlebits.sensorapp.util;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XFloatSensor;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;

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
    private static final double AVERAGE_STEP_LENGTH_M = 0.78;

    private long lastStepTime = 0;
    private static final long STEP_DEBOUNCE_MS = 300;

    // Fallback variables for accelerometer-based detection
    private float lastZ = 0;
    private boolean wasFalling = false;

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
            double distance = deltaSteps * AVERAGE_STEP_LENGTH_M;
            if (listener != null) listener.onDistanceChanged(distance);
        }

        else if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            XYZFloatSensor acc = (XYZFloatSensor) accelerometerSensor;
            float z = acc.getZ();

            long currentTime = System.currentTimeMillis();

            // Detect a rise after a fall
            if (wasFalling && z - lastZ > 2.0 && (currentTime - lastStepTime) > STEP_DEBOUNCE_MS) {
                estimatedSteps++;
                lastStepTime = currentTime;
                double distance = estimatedSteps * AVERAGE_STEP_LENGTH_M;
                if (listener != null) listener.onDistanceChanged(distance);
                wasFalling = false;  // reset state
            }

            // Detect fall phase
            if (lastZ - z > 1.5) {
                wasFalling = true;
            }

            lastZ = z;
        }
    }
}
