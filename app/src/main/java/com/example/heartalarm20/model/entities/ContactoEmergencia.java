package com.example.heartalarm20.model.entities;

public class ContactoEmergencia {
    private String id;
    private String numero;
    private String prioridad;
    private String nombre;

    public ContactoEmergencia(String id, String numero, String nombre) {
        this.numero = numero;
        this.prioridad = "Alta";
        this.nombre = nombre;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
