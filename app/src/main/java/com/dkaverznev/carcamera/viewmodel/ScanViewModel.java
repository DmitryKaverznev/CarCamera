// ScanViewModel.java
package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.dkaverznev.carcamera.data.CameraRepository;
import com.google.mlkit.vision.common.InputImage;
import androidx.camera.core.ImageProxy;

public class ScanViewModel extends AndroidViewModel {

    private final CameraRepository scanRepository;

    public final LiveData<String> scannedText;
    public final LiveData<String> scanErrorMessage;

    private final MutableLiveData<String> _scannedTextInternal;


    public ScanViewModel(@NonNull Application application) {
        super(application);
        scanRepository = new CameraRepository();

        _scannedTextInternal = scanRepository.scannedText;
        this.scannedText = _scannedTextInternal;
        this.scanErrorMessage = scanRepository.scanErrorMessage;
    }

    public void startTextScan(InputImage image, ImageProxy imageProxy) {
        scanRepository.scan(image, imageProxy);
    }

    public void clearScannedText() {
        _scannedTextInternal.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        scanRepository.close();
    }
}