package com.littlebits.sensorapp.activity;

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
import com.littlebits.sensorapp.service.ActivityClassifier;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorObserver {
    private final int TIME_STAMP = 100;

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
        sittingTextView  = findViewById(R.id.sitting_TextView);
        standingTextView = findViewById(R.id.standing_TextView);
        upstairsTextView = findViewById(R.id.upstairs_TextView);
        walkingTextView = findViewById(R.id.walking_TextView);

        ax=new ArrayList<>(); ay=new ArrayList<>(); az=new ArrayList<>();
        gx=new ArrayList<>(); gy=new ArrayList<>(); gz=new ArrayList<>();
        lx=new ArrayList<>(); ly=new ArrayList<>(); lz=new ArrayList<>();

        classifier=new ActivityClassifier(getApplicationContext());

        accelerometer = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_GYROSCOPE);
        linearAcceleration = (XYZFloatSensor) SensorRepository.getInstance().getSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        accelerometer.addObserver(this);
        gyroscope.addObserver(this);
        linearAcceleration.addObserver(this);
    }

    private void predictActivity() {
        List<Float> data=new ArrayList<>();
        if (ax.size() >= TIME_STAMP && ay.size() >= TIME_STAMP && az.size() >= TIME_STAMP
                && gx.size() >= TIME_STAMP && gy.size() >= TIME_STAMP && gz.size() >= TIME_STAMP
                && lx.size() >= TIME_STAMP && ly.size() >= TIME_STAMP && lz.size() >= TIME_STAMP) {
            data.addAll(ax.subList(0,TIME_STAMP));
            data.addAll(ay.subList(0,TIME_STAMP));
            data.addAll(az.subList(0,TIME_STAMP));

            data.addAll(gx.subList(0,TIME_STAMP));
            data.addAll(gy.subList(0,TIME_STAMP));
            data.addAll(gz.subList(0,TIME_STAMP));

            data.addAll(lx.subList(0,TIME_STAMP));
            data.addAll(ly.subList(0,TIME_STAMP));
            data.addAll(lz.subList(0,TIME_STAMP));

            results = classifier.predictProbabilities(toFloatArray(data));

            bikingTextView.setText("Biking: \t" + round(results[0],2));
            downstairsTextView.setText("DownStairs: \t" + round(results[1],2));
            joggingTextView.setText("Jogging: \t" + round(results[2],2));
            sittingTextView.setText("Sitting: \t" + round(results[3],2));
            standingTextView.setText("Standing: \t" + round(results[4],2));
            upstairsTextView.setText("Upstairs: \t" + round(results[5],2));;
            walkingTextView.setText("Walking: \t" + round(results[6],2));


            data.clear();
            ax.clear(); ay.clear(); az.clear();
            gx.clear(); gy.clear(); gz.clear();
            lx.clear();ly.clear(); lz.clear();
        }
    }

    private float round(float value, int decimal_places) {
        BigDecimal bigDecimal=new BigDecimal(Float.toString(value));
        bigDecimal = bigDecimal.setScale(decimal_places, BigDecimal.ROUND_HALF_UP);
        return bigDecimal.floatValue();
    }

    private float[] toFloatArray(List<Float> data) {
        int i=0;
        float[] array=new float[data.size()];
        for (Float f:data) {
            array[i++] = (f != null ? f: Float.NaN);
        }
        return array;
    }

    @Override
    protected void onResume() {
        super.onResume();
        accelerometer.register();
        gyroscope.register();
    }

    @Override
    protected void onPause() {
        super.onPause();
        accelerometer.unregister();
        gyroscope.unregister();
    }

    public void openAvailableSensors(View view) {
        Intent intent = new Intent(this, AvailableSensorsActivity.class);
        startActivity(intent);
    }

    public void openHome(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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
}
