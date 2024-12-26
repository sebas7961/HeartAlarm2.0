package com.example.heartalarm20;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Inicializar Firebase por defecto
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        // Inicializar Firebase explícitamente para la versión 2
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:805316543999:android:2375f8cdc53ed8dbd113ca") // ID de la app (en google-services.json)
                    .setApiKey("AIzaSyB4tSMEtSX74pSkSx3jq43-Ntkkor0SYYE") // API Key (en google-services.json)
                    .setProjectId("heartalarm-2fa22") // ID del proyecto
                    .build();

            // Crear una instancia específica para la versión 2
            FirebaseApp.initializeApp(this, options, "HeartAlarmV2");

            Log.d("MyApp", "Firebase V2 inicializado correctamente");

        } catch (Exception e) {
            Log.e("MyApp", "Error al inicializar Firebase V2", e);
        }
    }
}
