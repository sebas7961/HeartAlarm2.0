package com.example.heartalarm20.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.heartalarm20.model.api.EventoNotificacion;
import com.example.heartalarm20.model.repositories.FirebaseTokenHelper;
import com.example.heartalarm20.viewmodel.AuthViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpcionesPacienteFragment extends Fragment {

    private AuthViewModel authViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_opcionespaciente, container, false);

       // authViewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        TextView roleText = view.findViewById(R.id.roleText);
        Button btnMonitoreo = view.findViewById(R.id.btn_monitoreo);
       // Button btnGestionarPacientes = view.findViewById(R.id.btn_gestionar_pacientes);
        Button btnParametros = view.findViewById(R.id.btn_parametros);
        Button btnNEmergencia = view.findViewById(R.id.btn_NEmergencia);
        Button btnConectarDispositivo = view.findViewById(R.id.btn_ConectarDispositivo);
        Button btnNotificacion = view.findViewById(R.id.btn_notificador);
        btnNotificacion.setOnClickListener(v->notificar());
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
        /*
        //Opciones a g. pacientes
        btnGestionarPacientes.setOnClickListener(v ->{
                    Intent intent = new Intent(this.requireContext(), CallDialogActivity.class);
                    startActivity(intent);
                }
               // Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_gestionPacientesFragment)
        );

         */
        //Opciones a NumeroEmergencia
        btnNEmergencia.setOnClickListener(v ->
               Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_contactoEmergenciaFragment)
        );
        //Opciones a Conectar bluetooth
        btnConectarDispositivo.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_opcionesFragment_to_bluetoothtestFragment)
        );

        return view;
    }

    public void notificar(){
        Log.d("NotificacionesOpciones", "holasiprobando");
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        Set<String> vigilantesRegistrados =sharedPreferences.getStringSet("vigilantesRegistrados", new HashSet<>());
        List<String> tokens=new ArrayList<>();
        int b = 0;
        Log.d("NotificacionesOpciones", "Size: "+vigilantesRegistrados.size());
        for(String userID : vigilantesRegistrados){
            FirebaseTokenHelper.obtenerTokenDesdeFirestore(userID, new FirebaseTokenHelper.FirestoreCallback() {
                @Override
                public void onCallback(String token) {
                    tokens.add(token);
                    if(vigilantesRegistrados.size()==b+1){
                        Log.d("NotificacionesOpcionesOnCallbak", "holasiprobando444");
                        EventoNotificacion.enviarEvento("alerta", "holabola", tokens);
                        //NotificationSender.enviarNotificacion(tokens, "Alerta prueba", "noseah", getContext());
                    }
                }
            });
        }

    }
}
