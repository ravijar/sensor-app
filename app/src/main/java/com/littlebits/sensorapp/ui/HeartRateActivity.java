package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class HeartRateActivity extends AppCompatActivity {

    private TextView heartRateValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart_rate);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        heartRateValue = findViewById(R.id.heartRateValue);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onDoneClicked(View view) {
        finish();
    }
}
