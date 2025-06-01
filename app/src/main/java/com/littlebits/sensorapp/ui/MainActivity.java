package com.littlebits.sensorapp.ui;

import android.content.Intent;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.SensorObserver;
import com.littlebits.sensorapp.sensor.XYZFloatSensor;
import com.littlebits.sensorapp.util.ActivityClassifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorObserver {
    private static final int TIME_STAMP = 100;

    private ActivityClassifier classifier;
    private XYZFloatSensor accelerometer, gyroscope, linearAcceleration;
    private List<Float> ax,ay,az;
    private List<Float> gx,gy,gz;
    private List<Float> lx,ly,lz;
    private float[] results;

    private TextView bikingTextView, downstairsTextView, joggingTextView, sittingTextView, standingTextView, upstairsTextView, walkingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bikingTextView = findViewById(R.id.biking_TextView);
        downstairsTextView = findViewById(R.id.downstairs_TextView);
        joggingTextView = findViewById(R.id.jogging_TextView);
        sittingTextView = findViewById(R.id.sitting_TextView);
        standingTextView = findViewById(R.id.standing_TextView);
        upstairsTextView = findViewById(R.id.upstairs_TextView);
        walkingTextView = findViewById(R.id.walking_TextView);

        ax=new ArrayList<>(); ay=new ArrayList<>(); az=new ArrayList<>();
        gx=new ArrayList<>(); gy=new ArrayList<>(); gz=new ArrayList<>();
        lx=new ArrayList<>(); ly=new ArrayList<>(); lz=new ArrayList<>();

        classifier = new ActivityClassifier(getApplicationContext());

        accelerometer = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_GYROSCOPE);
        linearAcceleration = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accelerometer.addObserver(this);
        gyroscope.addObserver(this);
        linearAcceleration.addObserver(this);
    }

    private void predictActivity() {
        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP &&
                gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP &&
                lx.size() >= TIME_STAMP && ly.size() >= TIME_STAMP && lz.size() >= TIME_STAMP) {

            List<Float> data = new ArrayList<>();
            for (int i = 0; i < TIME_STAMP; i++) {
                data.add(ax.get(i)); data.add(ay.get(i)); data.add(az.get(i));
                data.add(gx.get(i)); data.add(gy.get(i)); data.add(gz.get(i));
                data.add(lx.get(i)); data.add(ly.get(i)); data.add(lz.get(i));
            }

            float[] input = toFloatArray(data);
            results = classifier.predictProbabilities(input);

            // Mapping from index to activity
            bikingTextView.setText("Biking: " + round(results[0], 2));
            downstairsTextView.setText("Downstairs: " + round(results[1], 2));
            joggingTextView.setText("Jogging: " + round(results[2], 2));
            sittingTextView.setText("Sitting: " + round(results[3], 2));
            upstairsTextView.setText("Upstairs: " + round(results[4], 2));
            standingTextView.setText("Standing: " + round(results[5], 2));
            walkingTextView.setText("Walking: " + round(results[6], 2));

            ax.clear(); ay.clear(); az.clear();
            gx.clear(); gy.clear(); gz.clear();
            lx.clear(); ly.clear(); lz.clear();
        }
    }

    private float round(float value, int decimalPlaces) {
        BigDecimal bigDecimal = new BigDecimal(Float.toString(value));
        bigDecimal = bigDecimal.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.floatValue();
    }

    private float[] toFloatArray(List<Float> data) {
        float[] array = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            array[i] = (data.get(i) != null) ? data.get(i) : Float.NaN;
        }
        return array;
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
        linearAcceleration.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
        linearAcceleration.unregister();
    }

    public void openAvailableSensors(View view) {
        startActivity(new Intent(this, AvailableSensorsActivity.class));
    }

    public void openHome(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }

    @Override
    public void onSensorChanged(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                ax.add(accelerometer.getX());
                ay.add(accelerometer.getY());
                az.add(accelerometer.getZ());
                break;
            case Sensor.TYPE_GYROSCOPE:
                gx.add(gyroscope.getX());
                gy.add(gyroscope.getY());
                gz.add(gyroscope.getZ());
                break;
            case Sensor.TYPE_LINEAR_ACCELERATION:
                lx.add(linearAcceleration.getX());
                ly.add(linearAcceleration.getY());
                lz.add(linearAcceleration.getZ());
                break;
        }

        predictActivity();
    }
}
