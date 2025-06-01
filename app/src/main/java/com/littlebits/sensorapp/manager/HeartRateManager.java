package com.littlebits.sensorapp.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Range;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.littlebits.sensorapp.repository.SensorRepository;
import com.littlebits.sensorapp.sensor.BaseSensor;
import com.littlebits.sensorapp.sensor.HeartRate;
import com.littlebits.sensorapp.sensor.SensorObserver;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class HeartRateManager implements SensorObserver {

    public interface HeartRateListener {
        void onHeartRateMeasured(int heartRate);
        void onError(String error);
    }

    private final Context context;
    private final CameraManager cameraManager;
    private HeartRateListener listener;

    private BaseSensor heartRateSensor;

    // Camera variables
    private CameraDevice cameraDevice;
    private CameraCaptureSession captureSession;
    private CaptureRequest.Builder previewRequestBuilder;
    private Handler backgroundHandler;
    private HandlerThread backgroundThread;
    private SurfaceView surfaceView;
    private boolean measuringWithCamera = false;
    private ImageReader imageReader;
    private final List<Long> redPeaksTimestamps = new ArrayList<>();
    private final List<Integer> redAvgHistory = new ArrayList<>();
    private static final int MEASUREMENT_DURATION_MS = 15000;
    private static final int FRAME_RATE = 30;
    private int lastRedAvg = -1;
    private boolean rising = false;
    private long startTime = 0;

    public HeartRateManager(Context context) {
        this.context = context;
        this.cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

        SensorRepository repository = SensorRepository.getInstance();
        repository.initializeSensors((android.hardware.SensorManager) context.getSystemService(Context.SENSOR_SERVICE));
        heartRateSensor = repository.getSensor(android.hardware.Sensor.TYPE_HEART_RATE);
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
        if (heartRateSensor == null) {
            if (listener != null) listener.onError("Heart rate sensor not available");
            return;
        }

        heartRateSensor.addObserver(this);
        heartRateSensor.register();
    }

    @Override
    public void onSensorChanged(int sensorType) {
        if (heartRateSensor != null && sensorType == android.hardware.Sensor.TYPE_HEART_RATE) {
            float x = ((HeartRate) heartRateSensor).getX();
            int bpm = Math.round(x);
            if (listener != null) listener.onHeartRateMeasured(bpm);
            heartRateSensor.unregister();
            heartRateSensor.removeObserver(this);
        }
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
            int width = surfaceView.getWidth() > 0 ? surfaceView.getWidth() : 640;
            int height = surfaceView.getHeight() > 0 ? surfaceView.getHeight() : 480;

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
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, new Range<>(FRAME_RATE, FRAME_RATE));

            cameraDevice.createCaptureSession(List.of(surface, imageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    captureSession = session;
                    try {
                        captureSession.setRepeatingRequest(previewRequestBuilder.build(), null, backgroundHandler);
                        startTime = System.currentTimeMillis();
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
        Image.Plane yPlane = image.getPlanes()[0];
        Image.Plane uPlane = image.getPlanes()[1];
        Image.Plane vPlane = image.getPlanes()[2];

        ByteBuffer yBuffer = yPlane.getBuffer();
        ByteBuffer uBuffer = uPlane.getBuffer();
        ByteBuffer vBuffer = vPlane.getBuffer();

        // Get plane properties
        int yWidth = yPlane.getRowStride();
        int yHeight = image.getHeight();
        int uvWidth = uPlane.getRowStride();
        int uvHeight = yHeight / 2;

        // Safe guard against buffer overflow
        int safeWidth = Math.min(yWidth, image.getWidth());
        int safeHeight = Math.min(yHeight, image.getHeight());

        int sum = 0;
        int pixelCount = 0;

        // Process Y plane (luminance)
        for (int y = 0; y < safeHeight; y++) {
            for (int x = 0; x < safeWidth; x++) {
                if (yBuffer.remaining() > 0) {
                    int yValue = yBuffer.get() & 0xFF;

                    // Get corresponding UV values (subsampled)
                    int uvX = x / 2;
                    int uvY = y / 2;
                    int uvIndex = uvY * uvWidth + uvX;

                    // Safely get UV values
                    int uValue = 128, vValue = 128; // Defaults
                    if (uvIndex < uBuffer.remaining()) {
                        uBuffer.position(uvIndex);
                        uValue = uBuffer.get() & 0xFF;
                    }
                    if (uvIndex < vBuffer.remaining()) {
                        vBuffer.position(uvIndex);
                        vValue = vBuffer.get() & 0xFF;
                    }

                    // Convert YUV to RGB (simplified, focus on red channel)
                    int r = (int) (yValue + 1.402 * (vValue - 128));
                    sum += r;
                    pixelCount++;
                }
            }
        }

        return pixelCount > 0 ? sum / pixelCount : 0;
    }

    private void detectPeak(int redAvg, long timestamp) {
        if (lastRedAvg == -1) {
            lastRedAvg = redAvg;
            return;
        }

        // Rising edge detection
        if (redAvg > lastRedAvg) {
            rising = true;
        }
        // Falling edge after a rising edge = peak
        else if (rising && redAvg < lastRedAvg) {
            redPeaksTimestamps.add(timestamp);
            rising = false;
        }
        lastRedAvg = redAvg;
    }

    private void analyzePeaksAndFinish() {
        if (redPeaksTimestamps.size() < 2) {
            if (listener != null) listener.onError("Not enough peaks detected");
            stopCameraMeasurement();
            return;
        }

        // Filter out unrealistic intervals (40-200 BPM range)
        List<Long> validIntervals = new ArrayList<>();
        for (int i = 1; i < redPeaksTimestamps.size(); i++) {
            long interval = redPeaksTimestamps.get(i) - redPeaksTimestamps.get(i - 1);
            if (interval > 300 && interval < 1500) { // 300ms (200 BPM) to 1500ms (40 BPM)
                validIntervals.add(interval);
            }
        }

        if (validIntervals.size() < 2) {
            if (listener != null) listener.onError("No valid heart rate detected");
            stopCameraMeasurement();
            return;
        }

        // Calculate average BPM
        long avgInterval = validIntervals.stream().reduce(0L, Long::sum) / validIntervals.size();
        int heartRate = (int) (60000 / avgInterval);
        heartRate = Math.max(40, Math.min(heartRate, 200));

        if (listener != null) listener.onHeartRateMeasured(heartRate);
        stopCameraMeasurement();
    }

    public void stopMeasurement() {
        if (heartRateSensor != null) {
            heartRateSensor.unregister();
            heartRateSensor.removeObserver(this);
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