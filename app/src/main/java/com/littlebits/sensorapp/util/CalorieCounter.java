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

    // Predefined user data
    private final double userWeightKg = 70.0;
    private final double userHeightCm = 175.0;
    private final int userAgeYears = 30;
    private final boolean userIsMale = true;

    private final double currentBMR; // Basal Metabolic Rate kcal/day

    public CalorieCounter() {
        initializeMETValues();
        currentBMR = calculateBMR();
    }

    private void initializeMETValues() {
        metValues.put(ActivityLabel.WALKING, 3.5);
        metValues.put(ActivityLabel.JOGGING, 7.0);
        metValues.put(ActivityLabel.BIKING, 6.0);
        metValues.put(ActivityLabel.SITTING, 1.5);
        metValues.put(ActivityLabel.STANDING, 2.0);
        metValues.put(ActivityLabel.UPSTAIRS, 5.0);
        metValues.put(ActivityLabel.DOWNSTAIRS, 4.0);
    }

    private double calculateBMR() {
        // Mifflin-St Jeor Equation
        if (userIsMale) {
            return 10 * userWeightKg + 6.25 * userHeightCm - 5 * userAgeYears + 5;
        } else {
            return 10 * userWeightKg + 6.25 * userHeightCm - 5 * userAgeYears - 161;
        }
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

        double caloriesPerMinute = (currentBMR / 1440.0) * met;
        double caloriesBurned = caloriesPerMinute * durationMin;

        totalCalories += caloriesBurned;
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
