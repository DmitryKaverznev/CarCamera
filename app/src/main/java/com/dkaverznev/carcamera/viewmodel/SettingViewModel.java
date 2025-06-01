package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.dkaverznev.carcamera.data.AuthRepository;
import java.util.Objects;

public class SettingViewModel extends AndroidViewModel {
    private final AuthRepository authRepository;

    public LiveData<Boolean> logoutSuccess;
    public LiveData<String> errorMessage;

    public SettingViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();

        logoutSuccess = Transformations.map(authRepository.firebaseUser, Objects::isNull);

        errorMessage = authRepository.authErrorMessage;
    }

    public void logOut() {
        if (authRepository.isUserLoggedIn()) {
            authRepository.logoutUser();
        } else {
            authRepository.authErrorMessage.setValue("Ошибка");
            authRepository.firebaseUser.setValue(null);
        }
    }
}