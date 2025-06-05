package com.dkaverznev.carcamera.data.vehicles;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DataVehiclesRepository {

    private final FirebaseAuth firebaseAuth;

    private final MutableLiveData<String> _authErrorMessage = new MutableLiveData<>();
    public MutableLiveData<String> authErrorMessage = _authErrorMessage;

    private final FirebaseFirestore db;
    private final CollectionReference vehiclesCollection;
    private final MutableLiveData<Vehicle> _vehicleData = new MutableLiveData<>();
    public MutableLiveData<Vehicle> vehicleData = _vehicleData;

    private final MutableLiveData<String> _databaseErrorMessage = new MutableLiveData<>();
    public MutableLiveData<String> databaseErrorMessage = _databaseErrorMessage;


    public DataVehiclesRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        vehiclesCollection = db.collection("vehicles");

        firebaseAuth.addAuthStateListener(firebaseAuth -> {
            FirebaseUser currentUser = firebaseAuth.getCurrentUser();
            if (currentUser == null) {
                _vehicleData.setValue(null);
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
                        _databaseErrorMessage.setValue("Ошибка при получении данных: " + task.getException().getMessage());
                        _vehicleData.setValue(null);
                    }
                });
    }

    public interface OnExistenceCheckListener {
        void onCheckResult(boolean exists);
        void onError(String errorMessage);
    }

    public void checkVehicleExists(String vehicleLicense, OnExistenceCheckListener listener) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            listener.onError("Пользователь не авторизован.");
            return;
        }

        if (vehicleLicense == null || vehicleLicense.trim().isEmpty()) {
            listener.onError("Номер автомобиля не может быть пустым.");
            return;
        }

        vehiclesCollection.document(vehicleLicense)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        listener.onCheckResult(document.exists());
                    } else {
                        listener.onError("Ошибка при проверке существования: " + task.getException().getMessage());
                    }
                });
    }
}
