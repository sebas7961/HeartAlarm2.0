package com.example.heartalarm20.model.repositories;

import com.example.heartalarm20.model.entities.ContactoEmergencia;

import java.util.List;

public interface ContactosRepository {
    void guardarContactos(List<ContactoEmergencia> contactos, RepositoryCallback<Void> callback);
    void obtenerContactos(RepositoryCallback<List<ContactoEmergencia>> callback);

    interface RepositoryCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
}

