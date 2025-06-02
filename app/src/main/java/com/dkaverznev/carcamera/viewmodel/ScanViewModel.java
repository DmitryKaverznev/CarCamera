package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.dkaverznev.carcamera.data.ScanRepository;
import com.google.mlkit.vision.common.InputImage;

public class ScanViewModel extends AndroidViewModel {

    private final ScanRepository scanRepository;

    public final LiveData<String> scannedText;
    public final LiveData<String> scanErrorMessage;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        scanRepository = new ScanRepository();


        this.scannedText = scanRepository.scannedText;
        this.scanErrorMessage = scanRepository.scanErrorMessage;
    }

    public void startTextScan(InputImage image) {
        scanRepository.scan(image);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        scanRepository.close();
    }
}