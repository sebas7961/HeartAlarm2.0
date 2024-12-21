package com.example.heartalarm20.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.heartalarm20.R;

public class GestionarPacientesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gestionarpacientes, container, false);
    }
    //faltalogicaadebatir
    //un propio fragment para los pacientes porque el botón de monitoreo será solo para el usuaario-paciente
    //Se podría reutilizar el MOniterio, pero creo que complica la relación de un vigilante a muchos pacientes.
    //Mejor el gestionar pacientes da un botón monitereo por paciente que tenga en su cuidado algo así.
    //mama say mama sa mama zakasa
}
