package com.example.heartalarm20;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PacienteActivity extends AppCompatActivity {

    @Override
    protected  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paciente);
    }
    @Override
    protected void onStart() {
        super.onStart();


        FirebaseApp appV2 = FirebaseApp.getInstance("HeartAlarmV2");
        FirebaseAuth auth = FirebaseAuth.getInstance(appV2);
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.d("PacienteActivity", "Usuario autenticado: " + currentUser.getEmail());
        } else {
            Log.d("PacienteActivity", "No hay usuario autenticado");
        }
    }


}
