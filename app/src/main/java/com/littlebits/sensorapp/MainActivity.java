package com.littlebits.sensorapp;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.littlebits.sensorapp.helper.SensorManagerHelper;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private SensorManagerHelper sensorHelper;
    private Map<String, TextView> sensorTextViews = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews and store them in a map
        sensorTextViews.put("activity", findViewById(R.id.activityText));
        sensorTextViews.put("accelX", findViewById(R.id.accelXText));
        sensorTextViews.put("accelY", findViewById(R.id.accelYText));
        sensorTextViews.put("accelZ", findViewById(R.id.accelZText));
        sensorTextViews.put("gyroX", findViewById(R.id.gyroXText));
        sensorTextViews.put("gyroY", findViewById(R.id.gyroYText));
        sensorTextViews.put("gyroZ", findViewById(R.id.gyroZText));
        sensorTextViews.put("light", findViewById(R.id.lightText));

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Initialize sensor manager helper and pass text fields dynamically
        sensorHelper = new SensorManagerHelper(sensorManager, sensorTextViews, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorHelper.registerSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorHelper.unregisterSensors();
    }

    public void openAvailableSensors(View view) {
        Intent intent = new Intent(this, AvailableSensorsActivity.class);
        startActivity(intent);
    }
}
