package com.example.heartalarm20.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.example.heartalarm20.model.repositories.ContactosRepository;
import com.example.heartalarm20.model.repositories.FirebaseContactosRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContactosViewModel extends ViewModel {
    private final MutableLiveData<List<ContactoEmergencia>> contactosList = new MutableLiveData<>(new ArrayList<>());
    private final FirebaseContactosRepository repositoryContactos;
    private Context context;

    public ContactosViewModel() {
        this.repositoryContactos = new FirebaseContactosRepository();
        cargarContactos();
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Obtener lista de contactos
    public LiveData<List<ContactoEmergencia>> getContactosList() {
        return contactosList;
    }

    private void cargarContactos() {
        repositoryContactos.obtenerContactos(new ContactosRepository.RepositoryCallback<List<ContactoEmergencia>>() {
            @Override
            public void onSuccess(List<ContactoEmergencia> result) {
                contactosList.setValue(result);
                for(ContactoEmergencia contactoEmergencia : result){
                    verificarContactosVigilantes(contactoEmergencia.getNumero());
                }
            }

            @Override
            public void onError(String error) {
                Log.e("ContactosViewModel", "Error al obtener contactos");
            }
        });
    }

    public void agregarContacto(ContactoEmergencia contacto) {
        List<ContactoEmergencia> currentList = contactosList.getValue();
        contacto.setNumero(contacto.getNumero().replace(" ", ""));
        assert currentList != null;
        if (currentList.isEmpty()) {
            contacto.setPrioridad("Principal");
            SharedPreferences sharedPreferences = context.getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("numero", contacto.getNumero());
            editor.apply();
        }
        currentList.add(contacto);
        repositoryContactos.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                contactosList.setValue(new ArrayList<>(currentList)); // Clonar lista antes de setValue
                verificarContactosVigilantes(contacto.getNumero());
                Log.e("Contacto Guardado", "gogymgogym");
            }

            @Override
            public void onError(String error) {
                Log.e("ContactosViewModel", "Error al guardar contacto" + error);
            }
        });
    }

    public void actualizarContacto(Integer position, String newPriority) {
        List<ContactoEmergencia> currentList = new ArrayList<>(contactosList.getValue());
        if (newPriority.equals("Principal")) {
            ContactoEmergencia contactoEmergencia = currentList.get(position);
            for (ContactoEmergencia contacto : currentList) {
                if (contacto.getNumero().equals(contactoEmergencia.getNumero())) {
                    contacto.setPrioridad(newPriority);
                    SharedPreferences sharedPreferences = context.getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("numero", contacto.getNumero());
                    editor.apply();
                } else if (contacto.getPrioridad().equals("Principal")) {
                    // Cambiar el anterior principal a secundario
                    contacto.setPrioridad("Secundario");
                }
            }
        } else {
            currentList.get(position).setPrioridad(newPriority);
        }

        repositoryContactos.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                contactosList.setValue(new ArrayList<>(currentList)); // Clonar para garantizar LiveData update
            }

            @Override
            public void onError(String error) {
                Log.e("ContactosViewModel", "Error al actualizar contacto");
            }
        });
    }


    public void eliminarContacto(int index) {
        List<ContactoEmergencia> currentList = new ArrayList<>(contactosList.getValue());
        if (currentList != null && index >= 0 && index < currentList.size()) {
            currentList.remove(index);
            repositoryContactos.guardarContactos(currentList, new ContactosRepository.RepositoryCallback<Void>() {
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

    public void verificarContactosVigilantes(String numero) {
        Log.e("VerificacionContactosViewModel", numero);
        repositoryContactos.verificarContactosVigilantes(numero, new ContactosRepository.RepositoryCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("OnSuccessRepositoryVerificar", "result:" + result);

                guardarVigilantesLocalmente(result);
            }

            @Override
            public void onError(String error) {
                Log.e("ContactosNoVerificadosgg", error);
            }
        }, context);
    }

    private void guardarVigilantesLocalmente(String uidnuevo) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> vigilantesRegistrados =sharedPreferences.getStringSet("vigilantesRegistrados", new HashSet<>());
        List<String> olasi = new ArrayList<>(vigilantesRegistrados);
        olasi.add(uidnuevo);
        Log.e("asdasd", uidnuevo + olasi.size());
        editor.putStringSet("vigilantesRegistrados", new HashSet<>(olasi));
        editor.apply();
    }
}
