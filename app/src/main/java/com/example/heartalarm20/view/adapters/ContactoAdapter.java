package com.example.heartalarm20.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.heartalarm20.R;
import com.example.heartalarm20.model.entities.ContactoEmergencia;

import java.util.List;

public class ContactoAdapter extends RecyclerView.Adapter<ContactoAdapter.ContactoViewHolder> {

    private final List<ContactoEmergencia> contactos;
    private final Context context;
    private OnContactoInteractionListener listener;

    // Interfaz para manejar eventos de interacción
    public interface OnContactoInteractionListener {
        void onDeleteContact(int position);
        void onPriorityChanged(int position, String newPriority);
    }

    public void setListener(OnContactoInteractionListener listener) {
        this.listener = listener;
    }

    public ContactoAdapter(Context context, List<ContactoEmergencia> contactos) {
        this.context = context;
        this.contactos = contactos;
    }

    @NonNull
    @Override
    public ContactoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.contact_list_item, parent, false);
        return new ContactoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoViewHolder holder, int position) {
        ContactoEmergencia contacto = contactos.get(position);
        holder.tvNombre.setText(contacto.getNombre());
        holder.tvNumero.setText(contacto.getNumero());

        // Configurar el Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                context,
                R.array.prioridades,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spPrioridad.setAdapter(adapter);

        // Seleccionar la prioridad actual
        int spinnerPosition = adapter.getPosition(contacto.getPrioridad());
        holder.spPrioridad.setSelection(spinnerPosition);

        // Listener para el cambio de prioridad
        holder.spPrioridad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String nuevaPrioridad = parent.getItemAtPosition(pos).toString();
                if (!nuevaPrioridad.equals(contacto.getPrioridad())) {
                    contacto.setPrioridad(nuevaPrioridad);
                    listener.onPriorityChanged(position, nuevaPrioridad);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botón de eliminar
        holder.ibDelete.setOnClickListener(v -> listener.onDeleteContact(position));
    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public static class ContactoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvNumero;
        Spinner spPrioridad;
        ImageButton ibDelete;

        public ContactoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvNumero = itemView.findViewById(R.id.tv_numero);
            spPrioridad = itemView.findViewById(R.id.sp_prioridad);
            ibDelete = itemView.findViewById(R.id.ib_delete);
        }
    }

    public void updateContactList(List<ContactoEmergencia> newContactList) {
        this.contactos.clear();
        this.contactos.addAll(newContactList);
        notifyDataSetChanged();
    }

}
