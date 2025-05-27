// LoginViewModel.java
package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.dkaverznev.carcamera.data.AuthRepository;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    private final MutableLiveData<Boolean> _loginSuccess = new MutableLiveData<>();
    public LiveData<Boolean> loginSuccess = _loginSuccess;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application.getApplicationContext());
    }

    public void login(String userName, String password) {
        String storedEmail = authRepository.getStoredEmail();
        String storedPassword = authRepository.getStoredPassword();

        if (storedEmail == null || storedPassword == null) {
            _errorMessage.setValue("Пользователь не зарегистрирован");
            _loginSuccess.setValue(false);
            return;
        }

        if (userName.equals(storedEmail) && password.equals(storedPassword)) {
            _loginSuccess.setValue(true);
            _errorMessage.setValue(null);
        } else {
            _loginSuccess.setValue(false);
            _errorMessage.setValue("Неверный адрес электронной почты или пароль.");
        }
    }
}
