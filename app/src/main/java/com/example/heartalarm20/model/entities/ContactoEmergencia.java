package com.example.heartalarm20.model.entities;

import java.util.Objects;

public class ContactoEmergencia {
    private String id;
    private String numero;
    private String prioridad;
    private String nombre;

    public ContactoEmergencia(String id, String numero, String nombre, String prioridad) {
        this.numero = numero;
        this.prioridad = prioridad;
        this.nombre = nombre;
    }

    public ContactoEmergencia(String id, String numero, String nombre) {
        this.numero = numero;
        this.prioridad = "Secundario";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactoEmergencia that = (ContactoEmergencia) o;
        return Objects.equals(id, that.id) && Objects.equals(numero, that.numero) && Objects.equals(prioridad, that.prioridad) && Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, numero, prioridad, nombre);
    }
}
