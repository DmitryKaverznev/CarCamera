package com.dkaverznev.carcamera.data.scans;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DataScansRepository {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Scan> _scanData = new MutableLiveData<>();
    public LiveData<Scan> scanData = _scanData;

    private final MutableLiveData<List<Scan>> _allScans = new MutableLiveData<>();
    public LiveData<List<Scan>> allScans = _allScans;

    private final MutableLiveData<Boolean> _deleteSuccess = new MutableLiveData<>();
    public LiveData<Boolean> deleteSuccess = _deleteSuccess;

    public DataScansRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public String createDocumentIdInternal(String vehicleLicense, long unixTime) {
        return vehicleLicense + "_" + unixTime;
    }

    private Map<String, Object> parseDocumentId(String documentId) {
        Map<String, Object> parsedData = new HashMap<>();
        int lastUnderscore = documentId.lastIndexOf('_');
        if (lastUnderscore != -1) {
            String vehicleLicense = documentId.substring(0, lastUnderscore);
            try {
                long unixTime = Long.parseLong(documentId.substring(lastUnderscore + 1));
                parsedData.put("vehicleLicense", vehicleLicense);
                parsedData.put("unixTime", unixTime);
            } catch (NumberFormatException e) {
                Log.e("DataScansRepo", "Ошибка парсинга времени из ID документа: " + documentId, e);
            }
        } else {
            parsedData.put("vehicleLicense", documentId);
        }
        return parsedData;
    }

    public void addScan(Scan scan) {
        if (scan == null) {
            _errorMessage.setValue("Объект сканирования не может быть null.");
            return;
        }

        String vehicleLicense = scan.getVehicleLicense();
        String status = scan.getStatus().getStringFirebase();
        long unixTime = scan.getTime().getSeconds();
        String type = scan.getType();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            _errorMessage.setValue("Пользователь не авторизирован");
            return;
        }

        if (vehicleLicense == null || vehicleLicense.trim().isEmpty()) {
            _errorMessage.setValue("Номер пустой");
            return;
        }

        CollectionReference userScansCollection = db.collection("users")
                .document(currentUser.getUid())
                .collection("scans");

        Map<String, Object> scanDataMap = new HashMap<>();
        scanDataMap.put("status", status);
        scanDataMap.put("time", scan.getTime());
        scanDataMap.put("type", type);

        String documentId = createDocumentIdInternal(vehicleLicense, unixTime);

        userScansCollection.document(documentId)
                .set(scanDataMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _errorMessage.setValue(null);
                        _scanData.setValue(scan);
                    } else {
                        String error = (task.getException() != null) ? Objects.requireNonNull(task.getException()).getMessage() : "Неизвестная ошибка при добавлении сканирования.";
                        _errorMessage.setValue(error);
                        Log.wtf("DataScansRepo", error);
                    }
                });
    }

    public void getScan(String vehicleLicense) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            _errorMessage.setValue("Пользователь не авторизирован");
            return;
        }

        if (vehicleLicense == null || vehicleLicense.trim().isEmpty()) {
            _errorMessage.setValue("Номер пустой");
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("scans")
                .orderBy(FieldPath.documentId(), Query.Direction.DESCENDING)
                .whereGreaterThanOrEqualTo(FieldPath.documentId(), vehicleLicense + "_")
                .whereLessThan(FieldPath.documentId(), vehicleLicense + "`")
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot result = task.getResult();
                        if (result != null && !result.isEmpty()) {
                            DocumentSnapshot document = result.getDocuments().get(0);
                            Scan scan = document.toObject(Scan.class);
                            if (scan != null) {
                                Map<String, Object> parsedData = parseDocumentId(document.getId());
                                String parsedLicense = (String) parsedData.get("vehicleLicense");
                                scan.setVehicleLicense(Objects.requireNonNullElseGet(parsedLicense, document::getId));
                            }
                            _scanData.setValue(scan);
                            _errorMessage.setValue(null);
                        } else {
                            _errorMessage.setValue("Документ firebase пустой или не найден");
                            _scanData.setValue(null);
                        }
                    } else {
                        String error = (task.getException() != null) ? Objects.requireNonNull(task.getException()).getMessage() : "Неизвестная ошибка при получении сканирования.";
                        _errorMessage.setValue(error);
                        _scanData.setValue(null);
                    }
                });
    }

    public void getAllUserScans() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            _errorMessage.setValue("Пользователь не авторизирован");
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("scans")
                .orderBy("time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Scan> scansList = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            Scan scan = document.toObject(Scan.class);
                            if (scan != null) {
                                Map<String, Object> parsedData = parseDocumentId(document.getId());
                                String parsedLicense = (String) parsedData.get("vehicleLicense");
                                scan.setVehicleLicense(Objects.requireNonNullElseGet(parsedLicense, document::getId));
                            }
                            scansList.add(scan);
                        }
                        _allScans.setValue(scansList);
                        _errorMessage.setValue(null);
                    } else {
                        String error = (task.getException() != null) ? Objects.requireNonNull(task.getException()).getMessage() : "Неизвестная ошибка при получении всех сканирований";
                        _errorMessage.setValue(error);
                        _allScans.setValue(null);
                    }
                });
    }

    public void deleteAllUserScans() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            _errorMessage.setValue("Пользователь не авторизирован");
            _deleteSuccess.setValue(false);
            return;
        }

        CollectionReference userScansCollection = db.collection("users")
                .document(currentUser.getUid())
                .collection("scans");

        userScansCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                WriteBatch batch = db.batch();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    batch.delete(document.getReference());
                }
                batch.commit()
                        .addOnCompleteListener(batchTask -> {
                            if (batchTask.isSuccessful()) {
                                _errorMessage.setValue(null);
                                _deleteSuccess.setValue(true);
                                Log.d("DataScansRepo", "Все документы пользователя успешно удалены.");
                            } else {
                                String error = (batchTask.getException() != null) ? Objects.requireNonNull(batchTask.getException()).getMessage() : "Неизвестная ошибка при удалении всех документов.";
                                _errorMessage.setValue(error);
                                _deleteSuccess.setValue(false);
                            }
                        });
            } else {
                String error = (task.getException() != null) ? Objects.requireNonNull(task.getException()).getMessage() : "Неизвестная ошибка при получении документов для удаления.";
                _errorMessage.setValue(error);
                _deleteSuccess.setValue(false);
            }
        });
    }
}