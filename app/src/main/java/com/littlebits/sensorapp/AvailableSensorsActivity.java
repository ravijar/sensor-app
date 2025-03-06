package com.littlebits.sensorapp;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.repository.SensorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AvailableSensorsActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private ListView sensorListView;
    private List<BaseSensor> sensorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Available Sensors");
        }

        sensorListView = findViewById(R.id.sensorList);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Initialize sensors in the repository (only happens once)
        SensorRepository.getInstance().initializeSensors(sensorManager);

        // Get the sensor instances from the repository
        Map<Integer, BaseSensor> sensorMap = SensorRepository.getInstance().getAllSensors();
        sensorList = new ArrayList<>(sensorMap.values());

        // Custom Adapter to display sensor names
        ArrayAdapter<BaseSensor> adapter = new ArrayAdapter<BaseSensor>(this, R.layout.item_sensor, sensorList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_sensor, parent, false);
                }

                BaseSensor sensor = getItem(position);

                // Get UI elements
                TextView sensorName = convertView.findViewById(R.id.sensorName);
                TextView sensorType = convertView.findViewById(R.id.sensorType);
                TextView sensorVendor = convertView.findViewById(R.id.sensorVendor);

                // Set sensor values
                sensorName.setText(sensor.getName());
                sensorType.setText(sensor.getStringType());
                sensorVendor.setText(sensor.getVendor());

                return convertView;
            }
        };

        sensorListView.setAdapter(adapter);

        // Handle sensor item click - Open SensorDetailsActivity
        sensorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BaseSensor selectedSensor = sensorList.get(position);

                SensorRepository.getInstance().setCurrentSensor(selectedSensor);

                Intent intent = new Intent(AvailableSensorsActivity.this, SensorDetailsActivity.class);
                startActivity(intent);
            }
        });
    }
}
