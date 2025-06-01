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
import com.littlebits.sensorapp.repository.WorkoutRepository;

import java.util.List;

public class PastWorkoutsActivity extends AppCompatActivity {

    private LinearLayout workoutLayout;
    private List<Workout> workoutList;
    private WorkoutRepository workoutRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_workouts);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        workoutLayout = findViewById(R.id.workoutLayout);
        workoutRepository = new WorkoutRepository(this);

        loadWorkoutsFromDatabase();
    }

    public void onBackClicked(View view) {
        finish();
    }

    private void loadWorkoutsFromDatabase() {
        workoutList = workoutRepository.getAllWorkouts();
        if (workoutList != null && !workoutList.isEmpty()) {
            populateWorkoutList();
        } else {
            // Optional: show a message like "No workouts recorded yet"
        }
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

    private void onViewWorkoutClicked(Workout workout) {
        Intent intent = new Intent(this, WorkoutSummaryActivity.class);
        intent.putExtra("workout_obj", workout); // 💡 Passed as serializable object
        startActivity(intent);
    }
}
