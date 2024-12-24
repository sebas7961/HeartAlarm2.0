package com.example.heartalarm20.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.heartalarm20.R;

public class NumeroDeEmergenciaFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_numeroemergencia, container, false);

        EditText numeroEmergencia = view.findViewById(R.id.et_numeroEmergencia);
        Button guardado = view.findViewById(R.id.btn_Guardar);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        String numeroGuardado = sharedPreferences.getString("numeroEmergencia", "");
        numeroEmergencia.setText(numeroGuardado);

        guardado.setOnClickListener(v -> {
            String nuevoNumeroContacto = numeroEmergencia.getText().toString().trim();

            if (nuevoNumeroContacto.isEmpty()) {
                Toast.makeText(getActivity(), "Debe ingresar un número de emergencia", Toast.LENGTH_SHORT).show();
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("numeroEmergencia", nuevoNumeroContacto);
                editor.apply();

                Toast.makeText(getActivity(), "Número de emergencia guardado: " + nuevoNumeroContacto, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
