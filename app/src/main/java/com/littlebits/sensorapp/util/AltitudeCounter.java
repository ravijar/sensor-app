package com.littlebits.sensorapp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XFloatSensor;

public class AltitudeCounter implements SensorObserver {

    public interface AltitudeListener {
        void onAltitudeChanged(double cumulativeGain);
    }

    private final Context context;
    private final LocationManager locationManager;
    private final SensorRepository repository;
    private final BaseSensor pressureSensor;

    private float lastAltitude = -1f;
    private float cumulativeAltitudeGain = 0f;
    private AltitudeListener listener;

    private final LocationListener gpsListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            float gpsAltitude = (float) location.getAltitude();
            processAltitude(gpsAltitude);
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
            pressureSensor.addObserver(this);
            pressureSensor.register();
        } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, gpsListener);
        } else {
            if (listener != null) listener.onAltitudeChanged(-1);
        }
    }

    public void stop() {
        if (pressureSensor != null) {
            pressureSensor.unregister();
            pressureSensor.removeObserver(this);
        }
        locationManager.removeUpdates(gpsListener);
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (sensorType == Sensor.TYPE_PRESSURE) {
            float pressure = ((XFloatSensor) pressureSensor).getX(); // in hPa
            float estimatedAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
            processAltitude(estimatedAltitude);
        }
    }

    private void processAltitude(float currentAltitude) {
        if (lastAltitude > 0) {
            float delta = currentAltitude - lastAltitude;
            if (delta > 0.3f) { // filter noise, only consider meaningful gains
                cumulativeAltitudeGain += delta;
                if (listener != null) {
                    listener.onAltitudeChanged(cumulativeAltitudeGain);
                }
            }
        }
        lastAltitude = currentAltitude;
    }
}
