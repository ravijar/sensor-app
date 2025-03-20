package com.littlebits.sensorapp.util;

import android.os.Handler;

public class WorkoutTimer {
    private int seconds = 0;
    private boolean running = true;
    private boolean wasRunning;
    private final Handler handler = new Handler();
    private TimerListener listener;

    public interface TimerListener {
        void onTimeUpdate(String formattedTime);
    }

    public WorkoutTimer(TimerListener listener) {
        this.listener = listener;
    }

    public void startTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time = String.format("%02d : %02d : %02d", hours, minutes, secs);

                if (listener != null) {
                    listener.onTimeUpdate(time);
                }

                if (running) {
                    seconds++;
                }

                handler.postDelayed(this, 1000);
            }
        });
    }

    public void pauseTimer() {
        running = false;
    }

    public void resumeTimer() {
        running = true;
    }

    public void resetTimer() {
        running = false;
        seconds = 0;
        if (listener != null) {
            listener.onTimeUpdate("00 : 00 : 00");
        }
    }

    public void stopTimer() {
        handler.removeCallbacksAndMessages(null);
    }

    public void saveState(int seconds, boolean running, boolean wasRunning) {
        this.seconds = seconds;
        this.running = running;
        this.wasRunning = wasRunning;
    }

    public int getSeconds() {
        return seconds;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean wasRunning() {
        return wasRunning;
    }

    public void setWasRunning(boolean wasRunning) {
        this.wasRunning = wasRunning;
    }
}
