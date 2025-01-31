package com.example.heartalarm20.model.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.heartalarm20.model.entities.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthHelper {

    private static final String TAG = "FirebaseAuthHelper";
    private final FirebaseAuth auth;
    private final FirebaseHelper firebaseHelper;
    public static String userID;

    // LiveData para registro y login
    private final MutableLiveData<Boolean> _isUserRegistered = new MutableLiveData<>();
    public LiveData<Boolean> isUserRegistered = _isUserRegistered;

    private final MutableLiveData<Boolean> _isUserLoggedIn = new MutableLiveData<>();
    public LiveData<Boolean> isUserLoggedIn = _isUserLoggedIn;

    private final MutableLiveData<String> _loginError = new MutableLiveData<>();
    public LiveData<String> loginError = _loginError;

    public FirebaseAuthHelper() {
//        if (FirebaseApp.getApps(context).isEmpty()) {
//            FirebaseApp.initializeApp(context);
//            Log.d(TAG, "Firebase inicializado dentro de FirebaseAuthHelper");
//        }
        auth = FirebaseAuth.getInstance();
        firebaseHelper = new FirebaseHelper(auth.getUid());
    }

    // 🔹 Registro de usuario con email, contraseña y datos adicionales
    public void registerUser(String email, String password, Usuario usuario) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userID = auth.getCurrentUser().getUid();
                        firebaseHelper.saveUserToFirestore(userID, usuario, new AuthRepository.RepositoryCallback<Boolean>() {
                            @Override
                            public void onSuccess(Boolean result) {
                                _isUserRegistered.postValue(true);
                               // FirebaseTokenHelper.obtenerYGuardarTokenFCM(userID);
                            }

                            @Override
                            public void onError(Boolean error) {
                                _isUserRegistered.postValue(false);
                            }
                        });
                    } else {
                        Log.e(TAG, "Error en registro: ", task.getException());
                        _isUserRegistered.postValue(false);
                    }
                });
    }

    // 🔹 Iniciar sesión con email y contraseña
    public void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Inicio de sesión exitoso");
                        _isUserLoggedIn.postValue(true);
//                        FirebaseTokenHelper.obtenerYGuardarTokenFCM(userID);
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
