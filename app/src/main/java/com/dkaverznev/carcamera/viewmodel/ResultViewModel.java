package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData; // Импортируем LiveData
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.scans.Scan;
import com.dkaverznev.carcamera.data.vehicles.DataVehiclesRepository;
import com.dkaverznev.carcamera.data.vehicles.Vehicle;
import com.dkaverznev.carcamera.data.scans.DataScansRepository;

public class ResultViewModel extends AndroidViewModel {

    private final DataVehiclesRepository dataVehiclesRepository;
    private final DataScansRepository dataScansRepository;

    public LiveData<String> authErrorMessage;
    public LiveData<Vehicle> vehicleData;
    public LiveData<String> databaseErrorMessage;
    public LiveData<String> scanErrorMessage;
    public LiveData<Scan> scanData; // Оставляем, так как будем запрашивать последний скан

    public ResultViewModel(@NonNull Application application) {
        super(application);
        dataVehiclesRepository = new DataVehiclesRepository(); // Конструктор без аргументов
        dataScansRepository = new DataScansRepository();

        authErrorMessage = dataVehiclesRepository.authErrorMessage;
        vehicleData = dataVehiclesRepository.vehicleData;
        databaseErrorMessage = dataVehiclesRepository.databaseErrorMessage;
        scanErrorMessage = dataScansRepository.errorMessage;
        scanData = dataScansRepository.scanData; // Используем публичный LiveData из репозитория
    }

    public void getVehicleDataByNumber(String vehicleLicense) {
        dataVehiclesRepository.getVehicleDataByNumber(vehicleLicense);
    }

    // Метод для добавления записи сканирования
    public void addScanRecord(Scan scan) {
        dataScansRepository.addScan(scan);
    }

    // Метод для получения последнего скана (если нужно отображать его)
    public void getLatestScan(String vehicleLicense) {
        dataScansRepository.getScan(vehicleLicense);
    }
}