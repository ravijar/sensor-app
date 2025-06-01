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
    private TextView heartRateStatusText;
    private SurfaceView heartRateCameraSurface;
    private HeartRateManager heartRateManager;
    private Handler handler = new Handler();
    private static final int REQUEST_CAMERA_PERMISSION = 1001;

    private boolean isAnimating = false;
    private Runnable bpmAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        heartRateValue = findViewById(R.id.heartRateValue);
        heartRateStatusText = findViewById(R.id.heartRateStatusText);
        heartRateCameraSurface = findViewById(R.id.heartRateCameraSurface);
        heartRateManager = new HeartRateManager(this);

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
                heartRateValue.setText("–");
                heartRateStatusText.setText("Camera permission is required.");
            }
        }
    }

    private void startHeartRateMeasurement() {
        heartRateValue.setText("Measuring...");
        heartRateStatusText.setText("Place your finger over the camera.");
        startBpmAnimation();

        heartRateManager.measureHeartRate(new HeartRateManager.HeartRateListener() {
            @Override
            public void onHeartRateMeasured(int heartRate) {
                stopBpmAnimation();
                runOnUiThread(() -> {
                    heartRateValue.setText(heartRate + " BPM");
                    heartRateStatusText.setText("Measurement complete.");
                });
            }

            @Override
            public void onError(String error) {
                stopBpmAnimation();
                runOnUiThread(() -> {
                    heartRateValue.setText("–");
                    heartRateStatusText.setText("Error: " + error);
                });
            }
        }, heartRateCameraSurface);
    }

    private void startBpmAnimation() {
        isAnimating = true;
        final int minBPM = 40;
        final int maxBPM = 180;
        final int step = 10;
        final int delay = 50; // ms

        handler.post(bpmAnimator = new Runnable() {
            int bpm = minBPM;
            boolean increasing = true;

            @Override
            public void run() {
                if (!isAnimating) return;

                heartRateValue.setText(bpm + " BPM");
                bpm += increasing ? step : -step;
                if (bpm >= maxBPM) increasing = false;
                if (bpm <= minBPM) increasing = true;

                handler.postDelayed(this, delay);
            }
        });
    }

    private void stopBpmAnimation() {
        isAnimating = false;
        if (bpmAnimator != null) {
            handler.removeCallbacks(bpmAnimator);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (heartRateManager != null) {
            heartRateManager.stopMeasurement();
        }
        stopBpmAnimation();
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onDoneClicked(View view) {
        finish();
    }
}
