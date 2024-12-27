package com.example.heartalarm20.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.heartalarm20.R;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button btnPrueba = view.findViewById(R.id.btn_prueba);

        btnPrueba.setOnClickListener(v ->
                Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_opcionesFragment)
        );

        return view;
    }

}