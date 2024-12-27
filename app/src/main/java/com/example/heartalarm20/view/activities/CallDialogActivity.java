package com.example.heartalarm20.view.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telecom.Call;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.widget.Toast;

import com.example.heartalarm20.MainActivity;
import com.example.heartalarm20.R;

public class CallDialogActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_call_dialog);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        DisplayMetrics medidas = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidas);

        int ancho = medidas.widthPixels;
        int alto = medidas.heightPixels;

        getWindow().setLayout((int)(ancho*0.85), (int)(alto*0.4));

        Button bt_call = findViewById(R.id.bt_call);

        bt_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makePhoneCall();
            }
        });

        Button bt_false_alarm = findViewById(R.id.bt_false_alarm);
        bt_false_alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CallDialogActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makePhoneCall() {
        // Verificar si se tiene el permiso de llamada
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // Si no se tiene el permiso, solicitarlo
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else {
            // Si se tiene el permiso, iniciar la llamada
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            SharedPreferences sharedPreferences = this.getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
            String num = sharedPreferences.getString("numero", "911");
            if(num.equals("911")){
                Toast.makeText(this, "Llamar al 911", Toast.LENGTH_SHORT).show();
            }
            callIntent.setData(Uri.parse("tel:"+num)); // Cambia este número según sea necesario
            startActivity(callIntent);

        }
    }

    // Manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(); // Si el permiso es otorgado, realizar la llamada
            } else {
                Toast.makeText(this, "Permiso de llamada denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Anular el registro del BroadcastReceiver para evitar memory leaks
        //unregisterReceiver(receiver);
    }

}