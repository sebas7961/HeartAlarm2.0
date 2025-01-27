package com.example.heartalarm20.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.heartalarm20.R;
import com.example.heartalarm20.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnRegister;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // 🔹 Referencias UI
        etEmail = view.findViewById(R.id.et_usuarioLogin);
        etPassword = view.findViewById(R.id.et_contraseñaLogin);
        btnLogin = view.findViewById(R.id.btn_prueba);
        btnRegister = view.findViewById(R.id.btn_register);
        progressBar = view.findViewById(R.id.progressBar);

        // 🔹 Inicializar ViewModel
        loginViewModel = new ViewModelProvider(requireActivity()).get(LoginViewModel.class);

        // 🔹 Observar cambios en ViewModel
        observeViewModel(view);

        // 🔹 Botón de Registro
        btnRegister.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment));

        // 🔹 Botón de Inicio de Sesión
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(getActivity(), "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                loginViewModel.loginUser(email, password);
            }
        });

        return view;
    }

    // 🔹 Observar el ViewModel
    private void observeViewModel(View view) {
        // 🔹 Observa el estado del inicio de sesión
        loginViewModel.getLoginStatus().observe(getViewLifecycleOwner(), isSuccess -> {
            progressBar.setVisibility(View.GONE);
            if (isSuccess) {
                Toast.makeText(getActivity(), "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_pacienteActivity);
            }
        });

        // 🔹 Observa si hay errores en el inicio de sesión
        loginViewModel.getLoginError().observe(getViewLifecycleOwner(), errorMessage -> {
            if (!TextUtils.isEmpty(errorMessage)) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
