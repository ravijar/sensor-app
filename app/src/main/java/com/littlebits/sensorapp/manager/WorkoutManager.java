package com.littlebits.sensorapp.manager;

import android.app.Activity;
import android.hardware.Sensor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.model.ActivityLabel;
import com.littlebits.sensorapp.model.Workout;
import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;
import com.littlebits.sensorapp.ui.WorkoutActivity;
import com.littlebits.sensorapp.util.ActivityClassifier;
import com.littlebits.sensorapp.util.AltitudeCounter;
import com.littlebits.sensorapp.util.CalorieCounter;
import com.littlebits.sensorapp.util.DateTimeFormatter;
import com.littlebits.sensorapp.util.DistanceCounter;
import com.littlebits.sensorapp.util.SpeedCounter;
import com.littlebits.sensorapp.util.StepsCounter;
import com.littlebits.sensorapp.util.WorkoutTimer;

import java.util.ArrayList;
import java.util.List;

public class WorkoutManager implements WorkoutTimer.TimerListener, SensorObserver {

    private final Activity context;
    private final TextView timerTextView, stepCountText, distanceText, speedText, altitudeText, calorieText;
    private final ImageView pauseButton;

    private WorkoutTimer workoutTimer;
    private StepsCounter stepsCounter;
    private DistanceCounter distanceCounter;
    private SpeedCounter speedCounter;
    private AltitudeCounter altitudeCounter;
    private CalorieCounter calorieCounter;
    private ActivityClassifier classifier;

    private ActivityLabel currentActivity = ActivityLabel.SITTING;
    private long startMillis;

    private XYZFloatSensor accelerometer, gyroscope, linearAcceleration;
    private final List<Float> ax = new ArrayList<>(), ay = new ArrayList<>(), az = new ArrayList<>();
    private final List<Float> gx = new ArrayList<>(), gy = new ArrayList<>(), gz = new ArrayList<>();
    private final List<Float> lx = new ArrayList<>(), ly = new ArrayList<>(), lz = new ArrayList<>();
    private static final int TIME_STAMP = 100;

    public WorkoutManager(Activity context,
                          TextView timerText,
                          TextView stepText,
                          TextView distanceText,
                          TextView speedText,
                          TextView altitudeText,
                          TextView calorieText,
                          ImageView pauseButton) {
        this.context = context;
        this.timerTextView = timerText;
        this.stepCountText = stepText;
        this.distanceText = distanceText;
        this.speedText = speedText;
        this.altitudeText = altitudeText;
        this.calorieText = calorieText;
        this.pauseButton = pauseButton;
    }

    public void init(Bundle savedInstanceState) {
        startMillis = System.currentTimeMillis();
        workoutTimer = new WorkoutTimer(this);
        if (savedInstanceState != null) {
            workoutTimer.saveState(
                    savedInstanceState.getInt("seconds"),
                    savedInstanceState.getBoolean("running"),
                    savedInstanceState.getBoolean("wasRunning")
            );
        }
        workoutTimer.startTimer();

        stepsCounter = new StepsCounter(context);
        stepsCounter.start(count -> context.runOnUiThread(() ->
                stepCountText.setText(String.valueOf(count))
        ));

        distanceCounter = new DistanceCounter(context);
        distanceCounter.start(meters -> context.runOnUiThread(() ->
                distanceText.setText(String.format("%.2f", meters))
        ));

        speedCounter = new SpeedCounter(context);
        speedCounter.start(speed -> context.runOnUiThread(() ->
                speedText.setText(String.format("%.1f", speed))
        ));

        altitudeCounter = new AltitudeCounter(context);
        altitudeCounter.start(altitude -> context.runOnUiThread(() ->
                altitudeText.setText(String.format("%.1f", altitude))
        ));

        calorieCounter = new CalorieCounter();
        calorieCounter.start(total -> context.runOnUiThread(() ->
                calorieText.setText(String.format("%.1f", total))
        ));

        classifier = new ActivityClassifier(context);

        accelerometer = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_GYROSCOPE);
        linearAcceleration = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accelerometer.addObserver(this);
        gyroscope.addObserver(this);
        linearAcceleration.addObserver(this);
    }

    @Override
    public void onSensorChanged(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                ax.add(accelerometer.getX());
                ay.add(accelerometer.getY());
                az.add(accelerometer.getZ());
            case Sensor.TYPE_GYROSCOPE:
                gx.add(gyroscope.getX());
                gy.add(gyroscope.getY());
                gz.add(gyroscope.getZ());
            case Sensor.TYPE_LINEAR_ACCELERATION:
                lx.add(linearAcceleration.getX());
                ly.add(linearAcceleration.getY());
                lz.add(linearAcceleration.getZ());
        }

        predictActivity();
    }

    private void predictActivity() {
        if (ax.size() < TIME_STAMP || ay.size() < TIME_STAMP || az.size() < TIME_STAMP ||
                gx.size() < TIME_STAMP || gy.size() < TIME_STAMP || gz.size() < TIME_STAMP ||
                lx.size() < TIME_STAMP || ly.size() < TIME_STAMP || lz.size() < TIME_STAMP) {
            return; // Wait until all buffers are full
        }

        List<Float> data = new ArrayList<>();
        for (int i = 0; i < TIME_STAMP; i++) {
            data.add(ax.get(i)); data.add(ay.get(i)); data.add(az.get(i));
            data.add(gx.get(i)); data.add(gy.get(i)); data.add(gz.get(i));
            data.add(lx.get(i)); data.add(ly.get(i)); data.add(lz.get(i));
        }

        float[] input = new float[data.size()];
        for (int i = 0; i < data.size(); i++) input[i] = data.get(i);

        currentActivity = classifier.getTopPrediction(input);

        if (context instanceof WorkoutActivity) {
            ((WorkoutActivity) context).setActivityIcon(currentActivity.getLabel());
        }

        // Notify CalorieCounter about the updated activity
        calorieCounter.updateActivity(currentActivity);

        // Clear old data
        ax.clear(); ay.clear(); az.clear();
        gx.clear(); gy.clear(); gz.clear();
        lx.clear(); ly.clear(); lz.clear();
    }


    @Override
    public void onTimeUpdate(String formattedTime) {
        context.runOnUiThread(() -> timerTextView.setText(formattedTime));
        calorieCounter.tick();
    }

    public void togglePause() {
        if (workoutTimer.isRunning()) {
            pause();
            pauseButton.setImageResource(R.drawable.ic_play);
        } else {
            resume();
            pauseButton.setImageResource(R.drawable.ic_pause);
        }
    }

    public void pause() {
        workoutTimer.pauseTimer();
        stepsCounter.pause();
        distanceCounter.pause();
        speedCounter.pause();
        altitudeCounter.pause();
        calorieCounter.pause();
    }

    public void resume() {
        workoutTimer.resumeTimer();
        stepsCounter.resume();
        distanceCounter.resume();
        speedCounter.resume();
        altitudeCounter.resume();
        calorieCounter.resume();
    }

    public void stopAll() {
        workoutTimer.stopTimer();
        stepsCounter.stop();
        distanceCounter.stop();
        speedCounter.stop();
        altitudeCounter.stop();
        calorieCounter.stop();
    }

    public void saveState(Bundle outState) {
        outState.putInt("seconds", workoutTimer.getSeconds());
        outState.putBoolean("running", workoutTimer.isRunning());
        outState.putBoolean("wasRunning", workoutTimer.wasRunning());
    }

    public Workout getCurrentWorkout() {
        Workout workout = new Workout(
                DateTimeFormatter.getCurrentDateFormatted(),
                DateTimeFormatter.getCurrentMonthYearFormatted(),
                DateTimeFormatter.getWorkoutDuration(startMillis, System.currentTimeMillis())
        );

        workout.setTime(workoutTimer.getSeconds());
        workout.setSteps(stepsCounter.getCurrentStepCount());
        workout.setDistance(distanceCounter.getCurrentDistance());
        workout.setSpeed(speedCounter.getAverageSpeed());
        workout.setAltitude(altitudeCounter.getFusedAltitude());
        workout.setCalories(calorieCounter.getTotalCalories());

        return workout;
    }

}
