package com.example.heartalarm20.model.repositories;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.firestore.FirebaseFirestore;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

public class FirebaseTokenHelper {
    private static final String TAG = "FirebaseTokenHelper";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance(FirebaseApp.getInstance());

    public static void obtenerYGuardarTokenFCM(String userId, Context context) {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FirebaseTokenHelper", "Error al obtener el token FCM", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d("FirebaseTokenHelper", "Token FCM obtenido: " + token);

                    // 🔹 Guardar en Firestore
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> tokenMap = new HashMap<>();
                    tokenMap.put("fcmToken", token);

                    db.collection("Vigilante").document(userId)
                            .update(tokenMap)
                            .addOnSuccessListener(aVoid -> Log.d("FirebaseTokenHelper", "Token FCM guardado en Firestore"))
                            .addOnFailureListener(e -> Log.e("FirebaseTokenHelper", "Error al guardar token", e));
                });
    }



    public static void obtenerTokenDesdeFirestore(String userId, FirestoreCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Vigilante").document(userId)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists() && document.contains("fcmToken")) {
                        String token = document.getString("fcmToken");
                        callback.onCallback(token);
                    } else {
                        callback.onCallback(null);
                    }
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    public interface FirestoreCallback {
        void onCallback(String token);
    }

}