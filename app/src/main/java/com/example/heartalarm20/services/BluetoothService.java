package com.example.heartalarm20.services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.heartalarm20.R;
import com.example.heartalarm20.view.activities.CallDialogActivity;
import com.example.heartalarm20.viewmodel.SharedViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluetoothService extends Service {

    private static final String TAG = "BluetoothService";
    private static final String ESP32_ADDRESS = "64:B7:08:29:32:42";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private BluetoothReceiverThread bluetoothReceiverThread;
    private MediaPlayer mediaPlayer;

    private final IBinder binder = new LocalBinder();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private SharedViewModel sharedViewModel;

    public class LocalBinder extends Binder{
        public BluetoothService getService(){
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        return binder;
    }

    public void setSharedViewModel(SharedViewModel viewModel){
        this.sharedViewModel = viewModel;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        mediaPlayer = MediaPlayer.create(this, R.raw.alarma);
        Log.d(TAG, "Bluetooth Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification = new NotificationCompat.Builder(this, "BluetoothServiceChannel")
                .setContentTitle("Heart Alarm")
                .setContentText("Te estamos cuidando")
                .setSmallIcon(R.drawable.baseline_monitor_heart_24)
                .build();

        startForeground(1, notification);
        Log.d(TAG, "Service started");
        return START_STICKY;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "BluetoothServiceChannel",
                    "Bluetooth Service Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public boolean connect() {
        if (!isBluetoothEnabled()) {
            Log.e(TAG, "Bluetooth is disabled or not available.");
            return false;
        }

        try {
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(ESP32_ADDRESS);
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothAdapter.cancelDiscovery();
            bluetoothSocket.connect();
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();

            startListeningForData();

            if (sharedViewModel != null) {
                sharedViewModel.setConexionBluetooth(true);
            }

            Log.d(TAG, "Connected to Bluetooth device");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to Bluetooth device", e);
            disconnect();
            return false;
        }
    }

    private boolean isBluetoothEnabled() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Bluetooth CONNECT permission not granted.");
                return false;
            }
        }

        return true;
    }

    public void disconnect() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (bluetoothSocket != null) bluetoothSocket.close();

            if (sharedViewModel != null) {
                sharedViewModel.setConexionBluetooth(false);
            }

            Log.d(TAG, "Bluetooth device disconnected");
        } catch (IOException e) {
            Log.e(TAG, "Error disconnecting Bluetooth device", e);
        }
    }

    public boolean sendData(String data) {
        if (outputStream != null) {
            try {
                outputStream.write(data.getBytes());
                return true;
            } catch (IOException e) {
                Log.e(TAG, "Error sending data", e);
                return false;
            }
        }
        return false;
    }

    private void startListeningForData() {
        if (bluetoothSocket != null) {
            bluetoothReceiverThread = new BluetoothReceiverThread(bluetoothSocket, this);
            bluetoothReceiverThread.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disconnect();
        executorService.shutdown();
        if (bluetoothReceiverThread != null && bluetoothReceiverThread.isAlive()) {
            bluetoothReceiverThread.interrupt();
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        Log.d(TAG, "Bluetooth Service destroyed");
    }



    private class BluetoothReceiverThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final Context mContext;

        public BluetoothReceiverThread(BluetoothSocket socket, Context context) {
            mmSocket = socket;
            mContext = context;
            InputStream tmpIn = null;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error getting InputStream", e);
            }
            mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (!interrupted()) {
                try {
                    bytes = mmInStream.read(buffer);
                    String incomingData = new String(buffer, 0, bytes);
                    Log.d(TAG, "Data received: " + incomingData);
                    processIncomingData(incomingData);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading data", e);
                    break;
                }
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            try {
                if (mmInStream != null) mmInStream.close();
                if (mmSocket != null) mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing Bluetooth resources", e);
            }
        }

        private void processIncomingData(String data) {
            Handler mainHandler = new Handler(Looper.getMainLooper());

            try {
                if (data == null || data.trim().isEmpty()) {
                    Log.w(TAG, "Datos vacíos o nulos recibidos desde Bluetooth.");
                    return;
                }

                if (data.contains("ALERTA: ")) {
                    String bpm = data.replace("ALERTA: ", "").trim();
                    mainHandler.post(() -> {
                        if (sharedViewModel != null) {
                            sharedViewModel.setAlerta(bpm);
                        }
                        mainHandler.post(() -> showCallDialog(bpm));
                        createNotification(data);
                        playAlertSound();
                    });
                    Log.d(TAG, "Alerta detectada: " + bpm);
                    return;
                }

                if (data.startsWith("Average BPM: ")) {
                    String bpmString = data.split("\n")[0].replaceAll("[^0-9]", "").trim();

                    if (!bpmString.isEmpty()) {
                        try {
                            short bpmValue = Short.parseShort(bpmString);
                            if (sharedViewModel != null) {
                                sharedViewModel.actualizarMonitoreoPulso(bpmValue);
                            }
                            updateFirestoreWithBPM(bpmValue);
                            Log.d(TAG, "BPM recibido y actualizado: " + bpmValue);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error: BPM no es un número válido -> " + bpmString, e);
                        }
                    } else {
                        Log.w(TAG, "BPM vacío o inválido: " + bpmString);
                    }
                    return;
                }

                if (data.contains("S: ")) {
                    String signalString = data.split("\n")[0].replaceAll("[^0-9]", "").trim();

                    if (!signalString.isEmpty()) {
                        try {
                            short pulseValue = Short.parseShort(signalString); // Convertir a Short
                            if (sharedViewModel != null) {
                                sharedViewModel.actualizarMonitoreoPulso(pulseValue); // Actualizar el pulso
                            }
                            Log.d(TAG, "Pulso recibido y actualizado desde señal: " + pulseValue);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error al convertir señal a Short: " + signalString, e);
                        }
                    } else {
                        Log.w(TAG, "Señal recibida vacía o no válida.");
                    }
                    return;
                }

                Log.w(TAG, "Datos desconocidos recibidos: " + data);

            } catch (Exception e) {
                Log.e(TAG, "Error al procesar los datos entrantes: " + data, e);
            }
        }

        private void showCallDialog(String data) {
            Intent dialogIntent = new Intent(getApplicationContext(), CallDialogActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            dialogIntent.putExtra("pulso", data);
            startActivity(dialogIntent);
        }

        private void createNotification(String data) {
            Intent intent = new Intent(getApplicationContext(), CallDialogActivity.class);
            intent.putExtra("pulso", data);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            PendingIntent pendingIntent = PendingIntent.getActivity(
                    BluetoothService.this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            NotificationCompat.Builder notification = new NotificationCompat.Builder(BluetoothService.this, "BluetoothServiceChannel")
                    .setContentTitle("Alerta de riesgo")
                    .setContentText("Se ha detectado un pulso anormal: " + data)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setSmallIcon(R.drawable.baseline_monitor_heart_24).setContentIntent(pendingIntent) // Asocia el PendingIntent
                    .setAutoCancel(true);

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.notify(2, notification.build());
            }
        }

        private void playAlertSound() {
            if (mediaPlayer != null) {
                mediaPlayer.start();
            }
        }

        private void updateFirestoreWithBPM(double bpmValue) {

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            try {
                firestore.collection("Paciente").document("72608730").get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        ArrayList<Double> bpms = (ArrayList<Double>) document.get("bpms");
                        if (bpms == null) {
                            bpms = new ArrayList<>();
                        }
                        bpms.add(bpmValue);
                        firestore.collection("Paciente").document("72608730").update("bpms", bpms)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "BPM actualizado con éxito"))
                                .addOnFailureListener(e -> Log.e(TAG, "Error al actualizar BPM", e));
                    } else {
                        Log.e(TAG, "Error al obtener documento", task.getException());
                    }
                });
            } catch (NumberFormatException e) {
                Log.e(TAG, "Formato de BPM inválido", e);
            }
        }
    }
}