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
import androidx.core.content.ContextCompat;

import android.content.res.ColorStateList;

import com.littlebits.sensorapp.R;
import com.littlebits.sensorapp.util.PersonalDetailsManager;

public class PersonalDetailsActivity extends AppCompatActivity {

    private EditText editName, editAge, editHeight, editWeight, editSosContact;
    private Spinner genderSpinner;
    private TextView bmiStatusText;
    private ImageButton editNameButton, editAgeButton, editHeightButton, editWeightButton, editSosButton;
    private Button saveButton;
    private View bmiStatusIndicator;

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
        bmiStatusIndicator = findViewById(R.id.bmiStatusIndicator);

        editNameButton = findViewById(R.id.editNameButton);
        editAgeButton = findViewById(R.id.editAgeButton);
        editHeightButton = findViewById(R.id.editHeightButton);
        editWeightButton = findViewById(R.id.editWeightButton);
        editSosButton = findViewById(R.id.editSosButton);
        saveButton = findViewById(R.id.saveButton);
        saveButton.setVisibility(View.GONE);

        detailsManager = new PersonalDetailsManager(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        loadPersonalDetails();

        // Calculate and display BMI on load if height and weight are available
        String loadedHeight = detailsManager.getSavedData("height");
        String loadedWeight = detailsManager.getSavedData("weight");
        if (!loadedHeight.isEmpty() && !loadedWeight.isEmpty()) {
            String bmiCategory = detailsManager.calculateAndSaveBMI(loadedHeight, loadedWeight);
            bmiStatusText.setText("BMI Category: " + bmiCategory);
            updateBmiStatusIndicatorColor(bmiCategory);
        }

        setEditButtonListeners();
        setFieldFocusListeners();
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
    }

    private void showSaveButton() {
        saveButton.setVisibility(isUpdated ? View.VISIBLE : View.GONE);
    }

    public void onBackClicked(View view) {
        finish();
    }

    public void onSaveClicked(View view) {
        hideKeyboardAndClearFocus();
        if (!validateFields()) {
            return;
        }
        saveAndUpdatePersonalDetails();
        isUpdated = false;
        saveButton.setVisibility(View.GONE);
        // genderSpinner.setEnabled(false);
    }

    private boolean validateFields() {
        String name = editName.getText().toString().trim();
        String ageStr = editAge.getText().toString().trim();
        String heightStr = editHeight.getText().toString().trim();
        String weightStr = editWeight.getText().toString().trim();
        String sos = editSosContact.getText().toString().trim();

        if (!name.isEmpty() && !name.matches("[a-zA-Z ]+")) {
            Toast.makeText(this, "Please enter a valid name (letters and spaces only)", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!ageStr.isEmpty()) {
            int age;
            try {
                age = Integer.parseInt(ageStr);
                if (age < 0 || age > 150) {
                    Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!heightStr.isEmpty()) {
            float height;
            try {
                height = Float.parseFloat(heightStr);
                if (height <= 0) {
                    Toast.makeText(this, "Height must be a positive value", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!weightStr.isEmpty()) {
            float weight;
            try {
                weight = Float.parseFloat(weightStr);
                if (weight <= 0) {
                    Toast.makeText(this, "Weight must be a positive value", Toast.LENGTH_SHORT).show();
                    return false;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        if (!sos.isEmpty() && !sos.matches("\\d{10}")) {
            Toast.makeText(this, "SOS contact must be a 10-digit mobile number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
            updateBmiStatusIndicatorColor(bmiCategory);
            Toast.makeText(this, "Personal details saved!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBmiStatusIndicatorColor(String bmiCategory) {
        int colorResourceId;
        switch (bmiCategory) {
            case "Underweight":
                colorResourceId = R.color.bmi_underweight;
                break;
            case "Normal weight":
                colorResourceId = R.color.bmi_normal;
                break;
            case "Overweight":
                colorResourceId = R.color.bmi_overweight;
                break;
            case "Obesity":
                colorResourceId = R.color.bmi_obesity;
                break;
            default:
                colorResourceId = android.R.color.transparent; // Default or error color
                break;
        }
        int color = ContextCompat.getColor(this, colorResourceId);
        bmiStatusIndicator.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    private void setFieldFocusListeners() {
        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            if (!hasFocus) {
                isUpdated = true;
                showSaveButton();
            }
        };

        editName.setOnFocusChangeListener(focusChangeListener);
        editAge.setOnFocusChangeListener(focusChangeListener);
        editHeight.setOnFocusChangeListener(focusChangeListener);
        editWeight.setOnFocusChangeListener(focusChangeListener);
        editSosContact.setOnFocusChangeListener(focusChangeListener);
    }

    private void hideKeyboardAndClearFocus() {
        View[] fields = {editName, editAge, editHeight, editWeight, editSosContact};
        for (View field : fields) {
            field.clearFocus();
        }
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
