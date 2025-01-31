package com.example.heartalarm20.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.entities.Usuario;
import com.example.heartalarm20.model.repositories.FirebaseAuthHelper;

public class RegisterViewModel extends AndroidViewModel {

    private final FirebaseAuthHelper authRepository;
    public LiveData<Boolean> isUserRegistered;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        authRepository = new FirebaseAuthHelper();
        isUserRegistered = authRepository.isUserRegistered;
    }

    public void registerUser(String email, String password, Usuario usuario) {
        authRepository.registerUser(email, password, usuario);
    }
}
