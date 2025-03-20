package com.littlebits.sensorapp.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Hide default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set click listener to navigate to WorkoutActivity
        findViewById(R.id.startWorkoutButton).setOnClickListener(v -> startWorkout());
    }

    private void startWorkout() {
        startActivity(new Intent(this, WorkoutActivity.class));
    }
}
