package com.littlebits.sensorapp.activity;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;
import com.littlebits.sensorapp.service.ActivityRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import ai.onnxruntime.OrtException;

public class MainActivity extends AppCompatActivity implements SensorObserver {
    private ActivityRecognizer activityRecognizer;
    private TextView activityText;
    private XYZFloatSensor accelerometer, gyroscope;
    private float[] sensorData = new float[6]; // Holds latest sensor values

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityText = findViewById(R.id.activityText);

        // Get accelerometer & gyroscope sensors
        accelerometer = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_GYROSCOPE);

        // Register observers
        accelerometer.addObserver(this);
        gyroscope.addObserver(this);

        try {
            // Load ONNX model
            File modelFile = new File(getFilesDir(), "activity_recognition_new.onnx");
            if (!modelFile.exists()) {
                copyAsset("activity_recognition_new.onnx", modelFile);
            }
            activityRecognizer = new ActivityRecognizer(modelFile.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void predictActivity(float[] sensorData) {
        try {
            int activityIndex = activityRecognizer.predictActivity(sensorData);

            // Activity labels (in the same order as training)
            String[] activities = {
                    "Standing Still", "Sitting Still", "Talking while Sitting", "Talking while Standing/Walking",
                    "Standing Up and Sitting Down", "Laying Still", "Standing Up and Laying Down",
                    "Picking up an Object", "Jumping", "Push-ups", "Sit-ups", "Walking",
                    "Walking Backward", "Walking in a Circle", "Running",
                    "Ascending Stairs", "Descending Stairs", "Playing Table Tennis"
            };

            // Update UI only if a valid prediction is made
            if (activityIndex >= 0 && activityIndex < activities.length) {
                activityText.setText(String.format("Predicted Activity: %s", activities[activityIndex]));
            }
        } catch (OrtException e) {
            e.printStackTrace();
        }
    }

    private void copyAsset(String assetName, File outFile) throws Exception {
        InputStream in = getAssets().open(assetName);
        FileOutputStream out = new FileOutputStream(outFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
    }

    public void openAvailableSensors(View view) {
        Intent intent = new Intent(this, AvailableSensorsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSensorChanged() {
        // Read sensor values
        sensorData[0] = accelerometer.getX();
        sensorData[1] = accelerometer.getY();
        sensorData[2] = accelerometer.getZ();
        sensorData[3] = gyroscope.getX();
        sensorData[4] = gyroscope.getY();
        sensorData[5] = gyroscope.getZ();

        // Make prediction
        predictActivity(sensorData);
    }
}
