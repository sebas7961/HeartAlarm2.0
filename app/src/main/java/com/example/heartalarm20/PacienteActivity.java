package com.example.heartalarm20;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.heartalarm20.model.repositories.FirebaseAuthHelper;
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
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            FirebaseAuthHelper.userID = currentUser.getUid();
            Log.d("PacienteActivity", "Usuario autenticado: " + currentUser.getEmail());
        } else {
            Log.d("PacienteActivity", "No hay usuario autenticado");
        }
    }


}
