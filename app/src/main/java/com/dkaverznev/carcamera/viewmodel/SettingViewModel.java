package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dkaverznev.carcamera.data.AuthRepository;
import com.dkaverznev.carcamera.data.scans.DataScansRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SettingViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final DataScansRepository dataScansRepository;

    public LiveData<Boolean> logoutSuccess;
    public LiveData<Boolean> deleteSuccess;
    public MutableLiveData<String> errorMessageAuth;
    public LiveData<String> errorMessageScan;
    public LiveData<FirebaseUser> firebaseUser;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(getApplication());
        dataScansRepository = new DataScansRepository();

        logoutSuccess = Transformations.map(authRepository.firebaseUser, Objects::isNull);
        deleteSuccess = dataScansRepository.deleteSuccess;
        errorMessageAuth = authRepository.authErrorMessage;
        errorMessageScan = dataScansRepository.errorMessage;
        firebaseUser = authRepository.firebaseUser;
    }

    public void logOut() {
        if (authRepository.isUserLoggedIn()) {
            authRepository.logoutUser();
        } else {
            authRepository.firebaseUser.setValue(null);
        }
    }

    public void deleteData() {
        if (authRepository.isUserLoggedIn()) {
            dataScansRepository.deleteAllUserScans();
        }
    }
}