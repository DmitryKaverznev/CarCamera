package com.dkaverznev.carcamera.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import com.dkaverznev.carcamera.data.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;

    public MutableLiveData<FirebaseUser> loginSuccess;
    public MutableLiveData<String> errorMessage;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        authRepository = new AuthRepository(application);

        loginSuccess = authRepository.firebaseUser;
        errorMessage = authRepository.authErrorMessage;
    }

    public void login(String email, String password) {
        authRepository.loginUser(email, password);
    }
}