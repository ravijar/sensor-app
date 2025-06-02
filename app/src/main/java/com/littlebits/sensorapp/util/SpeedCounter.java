package com.littlebits.sensorapp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.SystemClock;

import androidx.core.app.ActivityCompat;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;

import java.util.LinkedList;
import java.util.Queue;

public class SpeedCounter implements SensorObserver {

    public interface SpeedListener {
        void onSpeedChanged(double kmph);
    }

    private final Context context;
    private final LocationManager locationManager;
    private final SensorRepository repository;
    private final BaseSensor accelSensor, gyroSensor;

    private Location lastLocation = null;
    private SpeedListener listener;

    private boolean gpsWorking = false;
    private boolean sensorsWorking = false;
    private boolean isPaused = false;

    private long lastSensorTime = 0;
    private long sessionStartTime = 0;

    private final double[] velocityEstimate = {0.0, 0.0, 0.0};
    private final float[][] kalmanP = new float[3][3];
    private float q = 0.01f; // process noise covariance (adaptive)
    private float r = 0.5f;  // measurement noise covariance

    private double distanceSumMeters = 0.0;
    private double lastSpeedKmph = 0.0;
    private double averageSpeedKmph = 0.0;

    private long lastActiveTime = 0;

    // Calibration
    private boolean calibrationComplete = false;
    private long calibrationStartTime = 0;
    private double calibrationSpeedSum = 0.0;
    private int calibrationCount = 0;

    // Phone placement detection buffer and constants
    private static final int WINDOW_SIZE = 50; // samples (~1 sec at 50Hz)
    private final Queue<Float> accelMagWindow = new LinkedList<>();
    private final Queue<Float> gyroMagWindow = new LinkedList<>();

    private enum PhonePlacement {
        UNKNOWN, POCKET, BAG
    }

    private PhonePlacement phonePlacement = PhonePlacement.UNKNOWN;

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsWorking = true;
            if (lastLocation != null && !isPaused) {
                float speed = location.getSpeed(); // m/s
                lastSpeedKmph = speed * 3.6;

                long now = SystemClock.elapsedRealtime();
                float dt = (lastSensorTime == 0) ? 0 : (now - lastSensorTime) / 1000f;
                if (dt > 0) {
                    distanceSumMeters += (speed * dt);
                    updateAverageSpeed();
                }

                lastSensorTime = now;

                // Calibration phase: accumulate GPS speeds to personalize parameters
                if (!calibrationComplete) {
                    calibrationSpeedSum += speed;
                    calibrationCount++;
                    if ((now - calibrationStartTime) >= 15000) { // calibrate after 15 seconds
                        double avgSpeed = calibrationSpeedSum / calibrationCount;
                        calibrateInertialModel(avgSpeed);
                        calibrationComplete = true;
                    }
                }

                if (listener != null) listener.onSpeedChanged(lastSpeedKmph);
            }
            lastLocation = location;
        }
    };

    public SpeedCounter(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.repository = SensorRepository.getInstance();
        this.repository.initializeSensors((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        this.accelSensor = repository.getSensor(Sensor.TYPE_ACCELEROMETER);
        this.gyroSensor = repository.getSensor(Sensor.TYPE_GYROSCOPE);

        for (int i = 0; i < 3; i++) kalmanP[i][i] = 1f;
    }

    public void start(SpeedListener listener) {
        this.listener = listener;
        sessionStartTime = SystemClock.elapsedRealtime();
        calibrationStartTime = sessionStartTime;
        calibrationComplete = false;
        calibrationSpeedSum = 0.0;
        calibrationCount = 0;
        phonePlacement = PhonePlacement.UNKNOWN;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        }

        if (accelSensor != null && gyroSensor != null) {
            sensorsWorking = true;
            accelSensor.addObserver(this);
            accelSensor.register();
            gyroSensor.addObserver(this);
            gyroSensor.register();
        }
    }

    public void stop() {
        locationManager.removeUpdates(gpsListener);
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
        if (accelSensor != null) accelSensor.unregister();
        if (gyroSensor != null) gyroSensor.unregister();
        locationManager.removeUpdates(gpsListener);
    }

    public void resume() {
        isPaused = false;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        }
        if (accelSensor != null) accelSensor.register();
        if (gyroSensor != null) gyroSensor.register();
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (isPaused || !sensorsWorking) return;

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            XYZFloatSensor acc = (XYZFloatSensor) accelSensor;
            XYZFloatSensor gyro = (XYZFloatSensor) gyroSensor;

            long now = SystemClock.elapsedRealtime();

            // Calculate magnitude of acceleration and gyro
            float accMag = computeMagnitude(acc.getX(), acc.getY(), acc.getZ());
            float gyroMag = computeMagnitude(gyro.getX(), gyro.getY(), gyro.getZ());

            // Update sliding windows
            updateSlidingWindow(accelMagWindow, accMag);
            updateSlidingWindow(gyroMagWindow, gyroMag);

            // Detect phone placement every second when window is full
            if (accelMagWindow.size() == WINDOW_SIZE && gyroMagWindow.size() == WINDOW_SIZE) {
                phonePlacement = detectPhonePlacement(accelMagWindow, gyroMagWindow);
            }

            // Motion detection thresholds
            double accVariance = variance(accelMagWindow, mean(accelMagWindow));
            double gyroVariance = variance(gyroMagWindow, mean(gyroMagWindow));
            double motionThresholdAccel = 0.05; // tune this threshold
            double motionThresholdGyro = 0.03;  // tune this threshold

            boolean isMoving = accVariance > motionThresholdAccel && gyroVariance > motionThresholdGyro;

            // Suppress speed update if stationary and decay speed smoothly
            if (!isMoving) {
                lastSpeedKmph *= 0.85;
                updateAverageSpeed();
                if (listener != null) listener.onSpeedChanged(lastSpeedKmph);
                return;
            }

            // Prepare accelerometer vector minus gravity
            float[] a = new float[]{acc.getX(), acc.getY(), acc.getZ() - 9.8f};

            float dt = (lastSensorTime == 0) ? 0 : (now - lastSensorTime) / 1000f;
            lastSensorTime = now;

            if (dt > 0) {
                // Kalman filter update
                for (int i = 0; i < 3; i++) {
                    kalmanP[i][i] += q;
                    float innovation = a[i] - (float) velocityEstimate[i];
                    float s = kalmanP[i][i] + r;
                    float k = kalmanP[i][i] / s;
                    velocityEstimate[i] += k * innovation;
                    kalmanP[i][i] *= (1 - k);
                }

                double vx = velocityEstimate[0] * dt;
                double vy = velocityEstimate[1] * dt;
                double vz = velocityEstimate[2] * dt;

                double v = Math.sqrt(vx * vx + vy * vy + vz * vz);

                // Adjust velocity based on detected phone placement
                v = adjustVelocityForPlacement(v, phonePlacement);

                // Fuse GPS and inertial speed when possible
                if (gpsWorking && calibrationComplete) {
                    lastSpeedKmph = complementaryFilter(lastSpeedKmph, v * 3.6, 0.05);
                } else {
                    lastSpeedKmph = v * 3.6;
                }

                distanceSumMeters += v;

                lastActiveTime = now;

                updateAverageSpeed();

                if (!gpsWorking && listener != null) {
                    listener.onSpeedChanged(lastSpeedKmph);
                }
            }

            // Decay speed if no movement detected for 4 seconds
            if (now - lastActiveTime > 4000) {
                lastSpeedKmph *= 0.9;
                updateAverageSpeed();
                if (listener != null) listener.onSpeedChanged(lastSpeedKmph);
            }
        }
    }

    private PhonePlacement detectPhonePlacement(Queue<Float> accWindow, Queue<Float> gyroWindow) {
        // Compute mean and variance of acceleration magnitude
        double accMean = mean(accWindow);
        double accVar = variance(accWindow, accMean);

        // Compute mean and variance of gyro magnitude
        double gyroMean = mean(gyroWindow);
        double gyroVar = variance(gyroWindow, gyroMean);

        // Heuristic thresholds from paper and experiments
        if (accMean > 11 && gyroMean > 3 && accVar > 2 && gyroVar > 1) {
            return PhonePlacement.POCKET;
        } else {
            return PhonePlacement.BAG;
        }
    }

    private double mean(Queue<Float> window) {
        double sum = 0;
        for (Float v : window) sum += v;
        return sum / window.size();
    }

    private double variance(Queue<Float> window, double mean) {
        double sumSq = 0;
        for (Float v : window) sumSq += (v - mean) * (v - mean);
        return sumSq / window.size();
    }

    private double adjustVelocityForPlacement(double velocity, PhonePlacement placement) {
        switch (placement) {
            case POCKET:
                return velocity * 1.0; // baseline
            case BAG:
                return velocity * 0.85; // empirical correction
            default:
                return velocity;
        }
    }

    private void calibrateInertialModel(double gpsSpeed) {
        // Adapt Kalman filter process noise q based on walking speed
        if (gpsSpeed < 1.5) q = 0.02f;
        else q = 0.01f;
    }

    private double complementaryFilter(double prev, double newValue, double alpha) {
        return alpha * newValue + (1 - alpha) * prev;
    }

    private void updateSlidingWindow(Queue<Float> window, float newVal) {
        if (window.size() >= WINDOW_SIZE) window.poll();
        window.offer(newVal);
    }

    private float computeMagnitude(float x, float y, float z) {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    private void updateAverageSpeed() {
        long elapsedMillis = SystemClock.elapsedRealtime() - sessionStartTime;
        if (elapsedMillis <= 0) return;
        double elapsedHours = elapsedMillis / 3600000.0;
        averageSpeedKmph = (distanceSumMeters / 1000.0) / elapsedHours;
    }

    public double getAverageSpeed() {
        return averageSpeedKmph;
    }
}
