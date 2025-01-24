package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.repositories.FirebaseAuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {
    private final FirebaseAuthRepository authRepository;

    public LoginViewModel() {
        authRepository = new FirebaseAuthRepository();
    }

    // 🔹 Iniciar sesión
    public void loginUser(String email, String password) {
        authRepository.loginUser(email, password);
    }

    // 🔹 Cerrar sesión
    public void logoutUser() {
        authRepository.logoutUser();
    }

    // 🔹 Obtener estado de login
    public LiveData<Boolean> getLoginStatus() {
        return authRepository.isUserLoggedIn;
    }

    // 🔹 Obtener error de login
    public LiveData<String> getLoginError() {
        return authRepository.loginError;
    }

    // 🔹 Verificar si el usuario ya está autenticado
    public boolean isUserLoggedIn() {
        return authRepository.isUserLoggedIn();
    }

    // 🔹 Obtener usuario actual (si está autenticado)
    public FirebaseUser getCurrentUser() {
        return authRepository.getCurrentUser();
    }
}
