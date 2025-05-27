package com.littlebits.sensorapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.WeatherFetcher;

public class HomeActivity extends AppCompatActivity implements WeatherFetcher.WeatherListener {

    private TextView weatherTempTextView, locationTextView;
    private ImageView weatherIcon;
    private WeatherFetcher weatherFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Hide default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize UI elements
        weatherTempTextView = findViewById(R.id.weatherTemp);
        locationTextView = findViewById(R.id.location);
        weatherIcon = findViewById(R.id.weatherIcon);

        // Initialize WeatherFetcher and fetch weather data
        weatherFetcher = new WeatherFetcher(this, this);
        weatherFetcher.fetchWeather();
    }

    public void onWorkoutClick(View view) {
        startActivity(new Intent(this, WorkoutActivity.class));
    }

    public void onHeartRateClick(View view) {
        startActivity(new Intent(this, HeartRateActivity.class));
    }

    public void onLogsClick(View view) {
        startActivity(new Intent(this, PastWorkoutsActivity.class));
    }

    public void onProfileClick(View view) {
        startActivity(new Intent(this, PersonalDetailsActivity.class));
    }

    @Override
    public void onWeatherFetched(String temperature, String location, String weatherCode) {
        weatherTempTextView.setText(temperature);
        locationTextView.setText(location);
        weatherIcon.setImageResource(getResources().getIdentifier("ic_" + weatherCode, "drawable", getPackageName()));
    }
}
