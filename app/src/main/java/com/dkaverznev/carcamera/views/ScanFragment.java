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

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class ScanFragment extends Fragment {

    private ScanViewModel mViewModel;
    private FragmentScanBinding binding;
    private NavController navController;
    private ExecutorService cameraExecutor;

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
        binding.scannedTextView.setText(getString(R.string.text_scanning_in_progress));
    }

    private void setupObservers() {
        mViewModel.scannedText.observe(getViewLifecycleOwner(), text -> {
            if (text != null && !text.isEmpty()) {
                binding.scannedTextView.setText(text);
                binding.errorTextView.setText("");
                isProcessingImage.set(false);
                Log.d("ScanFragment", "Успешно распознан текст: " + text);
            }
        });

        mViewModel.scanErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.errorTextView.setText(errorMessage);
                binding.scannedTextView.setText("");
                Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                isProcessingImage.set(false);
                Log.e("ScanFragment", "Получена ошибка сканирования: " + errorMessage);
            } else {
                binding.errorTextView.setText("");
            }
        });
    }

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
                        Log.d("ScanFragment", "Начата обработка нового кадра.");

                        @Nullable InputImage inputImage;
                        try {
                            inputImage = InputImage.fromMediaImage(
                                    Objects.requireNonNull(imageProxy.getImage()),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );
                        } catch (Exception e) {
                            Log.e("ScanFragment", "Ошибка создания InputImage: " + e.getMessage(), e);
                            isProcessingImage.set(false);
                            imageProxy.close();
                            return;
                        }

                        mViewModel.startTextScan(inputImage);

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

        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
        binding = null;
    }
}