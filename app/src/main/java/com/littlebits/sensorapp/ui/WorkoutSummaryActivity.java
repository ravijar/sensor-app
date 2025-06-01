package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.model.Workout;

import java.util.Locale;

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

        // Get workout from Intent
        Workout workout = (Workout) getIntent().getSerializableExtra("workout_obj");
        if (workout != null) {
            timerText.setText(formatSeconds(workout.getTime()));
            stepsCount.setText(String.valueOf(workout.getSteps()));
            distanceCount.setText(String.format(Locale.getDefault(), "%.0f", workout.getDistance()));
            speedCount.setText(String.format(Locale.getDefault(), "%.1f", workout.getSpeed()));
            altitudeCount.setText(String.format(Locale.getDefault(), "%.0f", workout.getAltitude()));
            caloriesCount.setText(String.format(Locale.getDefault(), "%.0f", workout.getCalories()));
        }
    }

    private String formatSeconds(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d : %02d : %02d", hours, minutes, seconds);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onDoneClicked(View view) {
        finish();
    }

}
