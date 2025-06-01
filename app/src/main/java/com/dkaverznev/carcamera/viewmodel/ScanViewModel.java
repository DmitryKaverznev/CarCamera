package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.ScanRepository;
import com.google.mlkit.vision.common.InputImage; // Импорт InputImage

public class ScanViewModel extends AndroidViewModel {

    private final ScanRepository scanRepository;

    private final MutableLiveData<Boolean> _scanSuccess = new MutableLiveData<>();
    public LiveData<Boolean> scanSuccess = _scanSuccess;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public ScanViewModel(@NonNull Application application) {
        super(application);
        scanRepository = new ScanRepository(application.getApplicationContext());
    }

    public void scan(InputImage image) {
        scanRepository.scan(image);
    }
}