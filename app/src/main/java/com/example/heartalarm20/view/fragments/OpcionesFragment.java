package com.example.heartalarm20.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.heartalarm20.R;
import com.example.heartalarm20.view.activities.CallDialogActivity;
import com.example.heartalarm20.viewmodel.AuthViewModel;

public class OpcionesFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opciones, container, false);

       // authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        TextView roleText = view.findViewById(R.id.roleText);
        Button btnMonitoreo = view.findViewById(R.id.btn_monitoreo);
        Button btnGestionarPacientes = view.findViewById(R.id.btn_gestionar_pacientes);
        Button btnParametros = view.findViewById(R.id.btn_parametros);
        Button btnNEmergencia = view.findViewById(R.id.btn_NEmergencia);
/*
        authViewModel.getRol().observe(getViewLifecycleOwner(), rol -> {
            if ("Paciente".equals(rol)) {
                roleText.setText("Opciones para Paciente");
                btnGestionarPacientes.setVisibility(View.GONE);
            } else if ("Vigilante".equals(rol)) {
                roleText.setText("Opciones para Vigilante");
                btnMonitoreo.setVisibility(View.GONE);
            }
        });

 */
        //Para ir de opcioes a parametros botoncito
        btnParametros.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_parametrosFragment)
        );
        //Opciones a monitoreo
        btnMonitoreo.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_monitoreoFragment)
        );
        //Opciones a g. pacientes
        btnGestionarPacientes.setOnClickListener(v ->{
                    Intent intent = new Intent(this.requireContext(), CallDialogActivity.class);
                    startActivity(intent);
                }
               // Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_gestionPacientesFragment)
        );
        //Opciones a NumeroEmergencia
        btnNEmergencia.setOnClickListener(v ->
               Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_numeroEmergenciaFragment)
        );

        return view;
    }
}
