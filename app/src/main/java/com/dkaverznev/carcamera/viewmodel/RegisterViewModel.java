// RegisterViewModel.java
package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.AuthRepository;

public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> _registerSuccess = new MutableLiveData<>();
    public LiveData<Boolean> registerSuccess = _registerSuccess;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application.getApplicationContext());
    }

    public void register(String username, String password, String confirmPassword) {
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            _errorMessage.setValue("Электронная почта или пароль не могут быть пустыми.");
            _registerSuccess.setValue(false);
            return;
        }

        if (!password.equals(confirmPassword)) {
            _errorMessage.setValue("Пароли не совпадают");
            _registerSuccess.setValue(false);
            return;
        }

        if (authRepository.isUserRegistered()) {
            _errorMessage.setValue("Пользователь уже зарегистрирован");
            _registerSuccess.setValue(false);
            return;
        }

        authRepository.saveUser(username, password);
        _registerSuccess.setValue(true);
        _errorMessage.setValue(null);
    }
}
