package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.AltitudeCounter;
import com.littlebits.sensorapp.util.DistanceCounter;
import com.littlebits.sensorapp.util.SpeedCounter;
import com.littlebits.sensorapp.util.StepsCounter;
import com.littlebits.sensorapp.util.WorkoutTimer;

public class WorkoutActivity extends AppCompatActivity implements WorkoutTimer.TimerListener {

    private TextView timerTextView;
    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView altitudeTextView;

    private Button endWorkoutButton;
    private ImageView pauseButton;

    private WorkoutTimer workoutTimer;
    private StepsCounter stepsCounter;
    private DistanceCounter distanceCounter;
    private SpeedCounter speedCounter;
    private AltitudeCounter altitudeCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // UI components
        timerTextView = findViewById(R.id.timer);
        stepCountTextView = findViewById(R.id.stepCountText);
        distanceTextView = findViewById(R.id.distanceText);
        speedTextView = findViewById(R.id.speedText);
        altitudeTextView = findViewById(R.id.altitudeText);

        endWorkoutButton = findViewById(R.id.endWorkoutButton);
        pauseButton = findViewById(R.id.pauseButton);

        // Timer
        workoutTimer = new WorkoutTimer(this);
        if (savedInstanceState != null) {
            workoutTimer.saveState(
                    savedInstanceState.getInt("seconds"),
                    savedInstanceState.getBoolean("running"),
                    savedInstanceState.getBoolean("wasRunning")
            );
        }
        workoutTimer.startTimer();

        // Step counter
        stepsCounter = new StepsCounter(this);
        stepsCounter.start(totalSteps -> {
            if (totalSteps >= 0 && stepCountTextView != null) {
                runOnUiThread(() -> stepCountTextView.setText(String.valueOf(totalSteps)));
            }
        });

        // Distance counter
        distanceCounter = new DistanceCounter(this);
        distanceCounter.start(distance -> {
            if (distanceTextView != null) {
                runOnUiThread(() -> distanceTextView.setText(String.format("%.2f", distance)));
            }
        });

        // Speed counter
        speedCounter = new SpeedCounter(this);
        speedCounter.start(speedKmph -> {
            if (speedKmph >= 0 && speedTextView != null) {
                runOnUiThread(() -> speedTextView.setText(String.format("%.1f", speedKmph)));
            }
        });

        // Altitude counter 👇
        altitudeCounter = new AltitudeCounter(this);
        altitudeCounter.start(altitudeMeters -> {
            if (altitudeTextView != null) {
                runOnUiThread(() -> altitudeTextView.setText(String.format("%.1f", altitudeMeters)));
            }
        });

        // Buttons
        endWorkoutButton.setOnClickListener(v -> finishWorkout());
        pauseButton.setOnClickListener(v -> togglePause());
    }

    private void togglePause() {
        if (workoutTimer.isRunning()) {
            workoutTimer.pauseTimer();
            pauseButton.setImageResource(R.drawable.ic_play);
        } else {
            workoutTimer.resumeTimer();
            pauseButton.setImageResource(R.drawable.ic_pause);
        }
    }

    private void finishWorkout() {
        workoutTimer.stopTimer();
        if (stepsCounter != null) stepsCounter.stop();
        if (distanceCounter != null) distanceCounter.stop();
        if (speedCounter != null) speedCounter.stop();
        if (altitudeCounter != null) altitudeCounter.stop();
        finish();
    }

    @Override
    public void onTimeUpdate(String formattedTime) {
        timerTextView.setText(formattedTime);
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

    @Override
    public void onBackPressed() {
        if (stepsCounter != null) stepsCounter.stop();
        if (distanceCounter != null) distanceCounter.stop();
        if (speedCounter != null) speedCounter.stop();
        if (altitudeCounter != null) altitudeCounter.stop();
        super.onBackPressed();
    }
}
