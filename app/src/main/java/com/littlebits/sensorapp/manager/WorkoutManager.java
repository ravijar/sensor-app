package com.littlebits.sensorapp.manager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.AltitudeCounter;
import com.littlebits.sensorapp.util.DistanceCounter;
import com.littlebits.sensorapp.util.SpeedCounter;
import com.littlebits.sensorapp.util.StepsCounter;
import com.littlebits.sensorapp.util.WorkoutTimer;

public class WorkoutManager implements WorkoutTimer.TimerListener {

    private final Activity context;
    private final TextView timerTextView, stepCountText, distanceText, speedText, altitudeText;
    private final ImageView pauseButton;

    private WorkoutTimer workoutTimer;
    private StepsCounter stepsCounter;
    private DistanceCounter distanceCounter;
    private SpeedCounter speedCounter;
    private AltitudeCounter altitudeCounter;

    public WorkoutManager(Activity context,
                          TextView timerText,
                          TextView stepText,
                          TextView distanceText,
                          TextView speedText,
                          TextView altitudeText,
                          ImageView pauseButton) {
        this.context = context;
        this.timerTextView = timerText;
        this.stepCountText = stepText;
        this.distanceText = distanceText;
        this.speedText = speedText;
        this.altitudeText = altitudeText;
        this.pauseButton = pauseButton;
    }

    public void init(Bundle savedInstanceState) {
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
    }

    public void resume() {
        workoutTimer.resumeTimer();
        stepsCounter.resume();
        distanceCounter.resume();
        speedCounter.resume();
        altitudeCounter.resume();
    }

    public void stopAll() {
        workoutTimer.stopTimer();
        stepsCounter.stop();
        distanceCounter.stop();
        speedCounter.stop();
        altitudeCounter.stop();
    }

    public void saveState(Bundle outState) {
        outState.putInt("seconds", workoutTimer.getSeconds());
        outState.putBoolean("running", workoutTimer.isRunning());
        outState.putBoolean("wasRunning", workoutTimer.wasRunning());
    }

    @Override
    public void onTimeUpdate(String formattedTime) {
        context.runOnUiThread(() -> timerTextView.setText(formattedTime));
    }
}
