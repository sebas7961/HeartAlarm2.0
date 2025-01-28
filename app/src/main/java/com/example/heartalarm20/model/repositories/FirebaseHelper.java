package com.example.heartalarm20.model.repositories;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.heartalarm20.MyApp;
import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private final FirebaseFirestore db;
    private final String userId;

    public FirebaseHelper(String userId) {
        FirebaseApp app = FirebaseApp.getInstance("HeartAlarmV2");
        db = FirebaseFirestore.getInstance(app);
        this.userId = userId;
        //userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
        //        FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
    }

    public void guardarContactos(List<ContactoEmergencia> contactos, ContactosRepository.RepositoryCallback<Void> callback) {
        if (userId == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        Map<String, Object> pacienteData = new HashMap<>();
        List<Map<String, Object>> contactosList = getMaps(contactos);

        pacienteData.put("ContactosEmergencia", contactosList);

        db.collection("Paciente")
                .document(userId)
                .update(pacienteData)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @NonNull
    private static List<Map<String, Object>> getMaps(List<ContactoEmergencia> contactos) {
        List<Map<String, Object>> contactosList = new java.util.ArrayList<>();

        for (ContactoEmergencia contacto : contactos) {
            Map<String, Object> contactoMap = new HashMap<>();
            contactoMap.put("IdContacto", contacto.getId());
            contactoMap.put("NumeroContacto", contacto.getNumero());
            contactoMap.put("NombreContacto", contacto.getNombre());
            contactoMap.put("Prioridad", contacto.getPrioridad());
            contactosList.add(contactoMap);
        }
        return contactosList;
    }

    public void obtenerContactos(ContactosRepository.RepositoryCallback<List<ContactoEmergencia>> callback) {
        if (userId == null) {
            callback.onError("Usuario no autenticado");
            return;
        }

        db.collection("Paciente")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> contactosList = (List<Map<String, Object>>) documentSnapshot.get("ContactosEmergencia");
                        List<ContactoEmergencia> contactos = new java.util.ArrayList<>();
                        if (contactosList != null) {
                            for (Map<String, Object> contacto : contactosList) {
                                contactos.add(new ContactoEmergencia(
                                        (String) contacto.get("IdContacto"),
                                        (String) contacto.get("NumeroContacto"),
                                        (String) contacto.get("NombreContacto"),
                                        (String) contacto.get("Prioridad")
                                ));
                            }
                        }
                        callback.onSuccess(contactos);
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void verificarContactosVigilantes(List<ContactoEmergencia> contactos, ContactosRepository.RepositoryCallback<List<ContactoEmergencia>> callback, Context context) {
        List<String> vigilantesUIDs = new ArrayList<>();
        List<ContactoEmergencia> contactosActualizados = new ArrayList<>(contactos);

        for (ContactoEmergencia contacto : contactos) {
            db.collection("vigilantes")
                    .whereEqualTo("numero", contacto.getNumero()) // Filtramos por número
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String uid = document.getId(); // El ID del documento es el UID del usuario
                                vigilantesUIDs.add(uid);

                                // Marcar al contacto como vigilante registrado
                                contacto.setVigilante(true);
                                contacto.setUidVigilante(uid);
                            }
                        }
                        // Llamar al callback con los datos actualizados
                        callback.onSuccess(contactosActualizados);
                        // Guardar los UID en SharedPreferences
                        guardarVigilantesLocalmente(vigilantesUIDs, context);
                    })
                    .addOnFailureListener(e -> callback.onError("Error al verificar contactos: " + e.getMessage()));
        }
    }

    private void guardarVigilantesLocalmente(List<String> vigilantesUIDs, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("vigilantesRegistrados", new HashSet<>(vigilantesUIDs));
        editor.apply();
    }
}
