package com.dkaverznev.carcamera.data;


import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class DataVehiclesRepository {

    private final FirebaseFirestore db;
    private final MutableLiveData<Vehicle> _vehicleData = new MutableLiveData<>();
    public final LiveData<Vehicle> vehicleData = _vehicleData;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public final LiveData<String> errorMessage = _errorMessage;

    private ListenerRegistration vehicleListenerRegistration;

    public DataVehiclesRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public void getVehicleByLicensePlate(@NotNull String licensePlate) {
        _vehicleData.postValue(null);
        _errorMessage.postValue(null);


        if (vehicleListenerRegistration != null) {
            vehicleListenerRegistration.remove();
        }

        DocumentReference docRef = db.collection("vehicles").document(licensePlate);

        vehicleListenerRegistration = docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                _errorMessage.postValue(Objects.requireNonNull(e.getMessage()));
                _vehicleData.postValue(null);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Vehicle vehicle = documentSnapshot.toObject(Vehicle.class);
                if (vehicle != null) {
                    _vehicleData.postValue(vehicle);
                    _errorMessage.postValue(null);
                } else {
                    _errorMessage.postValue("Не удалось преобразовать документ в объект Vehicle.");
                    _vehicleData.postValue(null);
                }
            } else {
                _vehicleData.postValue(null);
                _errorMessage.postValue("Документ с номером '" + licensePlate + "' не найден.");
            }
        });
    }

    public void stopListeningForVehicles() {
        if (vehicleListenerRegistration != null) {
            vehicleListenerRegistration.remove();
            vehicleListenerRegistration = null;
        }
    }
}