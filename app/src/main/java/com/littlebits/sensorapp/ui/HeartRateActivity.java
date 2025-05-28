package com.littlebits.sensorapp.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.HeartRateManager;

public class HeartRateActivity extends AppCompatActivity {

    private TextView heartRateValue;
    private SurfaceView heartRateCameraSurface;
    private HeartRateManager heartRateManager;
    private Handler handler = new Handler();
    private static final int REQUEST_CAMERA_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        heartRateValue = findViewById(R.id.heartRateValue);
        heartRateCameraSurface = findViewById(R.id.heartRateCameraSurface);
        heartRateManager = new HeartRateManager(this);

        // Request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            handler.postDelayed(this::startHeartRateMeasurement, 2000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handler.postDelayed(this::startHeartRateMeasurement, 2000);
            } else {
                heartRateValue.setText("Camera permission is required for heart rate measurement.");
            }
        }
    }

    private void startHeartRateMeasurement() {
        heartRateValue.setText("Measuring...");
        heartRateManager.measureHeartRate(new HeartRateManager.HeartRateListener() {
            @Override
            public void onHeartRateMeasured(int heartRate) {
                runOnUiThread(() -> heartRateValue.setText(heartRate + " BPM"));
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> heartRateValue.setText("Error: " + error));
            }
        }, heartRateCameraSurface);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (heartRateManager != null) {
            heartRateManager.stopMeasurement();
        }
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onDoneClicked(View view) {
        finish();
    }
}
