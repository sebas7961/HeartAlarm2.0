package com.example.heartalarm20.model.repositories;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.heartalarm20.MyApp;
import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.example.heartalarm20.model.entities.Usuario;
import com.example.heartalarm20.model.entities.UsuarioBD;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class FirebaseHelper {
    private final FirebaseFirestore db;
    private final String userId;

    public FirebaseHelper(String userId) {
        db = FirebaseFirestore.getInstance();
        this.userId = FirebaseAuthHelper.userID;
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
                        if(documentSnapshot.contains("ContactosEmergencia")){
                            List<Map<String, Object>> contactosList = (List<Map<String,java.lang.Object>>) documentSnapshot.get("ContactosEmergencia");
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
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    public void verificarContactosVigilantes(String numero, ContactosRepository.RepositoryCallback<String> callback, Context context) {
        Log.e("FirebaseHelper", "número: "+numero);
        db.collection("Vigilante")
                .whereEqualTo("numero", numero) // Filtramos por número
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Log.e("FirebaseHelperOnSuccess", "ID del número en Firestore: " + document.getId());
                            callback.onSuccess(document.getId()); // El ID del documento es el UID del usuario
                        }
                    }
                })
                .addOnFailureListener(e -> callback.onError("Error al verificar contactos: " + e.getMessage()));

    }

    // 🔹 Guardar usuario en Firestore según su rol (Paciente / Vigilante)
    public void saveUserToFirestore(String userId, Usuario usuario, AuthRepository.RepositoryCallback<Boolean> callback) {
        String collection = usuario.isPaciente() ? "Paciente" : "Vigilante";

        UsuarioBD usuarioBD = new UsuarioBD(usuario);
        db.collection("Usuarios").document(userId).set(usuarioBD).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Usuario guardado en Firestore Usuario");
            }
        });

        db.collection(collection).document(userId)
                .set(usuario)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Usuario guardado en Firestore");
                    callback.onSuccess(true);

                    //_isUserRegistered.postValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al guardar usuario en Firestore", e);
                    callback.onError(false);
                    //_isUserRegistered.postValue(false);
                });

        //GUARDAR USUARIO?
    }


}
