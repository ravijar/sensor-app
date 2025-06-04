package com.littlebits.sensorapp.manager;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import org.mockito.ArgumentCaptor;

public class PersonalDetailsManagerTest {

    private PersonalDetailsManager personalDetailsManager;
    private Context mockContext;
    private SharedPreferences mockSharedPreferences;
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        mockContext = mock(Context.class);
        mockSharedPreferences = mock(SharedPreferences.class);
        mockEditor = mock(SharedPreferences.Editor.class);

        when(mockContext.getSharedPreferences(anyString(), anyInt()))
                .thenReturn(mockSharedPreferences);
        when(mockSharedPreferences.edit()).thenReturn(mockEditor);

        // Mock editor putString and apply to return the editor itself
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        // The apply() method is void, no need to mock its return value

        personalDetailsManager = new PersonalDetailsManager(mockContext);
    }

    @Test
    public void testCalculateBMI_validInput() {
        // Height in cm, Weight in kg
        float bmi = personalDetailsManager.calculateBMI("175", "70"); // 1.75m, 70kg
        // BMI = 70 / (1.75 * 1.75) = 70 / 3.0625 = 22.857...
        assertEquals(22.85f, bmi, 0.01f); // Using delta for float comparison
    }

    @Test
    public void testCalculateBMI_invalidHeight() {
        float bmi = personalDetailsManager.calculateBMI("abc", "70");
        assertEquals(0.0f, bmi, 0.01f);
    }

     @Test
    public void testCalculateBMI_invalidWeight() {
        float bmi = personalDetailsManager.calculateBMI("175", "xyz");
        assertEquals(0.0f, bmi, 0.01f);
    }

    @Test
    public void testGetBMICategory_underweight() {
        assertEquals("Underweight", personalDetailsManager.getBMICategory(18.0f));
    }

    @Test
    public void testGetBMICategory_normalWeight() {
        assertEquals("Normal Weight", personalDetailsManager.getBMICategory(22.0f));
    }

    @Test
    public void testGetBMICategory_overweight() {
        assertEquals("Overweight", personalDetailsManager.getBMICategory(26.0f));
    }

    @Test
    public void testGetBMICategory_obesity() {
        assertEquals("Obesity", personalDetailsManager.getBMICategory(35.0f));
    }

    @Test
    public void testGetSavedData_keyExists() {
        String testKey = "name";
        String testValue = "John Doe";
        when(mockSharedPreferences.getString(eq(testKey), anyString())).thenReturn(testValue);

        String retrievedValue = personalDetailsManager.getSavedData(testKey);
        assertEquals(testValue, retrievedValue);
    }

    @Test
    public void testGetSavedData_keyDoesNotExist() {
         String testKey = "nonexistent_key";
         when(mockSharedPreferences.getString(eq(testKey), anyString())).thenReturn(""); // Mocking the default return

         String retrievedValue = personalDetailsManager.getSavedData(testKey);
         assertEquals("", retrievedValue);
    }

     @Test
    public void testCalculateAndSaveBMI() {
        String height = "180"; // 1.8m
        String weight = "80"; // 80kg
        // BMI = 80 / (1.8 * 1.8) = 80 / 3.24 = 24.69...
        float expectedRawBmi = 80.0f / (Float.parseFloat(height) / 100.0f * Float.parseFloat(height) / 100.0f);

        String resultCategory = personalDetailsManager.calculateAndSaveBMI(height, weight);

        // Verify that updateField was called with the correct key and a reasonable BMI string value
        // Capture the argument passed to putString for the "bmi" key
        ArgumentCaptor<String> bmiStringCaptor = ArgumentCaptor.forClass(String.class);
        verify(mockEditor).putString(eq("bmi"), bmiStringCaptor.capture());

        // Parse the captured string back to a float and compare with the expected raw BMI
        float capturedBmi = Float.parseFloat(bmiStringCaptor.getValue());
        assertEquals(expectedRawBmi, capturedBmi, 0.01f); // Allow for small floating-point differences

        verify(mockEditor).apply();

        // Verify that the correct BMI category is returned (based on the calculated BMI)
        assertEquals("Normal Weight", resultCategory);
    }

    @Test
    public void testSavePersonalDetails() {
        String name = "Jane Doe";
        String age = "30";
        String gender = "Female";
        String height = "165";
        String weight = "60";
        String sosContact = "9876543210";

        personalDetailsManager.savePersonalDetails(name, age, gender, height, weight, sosContact);

        verify(mockEditor).putString(eq("name"), eq(name));
        verify(mockEditor).putString(eq("age"), eq(age));
        verify(mockEditor).putString(eq("gender"), eq(gender));
        verify(mockEditor).putString(eq("height"), eq(height));
        verify(mockEditor).putString(eq("weight"), eq(weight));
        verify(mockEditor).putString(eq("sos_contact"), eq(sosContact));
        verify(mockEditor).apply();
    }

    @Test
    public void testUpdateField() {
        String key = "test_key";
        String value = "test_value";

        personalDetailsManager.updateField(key, value);

        verify(mockEditor).putString(eq(key), eq(value));
        verify(mockEditor).apply();
    }
} 