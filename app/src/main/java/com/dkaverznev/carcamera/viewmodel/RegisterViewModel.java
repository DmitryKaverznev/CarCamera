package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.data.AuthRepository;

import java.util.Objects;

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public LiveData<Boolean> registerSuccess;
    public MutableLiveData<String> errorMessage;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);

        registerSuccess = Transformations.map(authRepository.firebaseUser, Objects::nonNull);

        errorMessage = authRepository.authErrorMessage;
    }

    public void register(String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage.postValue(getApplication().getString(R.string.error_empty_credentials));
            return;
        }

        if (!password.equals(confirmPassword)) {
            errorMessage.postValue(getApplication().getString(R.string.error_passwords_not_matching));
            return;
        }

        errorMessage.postValue(null);
        authRepository.registerUser(email, password);
    }
}