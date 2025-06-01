package com.littlebits.sensorapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.model.Workout;

import java.util.ArrayList;
import java.util.List;

public class PastWorkoutsActivity extends AppCompatActivity {

    private LinearLayout workoutLayout;

    private List<Workout> workoutList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_workouts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        workoutLayout = findViewById(R.id.workoutLayout);

        workoutList = new ArrayList<>();
        workoutList.add(new Workout("Monday, 04", "March 2025", "8.00-10.00"));
        workoutList.add(new Workout("Tuesday, 05", "March 2025", "9.00-11.00"));
        workoutList.add(new Workout("Wednesday, 06", "March 2025", "10.00-12.00"));

        populateWorkoutList();
    }

    public void onBackClicked(View view) {
        finish();
    }

    private void populateWorkoutList() {
        for (Workout workout : workoutList) {
            // Inflate the workout card layout template
            View workoutCard = getLayoutInflater().inflate(R.layout.item_workout, workoutLayout, false);

            // Get the views in the template
            TextView workoutDate = workoutCard.findViewById(R.id.workoutDate);
            TextView workoutMonthYear = workoutCard.findViewById(R.id.workoutMonthYear);
            TextView workoutTime = workoutCard.findViewById(R.id.workoutTime);
            ImageButton viewButton = workoutCard.findViewById(R.id.viewWorkoutButton);

            // Set the workout data to the template's views
            workoutDate.setText(workout.getDate());
            workoutMonthYear.setText(workout.getMonthYear());
            workoutTime.setText(workout.getDuration());

            // Set the button click listener for "View Workout"
            viewButton.setOnClickListener(v -> onViewWorkoutClicked(workout));

            // Add the workout card to the parent layout
            workoutLayout.addView(workoutCard);
        }
    }

    // Method called when a "View Workout" button is clicked
    public void onViewWorkoutClicked(Workout workout) {
        // You can pass workout details to the next activity (e.g., WorkoutSummaryActivity)
        Intent intent = new Intent(this, WorkoutSummaryActivity.class);
        intent.putExtra("workout_date", workout.getDate());
        intent.putExtra("workout_time", workout.getDuration());
        intent.putExtra("workout_month_year", workout.getMonthYear());
        startActivity(intent);
    }
}
