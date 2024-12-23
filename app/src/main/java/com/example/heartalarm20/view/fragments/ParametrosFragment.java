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

public class ParametrosFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parametros, container, false);

        EditText minPulsoConfig = view.findViewById(R.id.minPulseInput);
        EditText maxPulsoConfig = view.findViewById(R.id.maxPulseInput);
        Button btnGuardar = view.findViewById(R.id.saveButton);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        int minPulse = sharedPreferences.getInt("minPulso", 60);
        int maxPulse = sharedPreferences.getInt("maxPulso", 100);
        minPulsoConfig.setText(String.valueOf(minPulse));
        maxPulsoConfig.setText(String.valueOf(maxPulse));

        btnGuardar.setOnClickListener(v -> {
            String minPulsoText = minPulsoConfig.getText().toString().trim();
            String maxPulsoText = maxPulsoConfig.getText().toString().trim();

            if (minPulsoText.isEmpty() || maxPulsoText.isEmpty()) {
                Toast.makeText(getActivity(), "Debe Completar ambos campos", Toast.LENGTH_SHORT).show();
            } else {
                int minValorPulso = Integer.parseInt(minPulsoText);
                int maxValorPulso = Integer.parseInt(maxPulsoText);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("minPulso", minValorPulso);
                editor.putInt("maxPulso", maxValorPulso);
                editor.apply();

                Toast.makeText(getActivity(), "Configuración guardada: Min: " + minValorPulso + ", Max: " + maxValorPulso, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
