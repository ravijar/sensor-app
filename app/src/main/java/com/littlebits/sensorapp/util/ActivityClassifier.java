package com.littlebits.sensorapp.util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

import com.littlebits.sensorapp.model.ActivityLabel;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ActivityClassifier {

    private static final String MODEL_FILE = "model.tflite";
    private static final int INPUT_LENGTH = 100; // time steps
    private static final int INPUT_WIDTH = 9;    // features per step
    private static final int OUTPUT_SIZE = 7;    // 7 activity classes

    private final Interpreter interpreter;

    public ActivityClassifier(Context context) {
        try {
            interpreter = new Interpreter(loadModelFile(context));
        } catch (IOException e) {
            throw new RuntimeException("Error initializing TFLite model!", e);
        }
    }

    private MappedByteBuffer loadModelFile(Context context) throws IOException {
        AssetFileDescriptor fileDescriptor = context.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public float[] predictProbabilities(float[] inputDataFlat) {
        float[][][] input = new float[1][INPUT_LENGTH][INPUT_WIDTH];
        float[][] output = new float[1][OUTPUT_SIZE];

        for (int i = 0; i < INPUT_LENGTH; i++) {
            for (int j = 0; j < INPUT_WIDTH; j++) {
                input[0][i][j] = inputDataFlat[i * INPUT_WIDTH + j];
            }
        }

        interpreter.run(input, output);
        return output[0];
    }

    public ActivityLabel getTopPrediction(float[] inputDataFlat) {
        float[] probabilities = predictProbabilities(inputDataFlat);

        int maxIndex = 0;
        float maxProb = probabilities[0];
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > maxProb) {
                maxProb = probabilities[i];
                maxIndex = i;
            }
        }
        return ActivityLabel.fromIndex(maxIndex);
    }
}
