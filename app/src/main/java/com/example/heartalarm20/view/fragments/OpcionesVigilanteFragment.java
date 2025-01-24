package com.example.heartalarm20.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.heartalarm20.R;
import com.example.heartalarm20.viewmodel.AuthViewModel;

public class OpcionesVigilanteFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opcionesvigilante, container, false);

        TextView roleText = view.findViewById(R.id.roleText);
        Button btnGestionarPacientes = view.findViewById(R.id.btn_GestionarPacientes);
        Button btnConectarDispositivo = view.findViewById(R.id.btn_ConectarDispositivo);

        /*
        //Opciones a g. pacientes
        btnGestionarPacientes.setOnClickListener(v ->{
                    Intent intent = new Intent(this.requireContext(), CallDialogActivity.class);
                    startActivity(intent);
                }
               // Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_gestionPacientesFragment)
        );

         */
        //Opciones a Conectar bluetooth
        btnConectarDispositivo.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_bluetoothtestFragment)
        );

        return view;
    }
}

