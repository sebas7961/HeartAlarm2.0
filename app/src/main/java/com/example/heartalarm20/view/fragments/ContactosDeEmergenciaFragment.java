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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartalarm20.MainActivity;
import com.example.heartalarm20.R;
import com.example.heartalarm20.model.entities.ContactoEmergencia;
import com.example.heartalarm20.view.adapters.ContactoAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ContactosDeEmergenciaFragment extends Fragment implements ContactoAdapter.OnContactoInteractionListener{

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> contactPickerLauncher;

    private RecyclerView recyclerView;
    private ContactoAdapter adapter;
    private List<ContactoEmergencia> contactos;

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
    public void onDeleteContact(int position) {
        contactos.remove(position);
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onPriorityChanged(int position, String newPriority) {
        ContactoEmergencia contacto = contactos.get(position);
        contacto.setPrioridad(newPriority);
        // Aquí puedes agregar lógica para actualizar la base de datos
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contactosemergencia, container, false);

        FloatingActionButton fbt_add_contacto = view.findViewById(R.id.fbt_agregarcontacto);

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);

        fbt_add_contacto.setOnClickListener(v -> {
            requestContactsPermission();
        });


        //Configurar RecyclerView
        recyclerView = view.findViewById(R.id.rv_contactos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.requireContext()));

        contactos = new ArrayList<>();
        contactos.add(new ContactoEmergencia("1", "Juan Pérez", "+123456789"));
        contactos.add(new ContactoEmergencia("2", "Ana López", "+987654321"));

        adapter = new ContactoAdapter(this.requireContext(), contactos, this);
        recyclerView.setAdapter(adapter);

        return view;
    }

//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_contactosemergencia, container, false);
//
//        FloatingActionButton fbt_add_contacto = view.findViewById(R.id.fbt_agregarcontacto);
//
//        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("HeartAlarmPrefs", Context.MODE_PRIVATE);
//        String numeroGuardado = sharedPreferences.getString("numeroEmergencia", "");
//        numeroEmergencia.setText(numeroGuardado);
//
//        fbt_add_contacto.setOnClickListener(v -> {
//            String nuevoNumeroContacto = numeroEmergencia.getText().toString().trim();
//
//            if (nuevoNumeroContacto.isEmpty()) {
//                Toast.makeText(getActivity(), "Debe ingresar un número de emergencia", Toast.LENGTH_SHORT).show();
//            } else {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("numeroEmergencia", nuevoNumeroContacto);
//                editor.apply();
//
//                Toast.makeText(getActivity(), "Número de emergencia guardado: " + nuevoNumeroContacto, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        return view;
//    }

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
            name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }

        // Obtener número de teléfono
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
            contactoEmergencia = new ContactoEmergencia(id, phoneNumber, name);
        }

        // Guardar en la base de datos
        saveContactToDatabase(contactoEmergencia);
    }


    private void saveContactToDatabase(ContactoEmergencia contactoEmergencia) {
        if(contactoEmergencia!=null){

        }

    }

}