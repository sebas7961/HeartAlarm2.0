package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.entities.Usuario;
import com.example.heartalarm20.model.repositories.FirebaseAuthRepository;

public class RegisterViewModel extends ViewModel {

    private final FirebaseAuthRepository authRepository;
    public LiveData<Boolean> isUserRegistered;

    public RegisterViewModel() {
        authRepository = new FirebaseAuthRepository();
        isUserRegistered = authRepository.isUserRegistered;
    }

    public void registerUser(String email, String password, Usuario usuario) {
        authRepository.registerUser(email, password, usuario);
    }
}
