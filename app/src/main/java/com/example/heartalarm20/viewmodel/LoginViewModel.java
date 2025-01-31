package com.example.heartalarm20.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.repositories.FirebaseAuthHelper;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends AndroidViewModel {
    private final FirebaseAuthHelper authHelper;

    public LoginViewModel(Application application) {
        super(application);
        authHelper = new FirebaseAuthHelper();
    }

    // 🔹 Iniciar sesión
    public void loginUser(String email, String password) {
        authHelper.loginUser(email, password);
    }

    // 🔹 Cerrar sesión
    public void logoutUser() {
        authHelper.logoutUser();
    }

    // 🔹 Obtener estado de login
    public LiveData<Boolean> getLoginStatus() {
        return authHelper.isUserLoggedIn;
    }

    // 🔹 Obtener error de login
    public LiveData<String> getLoginError() {
        return authHelper.loginError;
    }

    // 🔹 Verificar si el usuario ya está autenticado
    public boolean isUserLoggedIn() {
        return authHelper.isUserLoggedIn();
    }

    // 🔹 Obtener usuario actual (si está autenticado)
    public FirebaseUser getCurrentUser() {
        return authHelper.getCurrentUser();
    }
}
