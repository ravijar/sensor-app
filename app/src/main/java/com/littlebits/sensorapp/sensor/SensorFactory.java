package com.littlebits.sensorapp.sensor;

import android.hardware.Sensor;
import android.hardware.SensorManager;

public class SensorFactory {

    public static BaseSensor createSensor(SensorManager sensorManager, int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                return new Accelerometer(sensorManager);
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES:
                return new AccelerometerLimitedAxes(sensorManager);
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED:
                return new AccelerometerLimitedAxesUncalibrated(sensorManager);
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return new AccelerometerUncalibrated(sensorManager);
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return new AmbientTemperature(sensorManager);
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return new GameRotationVector(sensorManager);
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return new GeomagneticRotationVector(sensorManager);
            case Sensor.TYPE_GRAVITY:
                return new Gravity(sensorManager);
            case Sensor.TYPE_GYROSCOPE:
                return new Gyroscope(sensorManager);
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES:
                return new GyroscopeLimitedAxes(sensorManager);
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED:
                return new GyroscopeLimitedAxesUncalibrated(sensorManager);
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return new GyroscopeUncalibrated(sensorManager);
            case Sensor.TYPE_HEADING:
                return new Heading(sensorManager);
            case Sensor.TYPE_HEAD_TRACKER:
                return new HeadTracker(sensorManager);
            case Sensor.TYPE_HEART_BEAT:
                return new HeartBeat(sensorManager);
            case Sensor.TYPE_HEART_RATE:
                return new HeartRate(sensorManager);
            case Sensor.TYPE_HINGE_ANGLE:
                return new HingeAngle(sensorManager);
            case Sensor.TYPE_LIGHT:
                return new Light(sensorManager);
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return new LinearAcceleration(sensorManager);
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return new LowLatencyOffBodyDetect(sensorManager);
            case Sensor.TYPE_MAGNETIC_FIELD:
                return new MagneticField(sensorManager);
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return new MagneticFieldUncalibrated(sensorManager);
            case Sensor.TYPE_MOTION_DETECT:
                return new MotionDetect(sensorManager);
            case Sensor.TYPE_ORIENTATION:
                return new Orientation(sensorManager);
            case Sensor.TYPE_PRESSURE:
                return new Pressure(sensorManager);
            case Sensor.TYPE_PROXIMITY:
                return new Proximity(sensorManager);
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return new RelativeHumidity(sensorManager);
            case Sensor.TYPE_ROTATION_VECTOR:
                return new RotationVector(sensorManager);
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return new SignificantMotion(sensorManager);
            case Sensor.TYPE_STATIONARY_DETECT:
                return new StationaryDetect(sensorManager);
            case Sensor.TYPE_STEP_COUNTER:
                return new StepCounter(sensorManager);
            case Sensor.TYPE_STEP_DETECTOR:
                return new StepDetector(sensorManager);
            case Sensor.TYPE_TEMPERATURE:
                return new Temperature(sensorManager);
            default:
                return null;
        }
    }
}
