package com.dkaverznev.carcamera.views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.camera.core.ExperimentalGetImage;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.core.content.ContextCompat;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;

import com.dkaverznev.carcamera.utils.PermissionUtils;
import com.google.common.util.concurrent.ListenableFuture;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.databinding.FragmentScanBinding;
import com.dkaverznev.carcamera.viewmodel.ScanViewModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanFragment extends Fragment {

    private ScanViewModel mViewModel;
    private FragmentScanBinding binding;
    private NavController navController;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;

    private final AtomicBoolean isProcessingImage = new AtomicBoolean(false);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentScanBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(ScanViewModel.class);
        navController = Navigation.findNavController(view);
        cameraExecutor = Executors.newSingleThreadExecutor();

        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        ActivityResultLauncher<String> requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        startCamera();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.text_permission_camera), Toast.LENGTH_SHORT).show();
                        navController.popBackStack();
                    }
                }
        );

        initUI();
        setupObservers();

        if (PermissionUtils.isCameraPermissionGranted(getContext())) {
            startCamera();
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void initUI() {
    }

    private void setupObservers() {
        mViewModel.scanSuccess.observe(getViewLifecycleOwner(), success -> {
            if (success != null) {
                if (success) {
                    Toast.makeText(getContext(), "Сканирование успешно!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Сканирование не удалось.", Toast.LENGTH_SHORT).show();
                }
                isProcessingImage.set(false);
            }
        });

        mViewModel.errorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                isProcessingImage.set(false);
            }
        });
    }

    /**
     * Запускает камеру и привязывает её к жизненному циклу фрагмента.
     */
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        if (isProcessingImage.get()) {
                            imageProxy.close();
                            return;
                        }

                        isProcessingImage.set(true);

                        @Nullable InputImage inputImage;
                        try {
                            inputImage = InputImage.fromMediaImage(
                                    Objects.requireNonNull(imageProxy.getImage()),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );
                        } catch (Exception e) {
                            isProcessingImage.set(false);
                            imageProxy.close();
                            return;
                        }

                        mViewModel.scan(inputImage);

                        imageProxy.close();
                    }
                });

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );

            } catch (Exception e) {
                Log.e("ScanFragment", "Ошибка при запуске камеры", e);
                Toast.makeText(getContext(), "Не удалось запустить камеру: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (textRecognizer != null) {
            textRecognizer.close();
        }
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        binding = null;
    }
}