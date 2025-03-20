package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.WorkoutTimer;

public class WorkoutActivity extends AppCompatActivity implements WorkoutTimer.TimerListener {

    private TextView timerTextView;
    private Button endWorkoutButton;
    private ImageView pauseButton;

    private WorkoutTimer workoutTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI components
        timerTextView = findViewById(R.id.timer);
        endWorkoutButton = findViewById(R.id.endWorkoutButton);
        pauseButton = findViewById(R.id.pauseButton);

        // Initialize WorkoutTimer
        workoutTimer = new WorkoutTimer(this);

        if (savedInstanceState != null) {
            workoutTimer.saveState(
                    savedInstanceState.getInt("seconds"),
                    savedInstanceState.getBoolean("running"),
                    savedInstanceState.getBoolean("wasRunning")
            );
        }

        // Start timer
        workoutTimer.startTimer();

        // Set button click listeners
        endWorkoutButton.setOnClickListener(v -> finishWorkout());

        pauseButton.setOnClickListener(v -> togglePause());
    }

    @Override
    public void onTimeUpdate(String formattedTime) {
        timerTextView.setText(formattedTime);
    }

    private void togglePause() {
        if (workoutTimer.isRunning()) {
            workoutTimer.pauseTimer();
            pauseButton.setImageResource(R.drawable.ic_play); // Update icon to play
        } else {
            workoutTimer.resumeTimer();
            pauseButton.setImageResource(R.drawable.ic_pause); // Update icon to pause
        }
    }

    private void finishWorkout() {
        workoutTimer.stopTimer();
        finish(); // Close activity
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", workoutTimer.getSeconds());
        outState.putBoolean("running", workoutTimer.isRunning());
        outState.putBoolean("wasRunning", workoutTimer.wasRunning());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (workoutTimer.wasRunning()) {
            workoutTimer.resumeTimer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        workoutTimer.setWasRunning(workoutTimer.isRunning());
        workoutTimer.pauseTimer();
    }
}
