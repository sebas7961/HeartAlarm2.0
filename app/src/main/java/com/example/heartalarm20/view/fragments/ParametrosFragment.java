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
        Button saveButton = view.findViewById(R.id.saveButton);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
        int minPulse = sharedPreferences.getInt("minPulse", 60);
        int maxPulse = sharedPreferences.getInt("maxPulse", 100);
        minPulsoConfig.setText(String.valueOf(minPulse));
        maxPulsoConfig.setText(String.valueOf(maxPulse));

        saveButton.setOnClickListener(v -> {
            String minPulseText = minPulsoConfig.getText().toString().trim();
            String maxPulseText = maxPulsoConfig.getText().toString().trim();

            if (minPulseText.isEmpty() || maxPulseText.isEmpty()) {
                Toast.makeText(getActivity(), "Por favor, complete ambos campos", Toast.LENGTH_SHORT).show();
            } else {
                int minPulseValue = Integer.parseInt(minPulseText);
                int maxPulseValue = Integer.parseInt(maxPulseText);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("minPulse", minPulseValue);
                editor.putInt("maxPulse", maxPulseValue);
                editor.apply();

                Toast.makeText(getActivity(), "Configuración guardada: Min: " + minPulseValue + ", Max: " + maxPulseValue, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
