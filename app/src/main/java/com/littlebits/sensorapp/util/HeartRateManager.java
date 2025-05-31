package com.littlebits.sensorapp.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import android.graphics.ImageFormat;
import android.media.Image;
import android.media.ImageReader;

public class HeartRateManager {
    public interface HeartRateListener {
        void onHeartRateMeasured(int heartRate);
        void onError(String error);
    }

    private final Context context;
    private final SensorManager sensorManager;
    private final CameraManager cameraManager;
    private Sensor heartRateSensor;
    private HeartRateListener listener;
    private SensorEventListener sensorEventListener;

    // Camera variables
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewRequestBuilder;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private SurfaceView surfaceView;
    private boolean measuringWithCamera = false;
    private List<Integer> redAvgList = new ArrayList<>();
    private long startTime = 0;
    private ImageReader imageReader;
    private final List<Long> redPeaksTimestamps = new ArrayList<>();
    private final List<Integer> redAvgHistory = new ArrayList<>();
    private static final int MEASUREMENT_DURATION_MS = 10000; // 10 seconds
    private static final int FRAME_RATE = 15; // Target frame rate

    public HeartRateManager(Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        if (sensorManager != null) {
            heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        }
    }

    public boolean hasHeartRateSensor() {
        return heartRateSensor != null;
    }

    public void measureHeartRate(HeartRateListener listener, SurfaceView surfaceView) {
        this.listener = listener;
        this.surfaceView = surfaceView;
        if (hasHeartRateSensor()) {
            startSensorMeasurement();
        } else {
            startCameraMeasurement();
        }
    }

    private void startSensorMeasurement() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
                    int heartRate = Math.round(event.values[0]);
                    if (listener != null) listener.onHeartRateMeasured(heartRate);
                    sensorManager.unregisterListener(this);
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(sensorEventListener, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void startCameraMeasurement() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (listener != null) listener.onError("Camera permission not granted");
            return;
        }
        measuringWithCamera = true;
        startBackgroundThread();
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                    createCameraPreviewSession();
                }
                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    camera.close();
                    cameraDevice = null;
                }
                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                    camera.close();
                    cameraDevice = null;
                    if (listener != null) listener.onError("Camera error: " + error);
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            if (listener != null) listener.onError("Camera access error: " + e.getMessage());
        }
    }

    private void createCameraPreviewSession() {
        try {
            SurfaceHolder holder = surfaceView.getHolder();
            Surface surface = holder.getSurface();
            int width = surfaceView.getWidth() > 0 ? surfaceView.getWidth() : 320;
            int height = surfaceView.getHeight() > 0 ? surfaceView.getHeight() : 240;
            imageReader = ImageReader.newInstance(width, height, ImageFormat.YUV_420_888, 2);
            imageReader.setOnImageAvailableListener(reader -> {
                Image image = null;
                try {
                    image = reader.acquireLatestImage();
                    if (image != null) {
                        int redAvg = computeRedAverage(image);
                        long now = System.currentTimeMillis();
                        redAvgHistory.add(redAvg);
                        detectPeak(redAvg, now);
                        if (now - startTime > MEASUREMENT_DURATION_MS) {
                            analyzePeaksAndFinish();
                        }
                    }
                } finally {
                    if (image != null) image.close();
                }
            }, backgroundHandler);
            previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewRequestBuilder.addTarget(surface);
            previewRequestBuilder.addTarget(imageReader.getSurface());
            previewRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);
            cameraDevice.createCaptureSession(List.of(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                        startTime = System.currentTimeMillis();
                        redAvgList.clear();
                        redPeaksTimestamps.clear();
                        redAvgHistory.clear();
                    } catch (CameraAccessException e) {
                        if (listener != null) listener.onError("Camera session error: " + e.getMessage());
                    }
                }
                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                    if (listener != null) listener.onError("Camera session configuration failed");
                }
            }, backgroundHandler);
        } catch (CameraAccessException e) {
            if (listener != null) listener.onError("Camera preview error: " + e.getMessage());
        }
    }

    private int computeRedAverage(Image image) {
        // YUV_420_888: Y is brightness, U/V are color. We'll use Y as a proxy for red intensity.
        // For a more accurate result, convert YUV to RGB and use R, but Y is sufficient for pulse detection.
        Image.Plane yPlane = image.getPlanes()[0];
        byte[] yBuffer = new byte[yPlane.getBuffer().remaining()];
        yPlane.getBuffer().get(yBuffer);
        int sum = 0;
        for (byte b : yBuffer) {
            sum += (b & 0xFF);
        }
        return sum / yBuffer.length;
    }

    private int lastRedAvg = -1;
    private boolean lastWasPeak = false;
    private void detectPeak(int redAvg, long timestamp) {
        // Simple peak detection: look for a local minimum (valley) in the red signal
        if (lastRedAvg != -1) {
            if (!lastWasPeak && redAvg < lastRedAvg - 2) { // threshold = 2
                // Detected a valley (pulse)
                redPeaksTimestamps.add(timestamp);
                lastWasPeak = true;
            } else if (redAvg > lastRedAvg) {
                lastWasPeak = false;
            }
        }
        lastRedAvg = redAvg;
    }

    private void analyzePeaksAndFinish() {
        if (redPeaksTimestamps.size() < 2) {
            if (listener != null) listener.onError("Not enough data to calculate heart rate");
            stopCameraMeasurement();
            return;
        }
        // Calculate intervals between peaks
        List<Long> intervals = new ArrayList<>();
        for (int i = 1; i < redPeaksTimestamps.size(); i++) {
            intervals.add(redPeaksTimestamps.get(i) - redPeaksTimestamps.get(i - 1));
        }
        // Average interval in ms
        long avgInterval = 0;
        for (long interval : intervals) avgInterval += interval;
        avgInterval /= intervals.size();
        int heartRate = (int) (60000 / avgInterval); // bpm
        if (listener != null) listener.onHeartRateMeasured(heartRate);
        stopCameraMeasurement();
    }

    public void stopMeasurement() {
        if (hasHeartRateSensor() && sensorEventListener != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        if (measuringWithCamera) {
            stopCameraMeasurement();
        }
    }

    private void stopCameraMeasurement() {
        measuringWithCamera = false;
        if (captureSession != null) {
            captureSession.close();
            captureSession = null;
        }
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
        stopBackgroundThread();
    }

    private void startBackgroundThread() {
        backgroundThread = new HandlerThread("CameraBackground");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void stopBackgroundThread() {
        if (backgroundThread != null) {
            backgroundThread.quitSafely();
            try {
                backgroundThread.join();
                backgroundThread = null;
                backgroundHandler = null;
            } catch (InterruptedException e) {
                Log.e("HeartRateManager", "Background thread interrupted");
            }
        }
    }
}