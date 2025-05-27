package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.PersonalDetailsManager;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText editName, editAge, editGender, editHeight, editWeight, editSosContact;
    private TextView bmiStatusText;
    private ImageButton editNameButton, editAgeButton, editGenderButton, editHeightButton, editWeightButton, editSosButton;

    private PersonalDetailsManager detailsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

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
        bmiStatusText = findViewById(R.id.bmiLabel);

        // Initialize edit buttons
        editNameButton = findViewById(R.id.editNameButton);
        editAgeButton = findViewById(R.id.editAgeButton);
        editGenderButton = findViewById(R.id.editGenderButton);
        editHeightButton = findViewById(R.id.editHeightButton);
        editWeightButton = findViewById(R.id.editWeightButton);
        editSosButton = findViewById(R.id.editSosButton);

        // Initialize PersonalDetailsManager
        detailsManager = new PersonalDetailsManager(this);

        // Set listeners for buttons to allow editing
        setEditButtonListeners();

        // Load saved data into fields
        loadPersonalDetails();
    }

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

    private void loadPersonalDetails() {
        editName.setText(detailsManager.getSavedData("name"));
        editAge.setText(detailsManager.getSavedData("age"));
        editGender.setText(detailsManager.getSavedData("gender"));
        editHeight.setText(detailsManager.getSavedData("height"));
        editWeight.setText(detailsManager.getSavedData("weight"));
        editSosContact.setText(detailsManager.getSavedData("sos_contact"));

        String bmiCategory = detailsManager.getSavedData("bmi");
        if (!bmiCategory.isEmpty()) {
            bmiStatusText.setText("BMI: " + bmiCategory);
        }
    }

    public void onBackClicked(View view) {
        saveAndUpdatePersonalDetails();
        finish();
    }

    private void saveAndUpdatePersonalDetails() {
        // Save the values in the fields
        detailsManager.savePersonalDetails(
                editName.getText().toString(),
                editAge.getText().toString(),
                editGender.getText().toString(),
                editHeight.getText().toString(),
                editWeight.getText().toString(),
                editSosContact.getText().toString()
        );

        // Calculate and update BMI
        String bmiCategory = detailsManager.calculateAndSaveBMI(editHeight.getText().toString(), editWeight.getText().toString());

        // Display BMI category
        bmiStatusText.setText("BMI Category: " + bmiCategory);

        // Show a confirmation toast
        Toast.makeText(this, "Personal details saved!", Toast.LENGTH_SHORT).show();
    }
}
