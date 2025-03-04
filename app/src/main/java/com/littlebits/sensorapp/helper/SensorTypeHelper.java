package com.littlebits.sensorapp.helper;

import android.hardware.Sensor;

public class SensorTypeHelper {

    // Convert sensor type integer to a readable name
    public static String getSensorTypeName(int type) {
        switch (type) {
            // Hardware sensors
            case Sensor.TYPE_ACCELEROMETER:
                return "Accelerometer";
            case Sensor.TYPE_GYROSCOPE:
                return "Gyroscope";
            case Sensor.TYPE_MAGNETIC_FIELD:
                return "Magnetometer";
            case Sensor.TYPE_LIGHT:
                return "Light Sensor";
            case Sensor.TYPE_PROXIMITY:
                return "Proximity Sensor";
            case Sensor.TYPE_PRESSURE:
                return "Barometer";
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return "Ambient Temperature";
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return "Humidity Sensor";

            // Software-based sensors (Derived from other sensors)
            case Sensor.TYPE_GRAVITY:
                return "Gravity Sensor";
            case Sensor.TYPE_LINEAR_ACCELERATION:
                return "Linear Acceleration Sensor";
            case Sensor.TYPE_ROTATION_VECTOR:
                return "Rotation Vector Sensor";
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                return "Game Rotation Vector";
            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                return "Geomagnetic Rotation Vector";

            // Motion sensors
            case Sensor.TYPE_SIGNIFICANT_MOTION:
                return "Significant Motion Sensor";
            case Sensor.TYPE_STEP_DETECTOR:
                return "Step Detector";
            case Sensor.TYPE_STEP_COUNTER:
                return "Step Counter";

            // Special sensors
            case Sensor.TYPE_POSE_6DOF:
                return "Pose 6DOF Sensor";
            case Sensor.TYPE_HEAD_TRACKER:
                return "Head Tracker Sensor";
            case Sensor.TYPE_MOTION_DETECT:
                return "Motion Detection Sensor";
            case Sensor.TYPE_STATIONARY_DETECT:
                return "Stationary Detection Sensor";
            case Sensor.TYPE_HEART_RATE:
                return "Heart Rate Sensor";
            case Sensor.TYPE_HEART_BEAT:
                return "Heart Beat Sensor";

            // Uncalibrated Sensors
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return "Uncalibrated Gyroscope";
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return "Uncalibrated Magnetometer";
            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                return "Uncalibrated Accelerometer";

            // Special Tracking Sensors
            case Sensor.TYPE_HINGE_ANGLE:
                return "Hinge Angle Sensor";
            case Sensor.TYPE_HEADING:
                return "Heading Sensor";

            // Vendor-specific or unknown sensors
            default:
                return "Vendor Sensor";
        }
    }

    // Determine if a sensor is hardware-based or software-based
    public static boolean isHardwareSensor(Sensor sensor) {
        int type = sensor.getType();
        return !(type == Sensor.TYPE_GRAVITY ||
                type == Sensor.TYPE_LINEAR_ACCELERATION ||
                type == Sensor.TYPE_ROTATION_VECTOR ||
                type == Sensor.TYPE_GAME_ROTATION_VECTOR ||
                type == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR ||
                type == Sensor.TYPE_SIGNIFICANT_MOTION ||
                type == Sensor.TYPE_STEP_DETECTOR ||
                type == Sensor.TYPE_STEP_COUNTER ||
                type == Sensor.TYPE_POSE_6DOF ||
                type == Sensor.TYPE_HEAD_TRACKER ||
                type == Sensor.TYPE_MOTION_DETECT ||
                type == Sensor.TYPE_STATIONARY_DETECT);
    }
}
