package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.PersonalDetailsManager;

public class PersonalDetailsActivity extends AppCompatActivity {

    // UI components
    private EditText editName, editAge, editGender, editHeight, editWeight, editSosContact;
    private TextView bmiStatusText;
    private ImageButton editNameButton, editAgeButton, editGenderButton, editHeightButton, editWeightButton, editSosButton;
    private Button saveButton;  // Save button at the bottom

    private PersonalDetailsManager detailsManager;

    // Variable to track changes
    private boolean isUpdated = false;

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

        // Initialize Save button
        saveButton = findViewById(R.id.saveButton);

        // Initialize PersonalDetailsManager
        detailsManager = new PersonalDetailsManager(this);

        // Set listeners for buttons to allow editing
        setEditButtonListeners();

        // Load saved data into fields
        loadPersonalDetails();

        // Set listener for save button
        saveButton.setOnClickListener(v -> {
            saveAndUpdatePersonalDetails();
            isUpdated = false;  // Reset updated flag
            saveButton.setVisibility(View.GONE);  // Hide the save button after saving
        });
    }

    private void setEditButtonListeners() {
        editNameButton.setOnClickListener(v -> {
            enableEditing(editName);
            isUpdated = true;  // Mark as updated
            showSaveButton();  // Show the save button when a field is edited
        });
        editAgeButton.setOnClickListener(v -> {
            enableEditing(editAge);
            isUpdated = true;
            showSaveButton();
        });
        editGenderButton.setOnClickListener(v -> {
            enableEditing(editGender);
            isUpdated = true;
            showSaveButton();
        });
        editHeightButton.setOnClickListener(v -> {
            enableEditing(editHeight);
            isUpdated = true;
            showSaveButton();
        });
        editWeightButton.setOnClickListener(v -> {
            enableEditing(editWeight);
            isUpdated = true;
            showSaveButton();
        });
        editSosButton.setOnClickListener(v -> {
            enableEditing(editSosContact);
            isUpdated = true;
            showSaveButton();
        });
    }

    // Method to enable editing for the fields and move the cursor to the end of the text
    private void enableEditing(EditText editText) {
        editText.setEnabled(true);  // Enable the EditText field for editing
        editText.requestFocus();    // Set the focus on the EditText
        editText.setSelection(editText.getText().length());  // Move the cursor to the end of the existing text

        // Show the soft keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);  // Show the keyboard
    }

    // Method to load saved personal details into the EditText fields
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

    // Show the save button when any field is updated
    private void showSaveButton() {
        if (isUpdated) {
            saveButton.setVisibility(View.VISIBLE);  // Show the Save button
        } else {
            saveButton.setVisibility(View.GONE);  // Hide the Save button if no changes
        }
    }

    public void onBackClicked(View view) {
        saveAndUpdatePersonalDetails();
        finish();
    }

    private void saveAndUpdatePersonalDetails() {
        if (isUpdated) {
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
}
