package com.littlebits.sensorapp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.littlebits.sensorapp.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
