package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.vehicles.DataVehiclesRepository;
import com.dkaverznev.carcamera.data.vehicles.Vehicle;

public class ResultViewModel extends AndroidViewModel {

    // Объявляем переменную репозитория
    private final DataVehiclesRepository dataVehiclesRepository;

    public MutableLiveData<String> authErrorMessage;
    public MutableLiveData<Vehicle> vehicleData;
    public MutableLiveData<String> databaseErrorMessage;
    public MutableLiveData<Boolean> vehicleExists = new MutableLiveData<>();

    public ResultViewModel(@NonNull Application application) {
        super(application);
        dataVehiclesRepository = new DataVehiclesRepository();

        authErrorMessage = dataVehiclesRepository.authErrorMessage;
        vehicleData = dataVehiclesRepository.vehicleData;
        databaseErrorMessage = dataVehiclesRepository.databaseErrorMessage;
    }

    public void getVehicleDataByNumber(String vehicleLicense) {
        dataVehiclesRepository.getVehicleDataByNumber(vehicleLicense);
    }

    public void checkVehicleExists(String licensePlate) {
        dataVehiclesRepository.checkVehicleExists(licensePlate, new DataVehiclesRepository.OnExistenceCheckListener() {
            @Override
            public void onCheckResult(boolean exists) {
                vehicleExists.setValue(exists);
            }

            @Override
            public void onError(String errorMessage) {
                databaseErrorMessage.setValue(errorMessage);
                vehicleExists.setValue(false);
            }
        });
    }
}
