package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dkaverznev.carcamera.data.AuthRepository;
import java.util.Objects;

public class SettingViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public LiveData<Boolean> logoutSuccess;
    public MutableLiveData<String> errorMessage;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(getApplication());

        logoutSuccess = Transformations.map(authRepository.firebaseUser, Objects::isNull);

        errorMessage = authRepository.authErrorMessage;
    }

    public void logOut() {
        if (authRepository.isUserLoggedIn()) {
            authRepository.logoutUser();
        } else {
            authRepository.firebaseUser.setValue(null);
        }
    }
}