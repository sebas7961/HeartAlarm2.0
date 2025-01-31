package com.example.heartalarm20.view.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.heartalarm20.R;
import com.example.heartalarm20.model.entities.Usuario;
import com.example.heartalarm20.viewmodel.LoginViewModel;
import com.example.heartalarm20.viewmodel.RegisterViewModel;
import com.google.firebase.FirebaseApp;

public class RegisterFragment extends Fragment {

    private RegisterViewModel registerViewModel;
    private EditText etName, etDni, etEmail, etPassword, etEmergencyContact, etRole;
    private ProgressBar progressBar;
    private RadioGroup roleGroup;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        if (FirebaseApp.getApps(requireContext()).isEmpty()) {
            FirebaseApp.initializeApp(requireContext());
            Log.e("asmdamsda", "gaaaaa");
            registerViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        } else {
            registerViewModel = new ViewModelProvider(requireActivity()).get(RegisterViewModel.class);
        }


        etName = view.findViewById(R.id.et_name);
        etDni = view.findViewById(R.id.et_dni);
        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        progressBar = view.findViewById(R.id.progressBar);
        Button btnRegister = view.findViewById(R.id.btn_register);
        roleGroup = view.findViewById(R.id.rg_roles);

        btnRegister.setOnClickListener(v -> registerUser());

        observeViewModel();

        return view;
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String role = getSelectedRole();

        if (validateInputs(name, dni, email, password, role)) {
            progressBar.setVisibility(View.VISIBLE);
            Usuario user = new Usuario(name, dni, email, password, role.equals("Paciente"));
            registerViewModel.registerUser(email, password, user);
        }
    }

    private String getSelectedRole() {
        int selectedId = roleGroup.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = getView().findViewById(selectedId);
            return selectedRadioButton.getText().toString();
        }
        return "";
    }

    private boolean validateInputs(String name, String dni, String email, String password, String role) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dni) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(role)) {
            Toast.makeText(getActivity(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void observeViewModel() {
        registerViewModel.isUserRegistered.observe(getViewLifecycleOwner(), isRegistered -> {
            progressBar.setVisibility(View.GONE);
            if (isRegistered) {
                Toast.makeText(getActivity(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigate(R.id.action_registerFragment_to_loginFragment);
            } else {
                Toast.makeText(getActivity(), "Error en el registro", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
