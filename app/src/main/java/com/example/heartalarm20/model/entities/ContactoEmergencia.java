package com.example.heartalarm20.model.entities;

public class ContactoEmergencia {
    private String numero;
    private String prioridad;

    public ContactoEmergencia(String prioridad, String numero) {
        this.prioridad = prioridad;
        this.numero = numero;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(String prioridad) {
        this.prioridad = prioridad;
    }
}
