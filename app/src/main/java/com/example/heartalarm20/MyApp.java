package com.example.heartalarm20;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase si no está inicializado
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
            Log.d("MyApp", "Firebase inicializado correctamente");
        } else {
            Log.d("MyApp", "Firebase ya estaba inicializado");
        }
    }
}
