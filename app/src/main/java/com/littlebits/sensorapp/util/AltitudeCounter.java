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
import com.littlebits.sensorapp.sensor.XFloatSensor;

public class AltitudeCounter implements SensorObserver {

    public interface AltitudeListener {
        void onAltitudeChanged(double altitudeMeters);
    }

    private final Context context;
    private final LocationManager locationManager;
    private final SensorRepository repository;
    private final BaseSensor pressureSensor;

    private AltitudeListener listener;
    private boolean isPaused = false;
    private boolean usingPressure = false;

    private float lastPressureAltitude = -1f;
    private float lastGpsAltitude = -1f;
    private float fusedAltitude = -1f;

    private long lastUpdateTime = 0;

    // Complementary filter parameter (tune alpha between 0 and 1)
    private static final float ALPHA = 0.02f;

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (!isPaused) {
                float gpsAltitude = (float) location.getAltitude();
                lastGpsAltitude = gpsAltitude;
                fuseAltitude();
            }
        }
    };

    public AltitudeCounter(Context context) {
        this.context = context;
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.repository = SensorRepository.getInstance();
        this.repository.initializeSensors((SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        this.pressureSensor = repository.getSensor(Sensor.TYPE_PRESSURE);
    }

    public void start(AltitudeListener listener) {
        this.listener = listener;

        if (pressureSensor != null) {
            usingPressure = true;
            pressureSensor.addObserver(this);
            pressureSensor.register();
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        }

        if (!usingPressure && listener != null) {
            // No pressure sensor, will rely on GPS only
        }
    }

    public void stop() {
        if (pressureSensor != null) {
            pressureSensor.unregister();
            pressureSensor.removeObserver(this);
        }
        locationManager.removeUpdates(gpsListener);
    }

    public void pause() {
        isPaused = true;
        if (usingPressure && pressureSensor != null) {
            pressureSensor.unregister();
        }
        locationManager.removeUpdates(gpsListener);
    }

    public void resume() {
        isPaused = false;
        if (usingPressure && pressureSensor != null) {
            pressureSensor.register();
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        }
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (isPaused) return;

        if (sensorType == Sensor.TYPE_PRESSURE) {
            float pressure = ((XFloatSensor) pressureSensor).getX(); // in hPa
            lastPressureAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            fuseAltitude();
        }
    }

    private void fuseAltitude() {
        if (lastPressureAltitude < 0 && lastGpsAltitude < 0) {
            return; // no data yet
        }

        long now = SystemClock.elapsedRealtime();
        if (lastUpdateTime == 0) {
            // Initialize fused altitude
            if (lastPressureAltitude >= 0 && lastGpsAltitude >= 0) {
                fusedAltitude = (lastPressureAltitude + lastGpsAltitude) / 2f;
            } else if (lastPressureAltitude >= 0) {
                fusedAltitude = lastPressureAltitude;
            } else {
                fusedAltitude = lastGpsAltitude;
            }
        } else {
            float dt = (now - lastUpdateTime) / 1000f;

            // Complementary filter fusion
            // Barometer is good for short-term changes, GPS good for long-term stability
            if (lastPressureAltitude >= 0 && lastGpsAltitude >= 0) {
                fusedAltitude = ALPHA * lastGpsAltitude + (1 - ALPHA) * (fusedAltitude + (lastPressureAltitude - fusedAltitude));
            } else if (lastPressureAltitude >= 0) {
                fusedAltitude = lastPressureAltitude;
            } else if (lastGpsAltitude >= 0) {
                fusedAltitude = lastGpsAltitude;
            }
        }

        lastUpdateTime = now;

        if (listener != null) {
            listener.onAltitudeChanged(fusedAltitude);
        }
    }

    public float getFusedAltitude() {
        return fusedAltitude;
    }
}
