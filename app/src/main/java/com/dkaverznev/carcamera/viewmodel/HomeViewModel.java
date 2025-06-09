package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.dkaverznev.carcamera.data.scans.DataScansRepository;
import com.dkaverznev.carcamera.data.scans.Scan;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final DataScansRepository dataScansRepository;
    private final LiveData<List<Scan>> allScans;
    private final LiveData<String> errorMessage;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        dataScansRepository = new DataScansRepository();
        allScans = dataScansRepository.allScans;
        errorMessage = dataScansRepository.errorMessage;
        loadAllScans();
    }

    public LiveData<List<Scan>> getAllScans() {
        return allScans;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void loadAllScans() {
        dataScansRepository.getAllUserScans();
    }
}