package com.littlebits.sensorapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openAvailableSensors(View view) {
        startActivity(new Intent(this, AvailableSensorsActivity.class));
    }

    public void openHome(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }
}
