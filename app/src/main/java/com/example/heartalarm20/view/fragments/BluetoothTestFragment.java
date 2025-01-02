package com.example.heartalarm20.view.fragments;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartalarm20.R;
import com.example.heartalarm20.services.BluetoothService;
import com.example.heartalarm20.viewmodel.SharedViewModel;

public class BluetoothTestFragment extends Fragment {

    private static final int BLUETOOTH_PERMISSION_REQUEST_CODE = 1001;
    private BluetoothService bluetoothService;
    private boolean isServiceBound = false;
    private SharedViewModel sharedViewModel;

    private TextView statusBluetooth;
    private TextView statusBPM;
    private TextView statusAlert;
    private Button buttonConnect;
    private Button buttonDisconnect;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) iBinder;
            bluetoothService = binder.getService();
            bluetoothService.setSharedViewModel(sharedViewModel);
            isServiceBound = true;
            Toast.makeText(requireContext(), "Servicio Bluetooth vinculado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothService = null;
            isServiceBound = false;
            Toast.makeText(requireContext(), "Servicio Bluetooth desvinculado", Toast.LENGTH_SHORT).show();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetoothtest, container, false);

        // Inicializar UI
        statusBluetooth = view.findViewById(R.id.statusBluetooth);
        statusBPM = view.findViewById(R.id.statusBPM);
        statusAlert = view.findViewById(R.id.statusAlert);
        buttonConnect = view.findViewById(R.id.buttonConnect);
        buttonDisconnect = view.findViewById(R.id.buttonDisconnect);

        // Inicializar ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar cambios en el SharedViewModel
        setupObservers();

        // Configurar botones
        buttonConnect.setOnClickListener(v -> checkBluetoothPermissions());
        buttonDisconnect.setOnClickListener(v -> disconnectBluetooth());

        // Iniciar el servicio Bluetooth
        Intent serviceIntent = new Intent(requireContext(), BluetoothService.class);
        requireActivity().bindService(serviceIntent, serviceConnection, requireContext().BIND_AUTO_CREATE);

        return view;
    }

    private void checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        requireActivity(),
                        new String[]{
                                Manifest.permission.BLUETOOTH_CONNECT,
                                Manifest.permission.BLUETOOTH_SCAN
                        },
                        BLUETOOTH_PERMISSION_REQUEST_CODE
                );
            } else {
                connectBluetooth();
            }
        } else {
            // Para versiones anteriores a Android 12
            connectBluetooth();
        }
    }

    private void setupObservers() {
        // Estado Bluetooth
        sharedViewModel.conexionBluetooth.observe(getViewLifecycleOwner(), conectado -> {
            statusBluetooth.setText(conectado ? "Estado Bluetooth: Conectado" : "Estado Bluetooth: Desconectado");
        });

        // BPM
        sharedViewModel.monitoreoPulso.observe(getViewLifecycleOwner(), bpm -> {
            statusBPM.setText("BPM: " + bpm);
        });

        // Alerta
        sharedViewModel.alerta.observe(getViewLifecycleOwner(), alerta -> {
            statusAlert.setText("Alerta: " + alerta);
        });
    }

    private void connectBluetooth() {
        if (isServiceBound && bluetoothService != null) {
            boolean success = bluetoothService.connect();
            if (success) {
                sharedViewModel.setConexionBluetooth(true);
            } else {
                sharedViewModel.setConexionBluetooth(false);
            }
        } else {
            Toast.makeText(requireContext(), "El servicio Bluetooth no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    private void disconnectBluetooth() {
        if (isServiceBound && bluetoothService != null) {
            bluetoothService.disconnect();
            sharedViewModel.setConexionBluetooth(false);
        } else {
            Toast.makeText(requireContext(), "El servicio Bluetooth no está disponible.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isServiceBound) {
            isServiceBound = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isServiceBound) {
            requireActivity().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

}
