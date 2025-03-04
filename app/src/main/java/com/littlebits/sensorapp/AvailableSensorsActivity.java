package com.littlebits.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.littlebits.sensorapp.helper.SensorTypeHelper;

import java.util.List;

public class AvailableSensorsActivity extends AppCompatActivity {
    private SensorManager sensorManager;
    private ListView sensorListView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        sensorListView = findViewById(R.id.sensorList);
        backButton = findViewById(R.id.backButton);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);

        // Custom Adapter inside the Activity
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

                return convertView;
            }
        };

        sensorListView.setAdapter(adapter);

        // Back button functionality
        backButton.setOnClickListener(v -> finish());
    }
}
