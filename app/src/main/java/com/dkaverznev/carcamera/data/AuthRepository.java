package com.dkaverznev.carcamera.data;

import androidx.lifecycle.MutableLiveData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class AuthRepository {

    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> _firebaseUser = new MutableLiveData<>();
    public MutableLiveData<FirebaseUser> firebaseUser = _firebaseUser;

    private final MutableLiveData<String> _authErrorMessage = new MutableLiveData<>();
    public MutableLiveData<String> authErrorMessage = _authErrorMessage;

    public AuthRepository() {
        firebaseAuth = FirebaseAuth.getInstance();
        _firebaseUser.setValue(firebaseAuth.getCurrentUser());
    }

    public void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _firebaseUser.setValue(firebaseAuth.getCurrentUser());
                        _authErrorMessage.setValue(null);
                    } else {
                        _authErrorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                        _firebaseUser.setValue(null);
                    }
                });
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        _firebaseUser.setValue(firebaseAuth.getCurrentUser());
                        _authErrorMessage.setValue(null);
                    } else {
                        _authErrorMessage.setValue(Objects.requireNonNull(task.getException()).getMessage());
                        _firebaseUser.setValue(null);
                    }
                });
    }

    public boolean isUserLoggedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void logoutUser() {
        firebaseAuth.signOut();
        _firebaseUser.setValue(null);
    }
}