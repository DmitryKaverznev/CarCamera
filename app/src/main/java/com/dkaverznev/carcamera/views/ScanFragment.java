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

import com.dkaverznev.carcamera.data.vehicles.ResultTypeEnum;
import com.dkaverznev.carcamera.utils.PermissionUtils;
import com.google.common.util.concurrent.ListenableFuture;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.databinding.FragmentScanBinding;
import com.dkaverznev.carcamera.viewmodel.ScanViewModel;
import com.google.mlkit.vision.common.InputImage;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanFragment extends Fragment {

    private ScanViewModel mViewModel;
    private FragmentScanBinding binding;
    private NavController navController;
    private ExecutorService cameraExecutor;

    private ProcessCameraProvider cameraProvider;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ImageAnalysis imageAnalysis;

    private String recognizedLicensePlate = null;

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
        // Скрываем FABs при инициализации UI
        binding.fabRetake.setVisibility(View.GONE);
        binding.fabNext.setVisibility(View.GONE);
        binding.fabNextBack.setVisibility(View.GONE);

        binding.buttonBack.setOnClickListener(v->
                navController.navigateUp());

        binding.fabRetake.setOnClickListener(v -> {
            binding.scannedTextView.setText(getString(R.string.text_scanning_in_progress));
            binding.fabRetake.setVisibility(View.GONE);
            binding.fabNext.setVisibility(View.GONE);
            binding.fabNextBack.setVisibility(View.GONE);
            binding.errorTextView.setText("");
            recognizedLicensePlate = null;
            if (cameraProvider != null) {
                cameraProvider.unbindAll();
            }
            startCamera();
        });

        binding.fabNext.setOnClickListener(v -> {
            if (recognizedLicensePlate != null && !recognizedLicensePlate.isEmpty()) {
                ScanFragmentDirections.ActionScanFragmentToResultFragment action =
                        ScanFragmentDirections.actionScanFragmentToResultFragment(recognizedLicensePlate, ResultTypeEnum.FORWARD);
                navController.navigate(action);

                mViewModel.clearScannedText();
                recognizedLicensePlate = null;
            }
        });

        binding.fabNextBack.setOnClickListener(v -> {
            if (recognizedLicensePlate != null && !recognizedLicensePlate.isEmpty()) {
                ScanFragmentDirections.ActionScanFragmentToResultFragment action =
                        ScanFragmentDirections.actionScanFragmentToResultFragment(recognizedLicensePlate, ResultTypeEnum.BACK);
                navController.navigate(action);

                mViewModel.clearScannedText();
                recognizedLicensePlate = null;
            }
        });
    }

    private void setupObservers() {
        mViewModel.scannedText.observe(getViewLifecycleOwner(), text -> {
            if (text != null && !text.isEmpty()) {
                binding.scannedTextView.setText(text);
                binding.errorTextView.setText("");

                recognizedLicensePlate = text;
                binding.fabNext.setVisibility(View.VISIBLE);
                binding.fabRetake.setVisibility(View.VISIBLE);
                binding.fabNextBack.setVisibility(View.VISIBLE);

                if (cameraProvider != null) {
                    cameraProvider.unbind(imageAnalysis, preview);
                    Log.d("ScanFragment", "ImageAnalysis и Preview отвязаны. Камера остановлена.");
                }

            } else {
                binding.scannedTextView.setText(getString(R.string.text_scanning_in_progress));
                binding.fabRetake.setVisibility(View.GONE);
                binding.fabNext.setVisibility(View.GONE);
                binding.fabNextBack.setVisibility(View.GONE);
                recognizedLicensePlate = null;
            }
        });

        mViewModel.scanErrorMessage.observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                binding.errorTextView.setText(errorMessage);
                binding.scannedTextView.setText(getString(R.string.text_scanning_in_progress));
                Toast.makeText(getContext(), "Ошибка: " + errorMessage, Toast.LENGTH_LONG).show();
                binding.fabRetake.setVisibility(View.VISIBLE);
                binding.fabNext.setVisibility(View.GONE);
                binding.fabNextBack.setVisibility(View.GONE);
                recognizedLicensePlate = null;
            } else {
                binding.errorTextView.setText("");
            }
        });
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        binding.fabRetake.setVisibility(View.GONE);
        binding.fabNext.setVisibility(View.GONE);
        binding.fabNextBack.setVisibility(View.GONE);

        cameraProviderFuture.addListener(() -> {
            try {
                this.cameraProvider = cameraProviderFuture.get();

                if (binding == null) {
                    Log.w("ScanFragment", "Cannot start camera.");
                    return;
                }

                this.preview = new Preview.Builder()
                        .build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                this.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                this.imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class)
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        if (recognizedLicensePlate != null && !recognizedLicensePlate.isEmpty()) {
                            imageProxy.close();
                            return;
                        }

                        @Nullable InputImage inputImage;
                        try {
                            inputImage = InputImage.fromMediaImage(
                                    Objects.requireNonNull(imageProxy.getImage()),
                                    imageProxy.getImageInfo().getRotationDegrees()
                            );
                        } catch (Exception e) {
                            Log.e("ScanFragment", "Ошибка создания InputImage: " + e.getMessage(), e);
                            imageProxy.close();
                            return;
                        }

                        mViewModel.startTextScan(inputImage, imageProxy);
                    }
                });

                cameraProvider.unbindAll();

                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageAnalysis
                );
                Log.d("ScanFragment", "Камера привязана: Preview и ImageAnalysis активны.");

            } catch (Exception e) {
                Log.e("ScanFragment", "Ошибка при запуске камеры", e);
                Toast.makeText(requireContext(), "Не удалось запустить камеру: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        if (cameraExecutor != null && !cameraExecutor.isShutdown()) {
            cameraExecutor.shutdown();
        }
        binding = null;
    }
}