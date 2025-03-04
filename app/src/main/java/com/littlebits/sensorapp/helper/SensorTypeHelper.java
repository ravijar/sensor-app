package com.littlebits.sensorapp.helper;

import android.hardware.Sensor;

public class SensorTypeHelper {

    public static String getSensorTypeName(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
                return "Accelerometer";
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES:
                return "Limited Axes Accelerometer";
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED:
                return "Uncalibrated Limited Axes Accelerometer";
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "Uncalibrated Accelerometer";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Ambient Temperature";
            case Sensor.TYPE_DEVICE_PRIVATE_BASE:
                return "Vendor Specific Sensor";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "Game Rotation Vector";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "Geomagnetic Rotation Vector";
            case Sensor.TYPE_GRAVITY:
                return "Gravity";
            case Sensor.TYPE_GYROSCOPE:
                return "Gyroscope";
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES:
                return "Limited Axes Gyroscope";
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED:
                return "Uncalibrated Limited Axes Gyroscope";
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "Uncalibrated Gyroscope";
            case Sensor.TYPE_HEADING:
                return "Heading Sensor";
            case Sensor.TYPE_HEAD_TRACKER:
                return "Head Tracker";
            case Sensor.TYPE_HEART_BEAT:
                return "Heart Beat Sensor";
            case Sensor.TYPE_HEART_RATE:
                return "Heart Rate Monitor";
            case Sensor.TYPE_HINGE_ANGLE:
                return "Hinge Angle Sensor";
            case Sensor.TYPE_LIGHT:
                return "Light Sensor";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Linear Acceleration";
            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
                return "Low Latency Off-Body Detect Sensor";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetic Field Sensor";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "Uncalibrated Magnetic Field Sensor";
            case Sensor.TYPE_MOTION_DETECT:
                return "Motion Detection Sensor";
            case Sensor.TYPE_ORIENTATION:
                return "Orientation";
            case Sensor.TYPE_POSE_6DOF:
                return "Pose 6DOF Sensor";
            case Sensor.TYPE_PRESSURE:
                return "Barometer";
            case Sensor.TYPE_PROXIMITY:
                return "Proximity Sensor";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Relative Humidity Sensor";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Rotation Vector Sensor";
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "Significant Motion Sensor";
            case Sensor.TYPE_STATIONARY_DETECT:
                return "Stationary Detection Sensor";
            case Sensor.TYPE_STEP_COUNTER:
                return "Step Counter";
            case Sensor.TYPE_STEP_DETECTOR:
                return "Step Detector";
            case Sensor.TYPE_TEMPERATURE:
                return "Temperature";
            default:
                return "Unknown";
        }
    }

    public static String getSensorCategory(int type) {
        switch (type) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES:
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED:
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES:
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_PROXIMITY:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
            case Sensor.TYPE_LIGHT:
            case Sensor.TYPE_HEART_RATE:
            case Sensor.TYPE_HEART_BEAT:
            case Sensor.TYPE_HINGE_ANGLE:
                return "Hardware";

            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_STEP_DETECTOR:
            case Sensor.TYPE_STEP_COUNTER:
            case Sensor.TYPE_MOTION_DETECT:
            case Sensor.TYPE_STATIONARY_DETECT:
                return "Software";

            default:
                return "Unknown";
        }
    }
}
