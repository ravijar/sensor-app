package com.littlebits.sensorapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.manager.WorkoutManager;
import com.littlebits.sensorapp.model.Workout;
import com.littlebits.sensorapp.repository.WorkoutRepository;
import com.littlebits.sensorapp.util.SOSDialer;

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

    private Location currentLocation;

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

        fetchLocation(this);

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

    private void fetchLocation(Context context) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                currentLocation = location;
            }
        });
    }

    public void setActivityIcon(String activityName) {
        ImageView activityImage = findViewById(R.id.activity);

        String resourceName = "ic_" + activityName.toLowerCase().replaceAll("\\s+", "");

        int resId = getResources().getIdentifier(resourceName, "drawable", getPackageName());

        if (resId != 0) {
            activityImage.setImageResource(resId);
        }
    }


    private Location getCurrentLocation() {
        return currentLocation;
    }

    public void onHeartRateClick(View view) {
        startActivity(new Intent(this, HeartRateActivity.class));
    }

    public void onMapClick(View view) {
        Location location = getCurrentLocation();

        String uri;
        if (location != null) {
            // If location is available, open Google Maps with current coordinates
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            uri = "geo:" + lat + "," + lon + "?q=" + lat + "," + lon + "(My+Current+Location)";
        } else {
            // Default fallback location: University of Moratuwa
            uri = "geo:6.7968,79.9011?q=University+of+Moratuwa";
        }

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Google Maps not installed", Toast.LENGTH_LONG).show();
        }
    }

    public void onSOSClick(View view) {
        SOSDialer.dialSOS(this);
    }
}
