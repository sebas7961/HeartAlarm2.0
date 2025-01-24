package com.example.heartalarm20.model.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.heartalarm20.model.entities.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthRepository {

    private static final String TAG = "FirebaseAuthRepository";
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;
    private final MutableLiveData<Boolean> _isUserRegistered = new MutableLiveData<>();
    public LiveData<Boolean> isUserRegistered = _isUserRegistered;

    public FirebaseAuthRepository() {
        FirebaseApp app = FirebaseApp.getInstance("HeartAlarmV2");
        auth = FirebaseAuth.getInstance(app);
        firestore = FirebaseFirestore.getInstance(app);
    }

    public void registerUser(String email, String password, Usuario usuario) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String userId = auth.getCurrentUser().getUid();
                        saveUserToFirestore(userId, usuario);
                    } else {
                        Log.e(TAG, "Error en registro: ", task.getException());
                        _isUserRegistered.postValue(false);
                    }
                });
    }

    private void saveUserToFirestore(String userId, Usuario usuario) {
        if(usuario.isPaciente()){
            firestore.collection("Paciente").document(userId)
                    .set(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Usuario guardado en Firestore");
                        _isUserRegistered.postValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al guardar usuario en Firestore", e);
                        _isUserRegistered.postValue(false);
                    });
        }else {
            firestore.collection("Vigilante").document(userId)
                    .set(usuario)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Usuario guardado en Firestore");
                        _isUserRegistered.postValue(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al guardar usuario en Firestore", e);
                        _isUserRegistered.postValue(false);
                    });
        }
    }
}