package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class WorkoutSummaryActivity extends AppCompatActivity {

    // UI components
    private TextView timerText;
    private TextView stepsCount, distanceCount, speedCount, altitudeCount, caloriesCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_summary);

        // Hide default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI components
        timerText = findViewById(R.id.timer);

        stepsCount = findViewById(R.id.stepsCount);
        distanceCount = findViewById(R.id.distanceCount);
        speedCount = findViewById(R.id.speedCount);
        altitudeCount = findViewById(R.id.altitudeCount);
        caloriesCount = findViewById(R.id.caloriesCount);

        // Set initial data values (these can be dynamic based on the actual workout data)
        timerText.setText("00 : 30 : 00");
        stepsCount.setText("250");
        distanceCount.setText("200 M");
        speedCount.setText("20 km/h");
        altitudeCount.setText("+20 m");
        caloriesCount.setText("18 g");

    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onDoneClicked(View view) {
        finish();
    }

}
