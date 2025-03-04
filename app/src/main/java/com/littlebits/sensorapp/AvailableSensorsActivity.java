package com.littlebits.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.littlebits.sensorapp.helper.SensorTypeHelper;

import java.util.ArrayList;
import java.util.List;

public class AvailableSensorsActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private ListView sensorListView;
    private List<Sensor> sensorList;
    private List<Sensor> knownSensors;
    private List<Sensor> unknownSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Sensors");
        }

        sensorListView = findViewById(R.id.sensorList);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Separate known and unknown sensors
        knownSensors = new ArrayList<>();
        unknownSensors = new ArrayList<>();

        for (Sensor sensor : allSensors) {
            String sensorTypeName = SensorTypeHelper.getSensorTypeName(sensor.getType());
            if (sensorTypeName.equals("Unknown")) {
                unknownSensors.add(sensor);
            } else {
                knownSensors.add(sensor);
            }
        }

        // Merge the lists: known sensors first, unknown sensors at the end
        sensorList = new ArrayList<>(knownSensors);
        sensorList.addAll(unknownSensors);

        // Custom Adapter
        ArrayAdapter<Sensor> adapter = new ArrayAdapter<Sensor>(this, R.layout.item_sensor, sensorList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sensor, parent, false);
                }

                Sensor sensor = getItem(position);

                // Get UI elements
                TextView sensorName = convertView.findViewById(R.id.sensorName);
                TextView sensorType = convertView.findViewById(R.id.sensorType);
                TextView sensorCategory = convertView.findViewById(R.id.sensorCategory);

                // Set sensor values
                sensorName.setText(sensor.getName());
                sensorType.setText(SensorTypeHelper.getSensorTypeName(sensor.getType()));
                sensorCategory.setText(SensorTypeHelper.getSensorCategory(sensor.getType()));

                // Check if sensor is unknown
                if (unknownSensors.contains(sensor)) {
                    convertView.setEnabled(false);  // Disable clicking
                    convertView.setAlpha(0.5f);  // Reduce opacity to indicate it's disabled
                } else {
                    convertView.setEnabled(true);
                    convertView.setAlpha(1.0f);
                }

                return convertView;
            }
        };

        sensorListView.setAdapter(adapter);

        // Handle sensor item click - Open SensorDetailsActivity
        sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sensor selectedSensor = sensorList.get(position);

                // Only allow clicks for known sensors
                if (!unknownSensors.contains(selectedSensor)) {
                    Intent intent = new Intent(AvailableSensorsActivity.this, SensorDetailsActivity.class);
                    intent.putExtra("sensorType", selectedSensor.getType());
                    startActivity(intent);
                }
            }
        });
    }
}
