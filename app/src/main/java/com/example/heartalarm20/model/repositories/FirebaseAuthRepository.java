package com.example.heartalarm20.model.repositories;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.heartalarm20.model.entities.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseAuthRepository {

    private static final String TAG = "FirebaseAuthRepository";
    private final FirebaseAuth auth;
    private final FirebaseFirestore firestore;

    // LiveData para registro y login
    private final MutableLiveData<Boolean> _isUserRegistered = new MutableLiveData<>();
    public LiveData<Boolean> isUserRegistered = _isUserRegistered;

    private final MutableLiveData<Boolean> _isUserLoggedIn = new MutableLiveData<>();
    public LiveData<Boolean> isUserLoggedIn = _isUserLoggedIn;

    private final MutableLiveData<String> _loginError = new MutableLiveData<>();
    public LiveData<String> loginError = _loginError;

    public FirebaseAuthRepository() {
        FirebaseApp app = FirebaseApp.getInstance("HeartAlarmV2");
        auth = FirebaseAuth.getInstance(app);
        firestore = FirebaseFirestore.getInstance(app);
    }

    // 🔹 Registro de usuario con email, contraseña y datos adicionales
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

    // 🔹 Guardar usuario en Firestore según su rol (Paciente / Vigilante)
    private void saveUserToFirestore(String userId, Usuario usuario) {
        String collection = usuario.isPaciente() ? "Paciente" : "Vigilante";

        firestore.collection(collection).document(userId)
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

    // 🔹 Iniciar sesión con email y contraseña
    public void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Inicio de sesión exitoso");
                        _isUserLoggedIn.postValue(true);
                    } else {
                        Log.e(TAG, "Error en inicio de sesión", task.getException());
                        _loginError.postValue("Error en autenticación. Verifique su usuario y contraseña.");
                    }
                });
    }

    // 🔹 Cerrar sesión
    public void logoutUser() {
        auth.signOut();
        _isUserLoggedIn.postValue(false);
    }

    // 🔹 Obtener usuario actual
    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    // 🔹 Verificar si el usuario ya está autenticado
    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
}
