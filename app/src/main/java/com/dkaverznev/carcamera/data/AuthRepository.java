package com.dkaverznev.carcamera.data;

import android.app.Application;
import android.content.Context;

import com.dkaverznev.carcamera.R;
import com.dkaverznev.carcamera.utils.ErrorFirebaseUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import androidx.lifecycle.MutableLiveData;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    public final MutableLiveData<FirebaseUser> firebaseUser = new MutableLiveData<>();

    public final MutableLiveData<String> authErrorMessage = new MutableLiveData<>();
    private final Context applicationContext;

    // Конструктор теперь принимает Application
    public AuthRepository(Application application) {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser.setValue(firebaseAuth.getCurrentUser());
        this.applicationContext = application.getApplicationContext();
    }

    public void registerUser(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            authErrorMessage.postValue(applicationContext.getString(R.string.error_empty_credentials));
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser.setValue(firebaseAuth.getCurrentUser());
                        authErrorMessage.setValue(null);
                    } else {
                        handleAuthError(task.getException());
                    }
                });
    }

    public void loginUser(String email, String password) {
        if (email == null || password == null || email.isEmpty() || password.isEmpty()) {
            authErrorMessage.postValue(applicationContext.getString(R.string.error_empty_credentials));
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseUser.setValue(firebaseAuth.getCurrentUser());
                        authErrorMessage.setValue(null);
                    } else {
                        handleAuthError(task.getException());
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void logoutUser() {
        firebaseAuth.signOut();
        firebaseUser.setValue(null);
    }

    private void handleAuthError(Exception exception) {
        String errorMessageText = applicationContext.getString(R.string.error_unknown);

        if (exception instanceof FirebaseAuthException) {
            FirebaseAuthException authException = (FirebaseAuthException) exception;
            String errorCode = authException.getErrorCode();
            errorMessageText = applicationContext.getString(ErrorFirebaseUtils.getString(errorCode));
        } else if (exception != null && exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            errorMessageText = applicationContext.getString(R.string.error_generic);
        }

        authErrorMessage.postValue(errorMessageText);
    }
}