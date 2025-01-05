package com.example.heartalarm20.view.fragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.heartalarm20.R;
import com.example.heartalarm20.services.BluetoothService;
import com.example.heartalarm20.viewmodel.SharedViewModel;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class MonitoreoFragment extends Fragment {

    private static final String TAG = "MonitoreoFragment";

    private LineChart chart;
    private TextView tvAux;
    private Button btVolver, btTest;

    private BluetoothService bluetoothService;
    private boolean isBound = false;

    private SharedViewModel sharedViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitoreo, container, false);

        btTest = view.findViewById(R.id.bt_test_update);
        btTest.setOnClickListener(v -> {
            sharedViewModel.actualizarMonitoreoPulso((short) 75);
        });

        // Inicializar UI
        chart = view.findViewById(R.id.chart);
        tvAux = view.findViewById(R.id.tvAux);
        btVolver = view.findViewById(R.id.bt_back);

        // Configurar gráfico
        setupChart();

        // Inicializar ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar cambios en los datos
        observeLiveData();

        // Conectar al servicio Bluetooth
        Intent intent = new Intent(requireContext(), BluetoothService.class);
        requireActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        // Botón para volver
        btVolver.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    // 📊 Configurar el gráfico
    private void setupChart() {
        chart.setDrawGridBackground(false);
        chart.setTouchEnabled(false);
        chart.setPinchZoom(false);
        chart.getDescription().setEnabled(false);
        chart.setData(new LineData());

        // Configurar eje X
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);

        // Configurar eje Y
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setLabelCount(6, true);
        yAxisLeft.setAxisMaximum(1000f);
        yAxisLeft.setAxisMinimum(0f);
        yAxisLeft.setDrawGridLines(true);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);
    }

    // 📡 Observar datos desde SharedViewModel
    private void observeLiveData() {
        sharedViewModel.getMonitoreoPulso().observe(requireActivity(), pulse -> {
            if (pulse != null) {
                Log.d(TAG, "Pulso recibido: " + pulse);
                updateChart(pulse);
                tvAux.setText(String.valueOf(pulse));
            } else {
                Log.w(TAG, "Pulso es nulo.");
            }
        });

        sharedViewModel.getSignalStrength().observe(getViewLifecycleOwner(), signal -> {
            if (signal != null) {
                Log.d(TAG, "Señal recibida: " + signal);
            } else {
                Log.w(TAG, "Señal es nula.");
            }
        });
    }

    // 📈 Actualizar gráfico
    private void updateChart(float sensorValue) {
        Log.d(TAG, "Actualizando gráfico con valor: " + sensorValue);
        LineData data = chart.getData();

        if (data != null) {
            LineDataSet set = (LineDataSet) data.getDataSetByIndex(0);
            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), sensorValue), 0);
            data.notifyDataChanged();

            chart.notifyDataSetChanged();
            chart.setVisibleXRangeMaximum(150);
            chart.moveViewToX(data.getEntryCount());
        } else {
            Log.e(TAG, "Datos del gráfico son nulos.");
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "EKG Signal");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.VerdeEKG));
        set.setLineWidth(2f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setFillAlpha(65);
        set.setFillColor(getResources().getColor(R.color.VerdeEKG));
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        return set;
    }

    // 🔗 Conexión con el servicio Bluetooth
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            bluetoothService = binder.getService();
            isBound = true;
            bluetoothService.setSharedViewModel(sharedViewModel);
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
