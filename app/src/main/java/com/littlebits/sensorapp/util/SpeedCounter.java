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
    private final float q = 0.01f;
    private final float r = 0.5f;
    private double distanceSumMeters = 0.0;

    private double lastSpeedKmph = 0.0;
    private long lastActiveTime = 0;

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            gpsWorking = true;
            if (lastLocation != null && !isPaused) {
                float speed = location.getSpeed(); // m/s
                lastSpeedKmph = speed * 3.6;
                long now = SystemClock.elapsedRealtime();
                float dt = (lastSensorTime == 0) ? 0 : (now - lastSensorTime) / 1000f;
                if (dt > 0) distanceSumMeters += (speed * dt);
                lastSensorTime = now;
                if (listener != null) listener.onSpeedChanged(getAverageSpeed());
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
        if (isPaused || !sensorsWorking || sensorType != Sensor.TYPE_ACCELEROMETER) return;

        XYZFloatSensor acc = (XYZFloatSensor) accelSensor;
        float[] a = new float[]{acc.getX(), acc.getY(), acc.getZ() - 9.8f};

        long now = SystemClock.elapsedRealtime();
        float dt = (lastSensorTime == 0) ? 0 : (now - lastSensorTime) / 1000f;
        lastSensorTime = now;

        if (dt > 0) {
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
            lastSpeedKmph = 0.8 * lastSpeedKmph + 0.2 * (v * 3.6);
            distanceSumMeters += v;

            lastActiveTime = now;

            if (!gpsWorking && listener != null) {
                listener.onSpeedChanged(getAverageSpeed());
            }
        }

        if (now - lastActiveTime > 4000) {
            lastSpeedKmph *= 0.9;
            if (listener != null) listener.onSpeedChanged(getAverageSpeed());
        }
    }

    private double getAverageSpeed() {
        long elapsedMillis = SystemClock.elapsedRealtime() - sessionStartTime;
        if (elapsedMillis <= 0) return 0.0;
        double elapsedHours = elapsedMillis / 3600000.0;
        return (distanceSumMeters / 1000.0) / elapsedHours;
    }
}
