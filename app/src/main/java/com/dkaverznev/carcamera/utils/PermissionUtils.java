package com.dkaverznev.carcamera.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils {

    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied();
    }

    // This method needs to be called from the Fragment's onCreate or onCreateView to register the launcher
    public static ActivityResultLauncher<String> registerPermissionLauncher(Fragment fragment, PermissionCallback callback) {
        return fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        });
    }

    public static void requestCameraPermission(
            Context context,
            ActivityResultLauncher<String> permissionLauncher,
            PermissionCallback callback) {

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }
}