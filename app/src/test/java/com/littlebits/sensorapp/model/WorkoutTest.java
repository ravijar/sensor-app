package com.littlebits.sensorapp.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class WorkoutTest {

    @Test
    public void testConstructorAndGetters() {
        String date = "2023-10-27";
        String monthYear = "October 2023";
        String duration = "00:30:00";

        Workout workout = new Workout(date, monthYear, duration);

        assertEquals(date, workout.getDate());
        assertEquals(monthYear, workout.getMonthYear());
        assertEquals(duration, workout.getDuration());
        assertEquals(0, workout.getTime()); // Default int value
        assertEquals(0, workout.getSteps()); // Default int value
        assertEquals(0.0, workout.getDistance(), 0.0); // Default double value
        assertEquals(0.0, workout.getSpeed(), 0.0); // Default double value
        assertEquals(0.0, workout.getAltitude(), 0.0); // Default double value
        assertEquals(0.0, workout.getCalories(), 0.0); // Default double value
    }

    @Test
    public void testSettersAndGetters() {
        Workout workout = new Workout("", "", ""); // Initial dummy values

        int time = 1800;
        int steps = 5000;
        double distance = 3.5;
        double speed = 6.2;
        double altitude = 100.0;
        double calories = 300.5;

        workout.setTime(time);
        workout.setSteps(steps);
        workout.setDistance(distance);
        workout.setSpeed(speed);
        workout.setAltitude(altitude);
        workout.setCalories(calories);

        assertEquals(time, workout.getTime());
        assertEquals(steps, workout.getSteps());
        assertEquals(distance, workout.getDistance(), 0.0);
        assertEquals(speed, workout.getSpeed(), 0.0);
        assertEquals(altitude, workout.getAltitude(), 0.0);
        assertEquals(calories, workout.getCalories(), 0.0);
    }

    @Test
    public void testSettersWithNewValues() {
        Workout workout = new Workout("Old Date", "Old Month", "Old Duration");

        String newDate = "2024-01-01";
        String newMonthYear = "January 2024";
        String newDuration = "01:00:00";

        workout.setDate(newDate);
        workout.setMonthYear(newMonthYear);
        workout.setDuration(newDuration);

        assertEquals(newDate, workout.getDate());
        assertEquals(newMonthYear, workout.getMonthYear());
        assertEquals(newDuration, workout.getDuration());
    }
} 