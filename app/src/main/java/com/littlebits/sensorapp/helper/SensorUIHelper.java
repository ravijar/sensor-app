package com.littlebits.sensorapp.helper;

import android.content.Context;
import android.hardware.Sensor;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.littlebits.sensorapp.R;

public class SensorUIHelper {
    private TextView valueX, valueY, valueZ, biasX, biasY, biasZ, value;

    public View inflateSensorView(Context context, int sensorType, LinearLayout sensorValueContainer) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View sensorView = null;

        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
            case Sensor.TYPE_ROTATION_VECTOR:
                sensorView = inflater.inflate(R.layout.item_accelerometer, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.accelX);
                valueY = sensorView.findViewById(R.id.accelY);
                valueZ = sensorView.findViewById(R.id.accelZ);
                break;

            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES:
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED:
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES:
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED:
                sensorView = inflater.inflate(R.layout.item_accelerometer_limited, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.accelX);
                valueY = sensorView.findViewById(R.id.accelY);
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                sensorView = inflater.inflate(R.layout.item_accelerometer_bias, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.accelX);
                valueY = sensorView.findViewById(R.id.accelY);
                valueZ = sensorView.findViewById(R.id.accelZ);
                biasX = sensorView.findViewById(R.id.biasX);
                biasY = sensorView.findViewById(R.id.biasY);
                biasZ = sensorView.findViewById(R.id.biasZ);
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_TEMPERATURE:
                sensorView = inflater.inflate(R.layout.item_temperature, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.temperatureValue);
                break;

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_HEAD_TRACKER:
                sensorView = inflater.inflate(R.layout.item_head_tracker, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.rotX);
                valueY = sensorView.findViewById(R.id.rotY);
                valueZ = sensorView.findViewById(R.id.rotZ);
                value = sensorView.findViewById(R.id.scalar);
                break;

            case Sensor.TYPE_HEADING:
                sensorView = inflater.inflate(R.layout.item_heading, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.headingValue);
                break;

            case Sensor.TYPE_HEART_BEAT:
                sensorView = inflater.inflate(R.layout.item_heart_beat, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.heartBeatValue);
                break;

            case Sensor.TYPE_HEART_RATE:
                sensorView = inflater.inflate(R.layout.item_heart_rate, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.heartRateValue);
                break;

            case Sensor.TYPE_HINGE_ANGLE:
                sensorView = inflater.inflate(R.layout.item_hinge_angle, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.hingeAngle);
                break;

            case Sensor.TYPE_LIGHT:
                sensorView = inflater.inflate(R.layout.item_light_sensor, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.lightValue);
                break;

            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
            case Sensor.TYPE_MOTION_DETECT:
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_STATIONARY_DETECT:
            case Sensor.TYPE_STEP_DETECTOR:
                sensorView = inflater.inflate(R.layout.item_off_body_detect, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.detect);
                break;

            case Sensor.TYPE_ORIENTATION:
                sensorView = inflater.inflate(R.layout.item_orientation, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.azimuth);
                valueY = sensorView.findViewById(R.id.pitch);
                valueZ = sensorView.findViewById(R.id.roll);
                break;

            case Sensor.TYPE_POSE_6DOF:
                sensorView = inflater.inflate(R.layout.item_pose_6dof, sensorValueContainer, false);
                valueX = sensorView.findViewById(R.id.accelX);
                valueY = sensorView.findViewById(R.id.accelY);
                valueZ = sensorView.findViewById(R.id.accelZ);
                biasX = sensorView.findViewById(R.id.quarX);
                biasY = sensorView.findViewById(R.id.quarY);
                biasZ = sensorView.findViewById(R.id.quarZ);
                break;

            case Sensor.TYPE_PRESSURE:
                sensorView = inflater.inflate(R.layout.item_pressure, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.pressureValue);
                break;

            case Sensor.TYPE_PROXIMITY:
                sensorView = inflater.inflate(R.layout.item_proximity, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.proximityValue);
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorView = inflater.inflate(R.layout.item_humidity, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.humidityValue);
                break;

            case Sensor.TYPE_STEP_COUNTER:
                sensorView = inflater.inflate(R.layout.item_step_counter, sensorValueContainer, false);
                value = sensorView.findViewById(R.id.stepValue);
                break;

            default:
                break;
        }

        return sensorView;
    }

    public void updateSensorValues(int sensorType, float[] values) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_GRAVITY:
            case Sensor.TYPE_LINEAR_ACCELERATION:
                if (valueX != null) valueX.setText("X: " + values[0] + " m/s²");
                if (valueY != null) valueY.setText("Y: " + values[1] + " m/s²");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " m/s²");
                break;

            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES:
            case Sensor.TYPE_ACCELEROMETER_LIMITED_AXES_UNCALIBRATED:
                if (valueX != null) valueX.setText("X: " + values[0] + " m/s²");
                if (valueY != null) valueY.setText("Y: " + values[1] + " m/s²");
                break;

            case Sensor.TYPE_ACCELEROMETER_UNCALIBRATED:
                if (valueX != null) valueX.setText("X: " + values[0] + " m/s²");
                if (valueY != null) valueY.setText("Y: " + values[1] + " m/s²");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " m/s²");
                if (biasX != null) biasX.setText("Bias X: " + values[3]);
                if (biasY != null) biasY.setText("Bias Y: " + values[4]);
                if (biasZ != null) biasZ.setText("Bias Z: " + values[5]);
                break;

            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_TEMPERATURE:
                if (value != null) value.setText("Temperature: " + values[0] + " °C");
                break;

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
            case Sensor.TYPE_HEAD_TRACKER:
                if (valueX != null) valueX.setText("X: " + values[0]);
                if (valueY != null) valueY.setText("Y: " + values[1]);
                if (valueZ != null) valueZ.setText("Z: " + values[2]);
                if (value != null) valueZ.setText("Scalar: " + values[3]);
                break;

            case Sensor.TYPE_ROTATION_VECTOR:
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                if (valueX != null) valueX.setText("X: " + values[0]);
                if (valueY != null) valueY.setText("Y: " + values[1]);
                if (valueZ != null) valueZ.setText("Z: " + values[2]);
                break;

            case Sensor.TYPE_GYROSCOPE:
                if (valueX != null) valueX.setText("X: " + values[0] + " rad/s");
                if (valueY != null) valueY.setText("Y: " + values[1] + " rad/s");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " rad/s");
                break;

            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES:
            case Sensor.TYPE_GYROSCOPE_LIMITED_AXES_UNCALIBRATED:
                if (valueX != null) valueX.setText("X: " + values[0] + " rad/s");
                if (valueY != null) valueY.setText("Y: " + values[1] + " rad/s");
                break;

            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                if (valueX != null) valueX.setText("X: " + values[0] + " rad/s");
                if (valueY != null) valueY.setText("Y: " + values[1] + " rad/s");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " rad/s");
                if (biasX != null) biasX.setText("Bias X: " + values[3]);
                if (biasY != null) biasY.setText("Bias Y: " + values[4]);
                if (biasZ != null) biasZ.setText("Bias Z: " + values[5]);
                break;

            case Sensor.TYPE_HEADING:
                if (value != null) value.setText("Heading: " + values[0]);
                break;

            case Sensor.TYPE_HEART_BEAT:
                if (value != null) value.setText("Heart Beat: " + values[0] + " BPM");
                break;

            case Sensor.TYPE_HEART_RATE:
                if (value != null) value.setText("Heart Rate: " + values[0] + " °");
                break;

            case Sensor.TYPE_HINGE_ANGLE:
                if (value != null) value.setText("Hinge Angle: " + values[0] + " °");
                break;

            case Sensor.TYPE_LIGHT:
                if (value != null) value.setText("Light: " + values[0] + " Lux");
                break;

            case Sensor.TYPE_LOW_LATENCY_OFFBODY_DETECT:
            case Sensor.TYPE_MOTION_DETECT:
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_STATIONARY_DETECT:
            case Sensor.TYPE_STEP_DETECTOR:
                if (value != null) value.setText("Detect: " + values[0]);
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                if (valueX != null) valueX.setText("X: " + values[0] + " µT");
                if (valueY != null) valueY.setText("Y: " + values[1] + " µT");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " µT");
                break;

            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                if (valueX != null) valueX.setText("X: " + values[0] + " µT");
                if (valueY != null) valueY.setText("Y: " + values[1] + " µT");
                if (valueZ != null) valueZ.setText("Z: " + values[2] + " µT");
                if (biasX != null) biasX.setText("Bias X: " + values[3]);
                if (biasY != null) biasY.setText("Bias Y: " + values[4]);
                if (biasZ != null) biasZ.setText("Bias Z: " + values[5]);
                break;

            case Sensor.TYPE_ORIENTATION:
                if (valueX != null) valueX.setText("Azimuth: " + values[0] + " °");
                if (valueY != null) valueY.setText("Pitch: " + values[1] + " °");
                if (valueZ != null) valueZ.setText("Roll: " + values[2] + " °");
                break;

            case Sensor.TYPE_POSE_6DOF:
                if (valueX != null) valueX.setText("X: " + values[0]);
                if (valueY != null) valueY.setText("Y: " + values[1]);
                if (valueZ != null) valueZ.setText("Z: " + values[2]);
                if (biasX != null) biasX.setText("Quaternion X: " + values[3]);
                if (biasY != null) biasY.setText("Quaternion Y: " + values[4]);
                if (biasZ != null) biasZ.setText("Quaternion Z: " + values[5]);
                break;

            case Sensor.TYPE_PRESSURE:
                if (value != null) value.setText("Pressure: " + values[0] + " hPa");
                break;

            case Sensor.TYPE_PROXIMITY:
                if (value != null) value.setText("Distance: " + values[0] + " cm");
                break;

            case Sensor.TYPE_RELATIVE_HUMIDITY:
                if (value != null) value.setText("Humidity: " + values[0] + " % RH");
                break;

            case Sensor.TYPE_STEP_COUNTER:
                if (value != null) value.setText("Steps: " + values[0]);
                break;

            default:
                break;
        }
    }
}