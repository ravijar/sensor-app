package com.littlebits.sensorapp.util;

import android.os.SystemClock;
import com.littlebits.sensorapp.model.ActivityLabel;

import java.util.HashMap;
import java.util.Map;

public class CalorieCounter {

    public interface CalorieListener {
        void onCaloriesUpdated(double totalCalories);
    }

    private final Map<ActivityLabel, Double> metValues = new HashMap<>();
    private double totalCalories = 0.0;
    private CalorieListener listener;

    private long lastTimestamp = SystemClock.elapsedRealtime();
    private boolean isPaused = false;

    private ActivityLabel currentActivity = ActivityLabel.SITTING;
    private final double userWeightKg = 70.0; // You can make this configurable

    public CalorieCounter() {
        metValues.put(ActivityLabel.WALKING, 3.5);
        metValues.put(ActivityLabel.JOGGING, 7.0);
        metValues.put(ActivityLabel.BIKING, 6.0);
        metValues.put(ActivityLabel.SITTING, 1.5);
        metValues.put(ActivityLabel.STANDING, 2.0);
        metValues.put(ActivityLabel.UPSTAIRS, 5.0);
        metValues.put(ActivityLabel.DOWNSTAIRS, 4.0);
    }

    public void start(CalorieListener listener) {
        this.listener = listener;
        this.lastTimestamp = SystemClock.elapsedRealtime();
        this.isPaused = false;
    }

    public void updateActivity(ActivityLabel newActivity) {
        if (!isPaused) {
            updateCalories(); // Add calories since last update
        }
        currentActivity = newActivity;
    }

    public void tick() {
        if (!isPaused) {
            updateCalories();
        }
    }

    private void updateCalories() {
        long now = SystemClock.elapsedRealtime();
        long deltaMillis = now - lastTimestamp;

        double durationMin = deltaMillis / 60000.0;
        double met = metValues.getOrDefault(currentActivity, 1.0);
        double calories = (met * userWeightKg * 3.5 / 200.0) * durationMin;

        totalCalories += calories;
        lastTimestamp = now;

        if (listener != null) {
            listener.onCaloriesUpdated(totalCalories);
        }
    }

    public void pause() {
        if (!isPaused) {
            updateCalories(); // Final update before pausing
            isPaused = true;
        }
    }

    public void resume() {
        if (isPaused) {
            lastTimestamp = SystemClock.elapsedRealtime(); // Reset timestamp
            isPaused = false;
        }
    }

    public void stop() {
        if (!isPaused) {
            updateCalories();
        }
        isPaused = true;
    }

    public double getTotalCalories() {
        return totalCalories;
    }
}
