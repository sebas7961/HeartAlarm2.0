package com.example.heartalarm20;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.example.heartalarm20.services.BluetoothService;
import com.example.heartalarm20.view.fragments.LoginFragment;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent serviceIntent = new Intent(this, BluetoothService.class);
        startService(serviceIntent);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
        Log.d("MainActivity", "onCreate ejecutado");

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("MainActivity", "ONSTAAAAAAART ejecutado");

        // Obtener la instancia personalizada de Firebase
        FirebaseApp customApp = FirebaseApp.getInstance("HeartAlarmV2");
        FirebaseAuth auth = FirebaseAuth.getInstance(customApp);
        FirebaseUser currentUser = auth.getCurrentUser();
        Log.d("MainActivity", "currentUser" +auth.getCurrentUser().getEmail());

        if (currentUser != null) {
            Log.d("MainActivity", "Usuario autenticado: " + currentUser.getEmail());

            FirebaseFirestore db = FirebaseFirestore.getInstance(customApp);

            // Primero busca en la colección "Paciente"
            db.collection("Paciente").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Log.d("MainActivity", "Usuario encontrado en la colección 'Paciente'");
                            startActivity(new Intent(this, PacienteActivity.class));
                            finish();
                        } else {
                            db.collection("Vigilante").document(currentUser.getUid()).get()
                                    .addOnSuccessListener(vigilanteSnapshot -> {
                                        if (vigilanteSnapshot.exists()) {
                                            Log.d("MainActivity", "Usuario encontrado en la colección 'Vigilante'");
                                            startActivity(new Intent(this, VigilanteActivity.class));
                                            finish();
                                        } else {
                                            // Si no está en ninguna de las dos colecciones, redirige al LoginFragment
                                            Log.d("MainActivity", "Usuario no encontrado en ninguna colección");
                                            navegarAlLoginFragment();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("MainActivity", "Error al buscar en la colección 'Vigilante'", e);
                                        navegarAlLoginFragment();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MainActivity", "Error al buscar en la colección 'Paciente'", e);
                        navegarAlLoginFragment();
                    });
        } else {
            Log.d("MainActivity", "No hay usuario autenticado");
            navegarAlLoginFragment();
        }
    }
    private void navegarAlLoginFragment() {
        Log.d("MainActivity", "Navegando al LoginFragment");
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment); // Reemplaza con tu contenedor
        navController.navigate(R.id.loginFragment); // Reemplaza con el ID real de tu LoginFragment en nav_graph.xml
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        return navHostFragment != null
                && navHostFragment.getNavController().navigateUp()
                || super.onSupportNavigateUp();
    }
}
