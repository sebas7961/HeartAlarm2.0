package com.example.heartalarm20.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartalarm20.R;
import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.example.heartalarm20.view.adapters.ContactoAdapter;
import com.example.heartalarm20.viewmodel.ContactosViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ContactosDeEmergenciaFragment extends Fragment{

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> contactPickerLauncher;

    private RecyclerView recyclerView;
    private ContactoAdapter adapter;
    private static final ContactosViewModel contactosVM = new ContactosViewModel();
    private String lastPriority  = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializar permisos
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        pickContact();
                    } else {
                        Toast.makeText(requireContext(), "Permiso denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Inicializar selector de contactos
        contactPickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri contactUri = result.getData().getData();
                        getContactDetails(contactUri);
                    }
                }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactosemergencia, container, false);

        FloatingActionButton fbt_add_contacto = view.findViewById(R.id.fbt_agregarcontacto);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);

        fbt_add_contacto.setOnClickListener(v -> {
            requestContactsPermission();
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        contactosVM.setContext(this.requireContext());

        recyclerView = view.findViewById(R.id.rv_contactos);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new ContactoAdapter(requireContext(), new ArrayList<>());

        adapter.setListener(new ContactoAdapter.OnContactoInteractionListener() {
            @Override
            public void onDeleteContact(int position) {
                if(contactosVM.getContactosList().getValue().get(position).getPrioridad().equals("Principal")){
                    Toast.makeText(ContactosDeEmergenciaFragment.this.requireContext(), "Primero asigne un nuevo contacto principal", Toast.LENGTH_SHORT).show();
                    Log.e("SelectedItem", "NO se hace el acmio, problema del toast");
                    return;
                }
                contactosVM.eliminarContacto(position);
            }

            @Override
            public void onPriorityChanged(int position, String newPriority) {
                if(lastPriority==null){
                    contactosVM.actualizarContacto(position, newPriority);
                } else {
                    Log.e("SelectedItem", "NO se hace el acmio, problema del toast");
                    Toast.makeText(ContactosDeEmergenciaFragment.this.requireContext(), "Primero debe asignar otro contacto como principal", Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        contactosVM.getContactosList().observe(getViewLifecycleOwner(), contactos -> {
            //actualizar la lista en el adapter
            adapter.updateContactList(contactos);
        });
    }


    //Métodos para obtener contactos desde el gestor de contactos nativo
    private void requestContactsPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        } else {
            pickContact();
        }
    }

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        contactPickerLauncher.launch(intent);
    }

    private void getContactDetails(Uri contactUri) {
        String id = null, name = null, phoneNumber = null;

        // Obtener ID y nombre del contacto
        Cursor cursor = requireActivity().getContentResolver().query(contactUri,
                new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
            cursor.close();
        }
        String idAux = id;
        // Obtener número y nombre de contacto
        Cursor phoneCursor = requireActivity().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                },
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id}, null);

        ContactoEmergencia contactoEmergencia = null;

        if (phoneCursor != null && phoneCursor.moveToFirst()) {
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name = phoneCursor.getString(phoneCursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneCursor.close();
            contactoEmergencia = new ContactoEmergencia(idAux, phoneNumber, name);
        }
        // Guardar en la base de datos
        contactosVM.agregarContacto(contactoEmergencia);
    }

    @Override
    public void onResume(){
        super.onResume();
    }
}