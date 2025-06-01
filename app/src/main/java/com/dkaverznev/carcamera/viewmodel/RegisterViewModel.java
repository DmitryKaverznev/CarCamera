package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dkaverznev.carcamera.data.AuthRepository;

import java.util.Objects;

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public LiveData<Boolean> registerSuccess;
    public LiveData<String> errorMessage;

    private final MutableLiveData<Boolean> _inputError = new MutableLiveData<>();

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository();

        registerSuccess = Transformations.map(authRepository.firebaseUser, Objects::nonNull);

        errorMessage = authRepository.authErrorMessage;
    }

    public void register(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _inputError.setValue(true);

            return;
        }

        if (!password.equals(confirmPassword)) {
            _inputError.setValue(true);
            return;
        }

        _inputError.setValue(false);
        authRepository.registerUser(email, password);
    }
}