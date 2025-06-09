package com.dkaverznev.carcamera.data.vehicles;

import androidx.lifecycle.LiveData; // Изменено на LiveData
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.scans.ScanStatus; // Этот импорт может быть не нужен, если Vehicle сам обрабатывает статус
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class DataVehiclesRepository {

    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<String> _authErrorMessage = new MutableLiveData<>();
    public LiveData<String> authErrorMessage = _authErrorMessage; // Сделаем публичным LiveData

    private final CollectionReference vehiclesCollection;
    private final MutableLiveData<Vehicle> _vehicleData = new MutableLiveData<>();
    public LiveData<Vehicle> vehicleData = _vehicleData; // Сделаем публичным LiveData

    private final MutableLiveData<String> _databaseErrorMessage = new MutableLiveData<>();
    public LiveData<String> databaseErrorMessage = _databaseErrorMessage; // Сделаем публичным LiveData


    public DataVehiclesRepository() { // Конструктор снова без DataScansRepository
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        vehiclesCollection = db.collection("vehicles");

        firebaseAuth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                _vehicleData.setValue(null);
                _authErrorMessage.setValue("Пользователь не авторизован.");
            }
        });
    }

    public void getVehicleDataByNumber(String vehicleLicense) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            _authErrorMessage.setValue("Пользователь не авторизован.");
            _vehicleData.setValue(null);
            return;
        }

        if (vehicleLicense == null || vehicleLicense.trim().isEmpty()) {
            _databaseErrorMessage.setValue("Номер автомобиля не может быть пустым.");
            _vehicleData.setValue(null);
            return;
        }

        vehiclesCollection.document(vehicleLicense)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Vehicle vehicle = document.toObject(Vehicle.class);
                            _vehicleData.setValue(vehicle);
                            _databaseErrorMessage.setValue(null);
                        } else {
                            _vehicleData.setValue(null);
                            _databaseErrorMessage.setValue("Данные автомобиля по номеру " + vehicleLicense + " не найдены.");
                        }
                    } else {
                        _databaseErrorMessage.setValue("Ошибка при получении данных: " + Objects.requireNonNull(task.getException()).getMessage());
                        _vehicleData.setValue(null);
                    }
                });
    }
}