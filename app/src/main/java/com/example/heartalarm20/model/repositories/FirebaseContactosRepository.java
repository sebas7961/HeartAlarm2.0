package com.example.heartalarm20.model.repositories;

import android.content.Context;
import android.util.Log;

import com.example.heartalarm20.model.entities.ContactoEmergencia;

import java.util.List;


public class FirebaseContactosRepository implements ContactosRepository {
    private final FirebaseHelper firebaseHelper;

    public FirebaseContactosRepository() {
        this.firebaseHelper = new FirebaseHelper(FirebaseAuthHelper.userID);
    }

    @Override
    public void guardarContactos(List<ContactoEmergencia> contactos, RepositoryCallback<Void> callback) {
        firebaseHelper.guardarContactos(contactos, callback);
    }

    @Override
    public void obtenerContactos(RepositoryCallback<List<ContactoEmergencia>> callback) {
        firebaseHelper.obtenerContactos(callback);
    }

    @Override
    public void verificarContactosVigilantes(String numero, RepositoryCallback<String> callback, Context context) {
        Log.d("FirebaseContactos", numero);
        firebaseHelper.verificarContactosVigilantes(numero, callback, context);
    }

}
