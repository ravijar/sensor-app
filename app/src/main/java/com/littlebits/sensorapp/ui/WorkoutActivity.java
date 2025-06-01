package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.manager.WorkoutManager;
import com.littlebits.sensorapp.model.Workout;
import com.littlebits.sensorapp.repository.WorkoutRepository;

public class WorkoutActivity extends AppCompatActivity {

    private TextView timerTextView;
    private TextView stepCountTextView;
    private TextView distanceTextView;
    private TextView speedTextView;
    private TextView altitudeTextView;
    private TextView calorieTextView;
    private Button endWorkoutButton;
    private ImageView pauseButton;

    private WorkoutManager workoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        timerTextView = findViewById(R.id.timer);
        stepCountTextView = findViewById(R.id.stepCountText);
        distanceTextView = findViewById(R.id.distanceText);
        speedTextView = findViewById(R.id.speedText);
        altitudeTextView = findViewById(R.id.altitudeText);
        calorieTextView = findViewById(R.id.calorieText);
        endWorkoutButton = findViewById(R.id.endWorkoutButton);
        pauseButton = findViewById(R.id.pauseButton);

        workoutManager = new WorkoutManager(
                this,
                timerTextView,
                stepCountTextView,
                distanceTextView,
                speedTextView,
                altitudeTextView,
                calorieTextView,
                pauseButton
        );

        workoutManager.init(savedInstanceState);

        endWorkoutButton.setOnClickListener(v -> {
            Workout workout = workoutManager.getCurrentWorkout();
            workoutManager.stopAll();

            try (WorkoutRepository db = new WorkoutRepository(this)) {
                db.saveWorkout(workout);
            }

            finish();
        });

        pauseButton.setOnClickListener(v -> workoutManager.togglePause());
    }

    @Override
    protected void onResume() {
        super.onResume();
        workoutManager.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        workoutManager.pause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        workoutManager.saveState(outState);
    }

    @Override
    public void onBackPressed() {
        workoutManager.stopAll();
        super.onBackPressed();
    }
}
