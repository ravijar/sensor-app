package com.littlebits.sensorapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.PersonalDetailsManager;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText editName, editAge, editHeight, editWeight, editSosContact;
    private Spinner genderSpinner;
    private TextView bmiStatusText;
    private ImageButton editNameButton, editAgeButton, editHeightButton, editWeightButton, editSosButton;
    private Button saveButton;

    private PersonalDetailsManager detailsManager;
    private boolean isUpdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editName = findViewById(R.id.editName);
        editAge = findViewById(R.id.editAge);
        genderSpinner = findViewById(R.id.editGenderSpinner);
        editHeight = findViewById(R.id.editHeight);
        editWeight = findViewById(R.id.editWeight);
        editSosContact = findViewById(R.id.sosContact);
        bmiStatusText = findViewById(R.id.bmiLabel);

        editNameButton = findViewById(R.id.editNameButton);
        editAgeButton = findViewById(R.id.editAgeButton);
        editHeightButton = findViewById(R.id.editHeightButton);
        editWeightButton = findViewById(R.id.editWeightButton);
        editSosButton = findViewById(R.id.editSosButton);
        saveButton = findViewById(R.id.saveButton);

        detailsManager = new PersonalDetailsManager(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        loadPersonalDetails();
        setEditButtonListeners();
    }

    private void setEditButtonListeners() {
        editNameButton.setOnClickListener(v -> {
            enableEditing(editName);
            isUpdated = true;
            showSaveButton();
        });

        editAgeButton.setOnClickListener(v -> {
            enableEditing(editAge);
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

    private void enableEditing(EditText editText) {
        editText.setEnabled(true);
        editText.requestFocus();
        editText.setSelection(editText.getText().length());
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    private void loadPersonalDetails() {
        editName.setText(detailsManager.getSavedData("name"));
        editAge.setText(detailsManager.getSavedData("age"));
        editHeight.setText(detailsManager.getSavedData("height"));
        editWeight.setText(detailsManager.getSavedData("weight"));
        editSosContact.setText(detailsManager.getSavedData("sos_contact"));

        String savedGender = detailsManager.getSavedData("gender");
        if (savedGender != null) {
            ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) genderSpinner.getAdapter();
            int position = adapter.getPosition(savedGender);
            genderSpinner.setSelection(position);
        }

        String bmiCategory = detailsManager.getSavedData("bmi");
        if (!bmiCategory.isEmpty()) {
            bmiStatusText.setText("BMI: " + bmiCategory);
        }
    }

    private void showSaveButton() {
        saveButton.setVisibility(isUpdated ? View.VISIBLE : View.GONE);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onSaveClicked(View view) {
        saveAndUpdatePersonalDetails();
        isUpdated = false;
        saveButton.setVisibility(View.GONE);
        genderSpinner.setEnabled(false);
    }

    private void saveAndUpdatePersonalDetails() {
        if (isUpdated) {
            detailsManager.savePersonalDetails(
                    editName.getText().toString(),
                    editAge.getText().toString(),
                    genderSpinner.getSelectedItem().toString(),
                    editHeight.getText().toString(),
                    editWeight.getText().toString(),
                    editSosContact.getText().toString()
            );

            String bmiCategory = detailsManager.calculateAndSaveBMI(
                    editHeight.getText().toString(),
                    editWeight.getText().toString()
            );

            bmiStatusText.setText("BMI Category: " + bmiCategory);
            Toast.makeText(this, "Personal details saved!", Toast.LENGTH_SHORT).show();
        }
    }
}
