package com.example.heartalarm20.model.repositories;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
}
