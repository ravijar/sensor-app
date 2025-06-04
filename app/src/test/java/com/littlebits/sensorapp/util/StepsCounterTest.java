package com.littlebits.sensorapp.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.XFloatSensor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

public class StepsCounterTest {

    private StepsCounter stepsCounter;

    @Mock
    private Context mockContext;
    @Mock
    private SensorRepository mockSensorRepository;
    @Mock
    private BaseSensor mockStepSensor, mockAccelSensor, mockGyroSensor;
    @Mock
    private SensorManager mockSensorManager;
    @Mock
    private StepsCounter.StepListener mockStepListener;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Mock context's getSystemService for SensorManager
        when(mockContext.getSystemService(Context.SENSOR_SERVICE)).thenReturn(mockSensorManager);

        // Mock SensorRepository.getInstance() and its methods
        // This is a workaround for testing static dependencies. In a real project, prefer dependency injection.
        try {
            java.lang.reflect.Field instanceField = SensorRepository.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, mockSensorRepository); // Set the static instance to our mock
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Instantiate StepsCounter after mocking SensorRepository.getInstance()
        stepsCounter = new StepsCounter(mockContext);

        // Manually set the sensor mocks because the constructor calls repository.getSensor()
        try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);

            java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, mockAccelSensor);

            java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, mockGyroSensor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testStart_stepSensorAvailable() {
        when(mockSensorRepository.getSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(mockStepSensor);
        when(mockSensorRepository.getSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(mockAccelSensor);
        when(mockSensorRepository.getSensor(Sensor.TYPE_GYROSCOPE)).thenReturn(mockGyroSensor);

        // Ensure the mock sensors are set in the StepsCounter instance
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.start(mockStepListener);

        verify(mockStepSensor).addObserver(stepsCounter);
        verify(mockStepSensor).register();
        verify(mockAccelSensor, never()).addObserver(stepsCounter);
        verify(mockAccelSensor, never()).register();
        verify(mockGyroSensor, never()).addObserver(stepsCounter);
        verify(mockGyroSensor, never()).register();
        verify(mockStepListener, never()).onStepDetected(anyInt());
    }

    @Test
    public void testStart_stepSensorNotAvailable_fallbackSensorsAvailable() {
        when(mockSensorRepository.getSensor(Sensor.TYPE_STEP_COUNTER)).thenReturn(null);
        when(mockSensorRepository.getSensor(Sensor.TYPE_ACCELEROMETER)).thenReturn(mockAccelSensor);
        when(mockSensorRepository.getSensor(Sensor.TYPE_GYROSCOPE)).thenReturn(mockGyroSensor);

        // Ensure the mock sensors are set in the StepsCounter instance (stepSensor is null)
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, null);
             java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, mockAccelSensor);
             java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, mockGyroSensor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.start(mockStepListener);

        verify(mockStepSensor, never()).addObserver(stepsCounter);
        verify(mockStepSensor, never()).register();
        verify(mockAccelSensor).addObserver(stepsCounter);
        verify(mockAccelSensor).register();
        verify(mockGyroSensor).addObserver(stepsCounter);
        verify(mockGyroSensor).register();
        verify(mockStepListener, never()).onStepDetected(anyInt());
    }

     @Test
    public void testStart_noSensorsAvailable() {
        when(mockSensorRepository.getSensor(anyInt())).thenReturn(null);

         // Ensure the mock sensors are set to null in the StepsCounter instance
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, null);
             java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, null);
             java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.start(mockStepListener);

        verify(mockStepSensor, never()).addObserver(stepsCounter);
        verify(mockStepSensor, never()).register();
        verify(mockAccelSensor, never()).addObserver(stepsCounter);
        verify(mockAccelSensor, never()).register();
        verify(mockGyroSensor, never()).addObserver(stepsCounter);
        verify(mockGyroSensor, never()).register();
        verify(mockStepListener).onStepDetected(eq(-1)); // Indicate no fallback possible
    }

    @Test
    public void testStop() {
        // Ensure sensors are not null for this test to verify unregister/removeObserver calls
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);

            java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, mockAccelSensor);

            java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, mockGyroSensor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.stop();

        verify(mockStepSensor).unregister();
        verify(mockStepSensor).removeObserver(stepsCounter);
        verify(mockAccelSensor).unregister();
        verify(mockAccelSensor).removeObserver(stepsCounter);
        verify(mockGyroSensor).unregister();
        verify(mockGyroSensor).removeObserver(stepsCounter);
    }

    @Test
    public void testPause() {
         // Ensure sensors are not null
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);

            java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, mockAccelSensor);

            java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, mockGyroSensor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.pause();

        // Verify isPaused is set to true
        try {
            java.lang.reflect.Field isPausedField = StepsCounter.class.getDeclaredField("isPaused");
            isPausedField.setAccessible(true);
            assertTrue((boolean) isPausedField.get(stepsCounter));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        verify(mockStepSensor).unregister();
        verify(mockAccelSensor).unregister();
        verify(mockGyroSensor).unregister();
    }

    @Test
    public void testResume() {
         // Ensure sensors are not null
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);

            java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, mockAccelSensor);

            java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, mockGyroSensor);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Ensure isPaused is initially true for this test
         try {
            java.lang.reflect.Field isPausedField = StepsCounter.class.getDeclaredField("isPaused");
            isPausedField.setAccessible(true);
            isPausedField.set(stepsCounter, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        stepsCounter.resume();

         // Verify isPaused is set to false
        try {
            java.lang.reflect.Field isPausedField = StepsCounter.class.getDeclaredField("isPaused");
            isPausedField.setAccessible(true);
            assertFalse((boolean) isPausedField.get(stepsCounter));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        verify(mockStepSensor).register();
        verify(mockAccelSensor).register();
        verify(mockGyroSensor).register();
    }

     @Test
    public void testGetCurrentStepCount() {
        // Manually set the currentStepCount using reflection for testing
        int expectedStepCount = 150;
        try {
            java.lang.reflect.Field currentStepCountField = StepsCounter.class.getDeclaredField("currentStepCount");
            currentStepCountField.setAccessible(true);
            currentStepCountField.set(stepsCounter, expectedStepCount);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        assertEquals(expectedStepCount, stepsCounter.getCurrentStepCount());
     }

     @Test
    public void testOnSensorChanged_stepCounterType() {
        // Ensure stepSensor is not null and fallback sensors are null for this test path
        try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);
             java.lang.reflect.Field accelSensorField = StepsCounter.class.getDeclaredField("accelSensor");
            accelSensorField.setAccessible(true);
            accelSensorField.set(stepsCounter, null);
             java.lang.reflect.Field gyroSensorField = StepsCounter.class.getDeclaredField("gyroSensor");
            gyroSensorField.setAccessible(true);
            gyroSensorField.set(stepsCounter, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Manually set the listener
        try {
            java.lang.reflect.Field listenerField = StepsCounter.class.getDeclaredField("listener");
            listenerField.setAccessible(true);
            listenerField.set(stepsCounter, mockStepListener);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Mock the XFloatSensor (stepSensor) to return values
        XFloatSensor mockXFloatSensor = mock(XFloatSensor.class);
         try {
            java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockXFloatSensor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Simulate sensor readings: initial reading of 1000, then 1005 (5 steps)
        when(mockXFloatSensor.getX()).thenReturn(1000f).thenReturn(1005f);

        // Initial call to set initialStepCount
        stepsCounter.onSensorChanged(Sensor.TYPE_STEP_COUNTER);

        // Second call to simulate steps and notify listener
        stepsCounter.onSensorChanged(Sensor.TYPE_STEP_COUNTER);

        // Verify the listener is called with the correct step count relative to the initial
        verify(mockStepListener).onStepDetected(eq(5));

        // Note: Testing the accelerometer/gyroscope fallback logic in onSensorChanged
        // is significantly more complex due to the need to simulate sequences of sensor
        // events over time and the internal state management (lastAccelZ, accelRising, lastStepTime).
        // This would require a more advanced testing approach, potentially involving
        // a custom test runner or a helper class to feed simulated sensor data and time.
        // For now, we will not add tests for this fallback mechanism.
    }

     @Test
    public void testOnSensorChanged_whenPaused() {
        // Ensure isPaused is true
         try {
            java.lang.reflect.Field isPausedField = StepsCounter.class.getDeclaredField("isPaused");
            isPausedField.setAccessible(true);
            isPausedField.set(stepsCounter, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Manually set the listener and sensors (optional, as nothing should be called)
         try {
            java.lang.reflect.Field listenerField = StepsCounter.class.getDeclaredField("listener");
            listenerField.setAccessible(true);
            listenerField.set(stepsCounter, mockStepListener);
             java.lang.reflect.Field stepSensorField = StepsCounter.class.getDeclaredField("stepSensor");
            stepSensorField.setAccessible(true);
            stepSensorField.set(stepsCounter, mockStepSensor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        // Call onSensorChanged for any sensor type
        stepsCounter.onSensorChanged(Sensor.TYPE_STEP_COUNTER);

        // Verify that no interactions with the listener or sensors occur
        verify(mockStepListener, never()).onStepDetected(anyInt());
        verify(mockStepSensor, never()).unregister(); // Just an example, any sensor method would do
    }


} 