package com.littlebits.sensorapp.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class WorkoutActivity extends AppCompatActivity {

    private TextView timerTextView;
    private Button endWorkoutButton;
    private ImageView pauseButton;

    private int seconds = 0;
    private boolean running = true;
    private boolean wasRunning;
    private Handler handler = new Handler();

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
        pauseButton = findViewById(R.id.pauseButton); // Fixed: Initialize properly

        // Restore state if exists (for screen rotation cases)
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
            running = savedInstanceState.getBoolean("running");
            wasRunning = savedInstanceState.getBoolean("wasRunning");
        }

        // Start timer
        runTimer();

        // Set button click listeners
        endWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishWorkout();
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePause();
            }
        });
    }

    private void runTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;

                // Update timer text
                String time = String.format("%02d : %02d : %02d", hours, minutes, secs);
                timerTextView.setText(time);

                if (running) {
                    seconds++;
                }

                // Run again after 1 second if activity is not finishing
                if (!isFinishing()) {
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    private void togglePause() {
        running = !running;
        if (running) {
            pauseButton.setImageResource(R.drawable.ic_pause); // Update icon to pause
        } else {
            pauseButton.setImageResource(R.drawable.ic_play); // Update icon to play
        }
    }

    private void finishWorkout() {
        running = false;
        handler.removeCallbacksAndMessages(null); // Stop timer
        finish(); // Close activity
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        outState.putBoolean("running", running);
        outState.putBoolean("wasRunning", wasRunning);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning) {
            running = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }
}
