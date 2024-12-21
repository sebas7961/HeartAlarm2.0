package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class AuthViewModel extends ViewModel {
    private final MutableLiveData<String> rol = new MutableLiveData<>();


    public LiveData<String> getRol() {
        return rol;
    }
    public void setRol(String role){
        rol.setValue(role);
    }

}
