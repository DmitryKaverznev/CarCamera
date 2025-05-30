package com.dkaverznev.carcamera.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.activity.result.ActivityResultLauncher;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.databinding.FragmentScanBinding;
import com.dkaverznev.carcamera.utils.CameraXManager; // Import the new CameraXManager
import com.dkaverznev.carcamera.utils.PermissionUtils; // Import the new PermissionUtils
import com.dkaverznev.carcamera.viewmodel.ScanViewModel;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanFragment extends Fragment {

    private ScanViewModel mViewModel;
    private FragmentScanBinding binding;
    private NavController navController;
    private ExecutorService cameraExecutor;
    private TextRecognizer textRecognizer;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher; // Declare the launcher

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

        requestCameraPermissionLauncher =
                PermissionUtils.registerPermissionLauncher(
                        this,
                        new PermissionUtils.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                startCamera();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(getContext(), "Camera permission not granted.", Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }
        });

        initUI();
        setupObservers();

        PermissionUtils.requestCameraPermission(requireContext(), requestCameraPermissionLauncher, new PermissionUtils.PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                startCamera();
            }

            @Override
            public void onPermissionDenied() {
                Toast.makeText(getContext(), getString(R.string.text_permission_camera), Toast.LENGTH_SHORT).show();
                navController.popBackStack();
            }
        });
    }

    private void initUI() {
    }

    private void setupObservers() {
    }

    private void startCamera() {
        CameraXManager.startCamera(
                requireContext(),
                this,
                binding.previewView,
                cameraExecutor,
                inputImage -> mViewModel.scan(inputImage)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CameraXManager.shutdownExecutor(cameraExecutor);
        if (textRecognizer != null) {
            textRecognizer.close();
        }
        binding = null;
    }
}