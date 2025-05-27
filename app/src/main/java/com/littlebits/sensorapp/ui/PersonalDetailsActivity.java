package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;

public class PersonalDetailsActivity extends AppCompatActivity {

    // UI components
    private EditText editName, editAge, editGender, editHeight, editWeight, editSosContact;
    private View bmiStatusIndicator;

    private ImageButton editNameButton, editAgeButton, editGenderButton, editHeightButton, editWeightButton, editSosButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        // Hide default action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Initialize fields
        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        editGender = findViewById(R.id.editGender);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        editSosContact = findViewById(R.id.sosContact);
        bmiStatusIndicator = findViewById(R.id.bmiStatusIndicator);

        // Initialize edit buttons
        editNameButton = findViewById(R.id.editNameButton);
        editAgeButton = findViewById(R.id.editAgeButton);
        editGenderButton = findViewById(R.id.editGenderButton);
        editHeightButton = findViewById(R.id.editHeightButton);
        editWeightButton = findViewById(R.id.editWeightButton);
        editSosButton = findViewById(R.id.editSosButton);

        // Set listeners for buttons to allow editing
        setEditButtonListeners();
    }

    // Method to enable editing for the fields
    private void setEditButtonListeners() {
        editNameButton.setOnClickListener(v -> enableEditing(editName));
        editAgeButton.setOnClickListener(v -> enableEditing(editAge));
        editGenderButton.setOnClickListener(v -> enableEditing(editGender));
        editHeightButton.setOnClickListener(v -> enableEditing(editHeight));
        editWeightButton.setOnClickListener(v -> enableEditing(editWeight));
        editSosButton.setOnClickListener(v -> enableEditing(editSosContact));
    }

    private void enableEditing(EditText editText) {
        editText.setEnabled(true);
    }

    public void onBackClicked(View view) {
        finish();
    }
}
