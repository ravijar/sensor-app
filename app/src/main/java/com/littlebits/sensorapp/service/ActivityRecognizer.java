package com.littlebits.sensorapp.service;

import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public class ActivityRecognizer {
    private OrtEnvironment env;
    private OrtSession session;

    public ActivityRecognizer(String modelPath) throws OrtException {
        env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        session = env.createSession(modelPath, options);
    }

    public int predictActivity(float[] sensorData) throws OrtException {
        // Ensure sensorData has exactly 6 values
        if (sensorData.length != 6) {
            throw new IllegalArgumentException("Sensor data must contain exactly 6 values.");
        }

        // Convert input array into ONNX tensor
        long[] shape = {1, 6};  // Single sample, 6 features
        FloatBuffer inputTensor = FloatBuffer.wrap(sensorData);
        OnnxTensor input = OnnxTensor.createTensor(env, inputTensor, shape);
        Map<String, OnnxTensor> inputs = Collections.singletonMap(session.getInputNames().iterator().next(), input);

        // Run inference
        OrtSession.Result result = session.run(inputs);
        Object outputObj = result.get(0).getValue();

        int predictedLabel = 0;
        if (outputObj instanceof float[]) {
            float[] output = (float[]) outputObj;
            predictedLabel = argMax(output);
        } else if (outputObj instanceof long[]) {
            long[] output = (long[]) outputObj;
            predictedLabel = (int) output[0];  // Convert long to int
        } else {
            throw new RuntimeException("Unexpected output type: " + outputObj.getClass().getSimpleName());
        }

        return predictedLabel;
    }

    private int argMax(float[] array) {
        int maxIndex = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
}
