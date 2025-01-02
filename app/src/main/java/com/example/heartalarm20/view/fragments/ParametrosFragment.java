package com.example.heartalarm20.view.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartalarm20.R;
import com.example.heartalarm20.services.BluetoothService;
import com.example.heartalarm20.viewmodel.SharedViewModel;

public class ParametrosFragment extends Fragment {

    private static final String TAG = "ParametrosFragment";
    private BluetoothService bluetoothService;
    private boolean isBound = false;
    private SharedViewModel sharedViewModel;

    private EditText etMax, etMin;
    private Button btnGuardarYEnviar;

    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_parametros, container, false);

        etMax = view.findViewById(R.id.maxPulseInput);
        etMin = view.findViewById(R.id.minPulseInput);
        btnGuardarYEnviar = view.findViewById(R.id.saveButton);

        sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        loadPreviousValues();

        Intent intent = new Intent(requireContext(), BluetoothService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        btnGuardarYEnviar.setOnClickListener(v -> saveAndSendParametros());

        return view;
    }

    private void loadPreviousValues() {
        int minPulse = sharedPreferences.getInt("minPulso", 60);
        int maxPulse = sharedPreferences.getInt("maxPulso", 100);

        etMin.setText(String.valueOf(minPulse));
        etMax.setText(String.valueOf(maxPulse));

        sharedViewModel.setParametrosBajo((short) minPulse);
        sharedViewModel.setParametrosAlto((short) maxPulse);
    }

    private void saveAndSendParametros() {
        String minPulseText = etMin.getText().toString().trim();
        String maxPulseText = etMax.getText().toString().trim();

        if (minPulseText.isEmpty() || maxPulseText.isEmpty()) {
            Toast.makeText(getActivity(), "Debe completar ambos campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            short minPulse = Short.parseShort(minPulseText);
            short maxPulse = Short.parseShort(maxPulseText);

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("minPulso", minPulse);
            editor.putInt("maxPulso", maxPulse);
            editor.apply();

            sharedViewModel.setParametrosBajo(minPulse);
            sharedViewModel.setParametrosAlto(maxPulse);

            Toast.makeText(getActivity(), "Parámetros guardados correctamente", Toast.LENGTH_SHORT).show();

            // Enviar datos al ESP32
            sendDataToArduino(minPulse, maxPulse);

        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Valores inválidos. Ingrese números válidos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDataToArduino(short minPulse, short maxPulse) {
        if (isBound && bluetoothService != null) {

            String data = String.valueOf(minPulse) + String.valueOf(maxPulse);
            boolean success = bluetoothService.sendData(data);

            if (success) {
                Toast.makeText(getActivity(), "Parámetros enviados al ESP32", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Datos enviados: " + data);
            } else {
                Toast.makeText(getActivity(), "Error al enviar datos al ESP32", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error al enviar datos al ESP32: " + data);
            }
        } else {
            Toast.makeText(getActivity(), "Servicio Bluetooth no conectado", Toast.LENGTH_LONG).show();
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            bluetoothService.setSharedViewModel(sharedViewModel); // Asociar SharedViewModel
            Log.d(TAG, "Servicio Bluetooth vinculado correctamente");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            bluetoothService = null;
            Log.d(TAG, "Servicio Bluetooth desconectado");
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isBound) {
            requireActivity().unbindService(serviceConnection);
            isBound = false;
            Log.d(TAG, "Servicio Bluetooth desvinculado");
        }
    }
}
