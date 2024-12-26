package com.example.heartalarm20.viewmodel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.example.heartalarm20.model.repositories.ContactosRepository;
import com.example.heartalarm20.model.repositories.FirebaseContactosRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ContactosViewModel extends ViewModel {
    private final MutableLiveData<List<ContactoEmergencia>> contactosList = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseContactosRepository repository;

    public ContactosViewModel() {
        this.repository  = new FirebaseContactosRepository();
        cargarContactos();
    }

    // Obtener lista de contactos
    public LiveData<List<ContactoEmergencia>> getContactosList() {
        return contactosList;
    }

    private void cargarContactos() {
        repository.obtenerContactos(new ContactosRepository.RepositoryCallback<List<ContactoEmergencia>>() {
            @Override
            public void onSuccess(List<ContactoEmergencia> result) {
                contactosList.setValue(result);
            }

            @Override
            public void onError(String error) {
                Log.e("ContactosViewModel", "Error al obtener contactos");
            }
        });
    }

    public void agregarContacto(ContactoEmergencia contacto) {
        List<ContactoEmergencia> currentList = new ArrayList<>(contactosList.getValue());
        if (currentList != null) {
            currentList.add(contacto);
            repository.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    contactosList.setValue(new ArrayList<>(currentList)); // Clonar lista antes de setValue
                }

                @Override
                public void onError(String error) {
                    Log.e("ContactosViewModel", "Error al guardar contacto");
                }
            });
        }
    }

    public void actualizarContacto(int index, String newPriority) {
        List<ContactoEmergencia> currentList = new ArrayList<>(contactosList.getValue());
        if (currentList != null && index >= 0 && index < currentList.size()) {
            ContactoEmergencia contacto = currentList.get(index);
            contacto.setPrioridad(newPriority); // Actualizar prioridad directamente

            repository.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    contactosList.setValue(new ArrayList<>(currentList)); // Clonar lista antes de setValue
                }

                @Override
                public void onError(String error) {
                    Log.e("ContactosViewModel", "Error al actualizar contacto");
                }
            });
        }
    }

    public void eliminarContacto(int index) {
        List<ContactoEmergencia> currentList = new ArrayList<>(contactosList.getValue());
        if (currentList != null && index >= 0 && index < currentList.size()) {
            currentList.remove(index);
            repository.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    contactosList.setValue(new ArrayList<>(currentList)); // Clonar lista antes de setValue
                }

                @Override
                public void onError(String error) {
                    Log.e("ContactosViewModel", "Error al eliminar contacto");
                }
            });
        }
    }

}
