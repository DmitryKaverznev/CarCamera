package com.dkaverznev.carcamera.utils;

import android.content.Context;
import android.util.Log;
import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer; // Using Java 8 Consumer for simpler callback

public class CameraXManager {

    private static final String TAG = "CameraXManager";

    @OptIn(markerClass = ExperimentalGetImage.class)
    public static void startCamera(
            Context context,
            LifecycleOwner lifecycleOwner,
            PreviewView previewView,
            ExecutorService cameraExecutor,
            Consumer<InputImage> imageAnalyzerCallback) {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Optimize for real-time processing
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, imageProxy -> {
                    // It's safe to call getImage() with @ExperimentalGetImage
                    InputImage inputImage = InputImage.fromMediaImage(
                            Objects.requireNonNull(imageProxy.getImage()),
                            imageProxy.getImageInfo().getRotationDegrees()
                    );
                    imageAnalyzerCallback.accept(inputImage);
                    imageProxy.close(); // Important to close the imageProxy
                });

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {
                    cameraProvider.unbindAll();
                    cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis);
                } catch(Exception exc) {
                    Log.e(TAG, "Error binding CameraX use cases", exc);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting ProcessCameraProvider", e);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    public static void shutdownExecutor(ExecutorService executorService) {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}