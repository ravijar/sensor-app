package com.littlebits.sensorapp.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonalDetailsManager {

    private static final String PREFS_NAME = "PersonalDetailsPrefs";
    private SharedPreferences sharedPreferences;

    public PersonalDetailsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // Save personal details to SharedPreferences
    public void savePersonalDetails(String name, String age, String gender, String height, String weight, String sosContact) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("age", age);
        editor.putString("gender", gender);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("sos_contact", sosContact);
        editor.apply();
    }

    // Retrieve saved personal detail from SharedPreferences
    public String getSavedData(String key) {
        return sharedPreferences.getString(key, ""); // Default empty string if not found
    }

    // Update a single field in SharedPreferences
    public void updateField(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    // BMI calculation logic
    public float calculateBMI(String heightStr, String weightStr) {
        try {
            // Convert height to meters and weight to kg
            float height = Float.parseFloat(heightStr) / 100;  // Convert cm to meters
            float weight = Float.parseFloat(weightStr);

            // BMI formula: Weight / (Height^2)
            return weight / (height * height);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0f;  // If parsing fails, return 0.0 BMI
        }
    }

    // Calculate and save BMI
    public String calculateAndSaveBMI(String heightStr, String weightStr) {
        float bmi = calculateBMI(heightStr, weightStr);

        // Save BMI value to SharedPreferences
        updateField("bmi", String.valueOf(bmi));

        return getBMICategory(bmi); // Return BMI category as string
    }

    // Determine BMI category
    public String getBMICategory(float bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            return "Normal weight";
        } else if (bmi >= 25 && bmi <= 29.9) {
            return "Overweight";
        } else {
            return "Obesity";
        }
    }
}
