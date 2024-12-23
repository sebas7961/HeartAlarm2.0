package com.example.heartalarm20.model.entities;

import com.example.heartalarm20.model.Parametros;

import java.util.List;

public class Paciente {
    private String nombrePaciente;
    private String dni;
    private short edad;
    private float peso;
    private enum gradoMonitoreo{Bajo, Medio, Alto;}
    private List<ContactoEmergencia> contactosEmergencia;
    private Parametros parametros;


    public Paciente(String nombrePaciente, String dni, short edad, float peso, List<ContactoEmergencia> contactosEmergencia, Parametros parametros) {
        this.nombrePaciente = nombrePaciente;
        this.dni = dni;
        this.edad = edad;
        this.peso = peso;
        this.contactosEmergencia = contactosEmergencia;
        this.parametros = parametros;
    }

    public String getNombrePaciente() {
        return nombrePaciente;
    }

    public void setNombrePaciente(String nombrePaciente) {
        this.nombrePaciente = nombrePaciente;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public short getEdad() {
        return edad;
    }

    public void setEdad(short edad) {
        this.edad = edad;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public List<ContactoEmergencia> getContactosEmergencia() {
        return contactosEmergencia;
    }

    public ContactoEmergencia getContactoEmergencia(int index){
        return contactosEmergencia.get(index);
    }

    public void setContactosEmergencia(List<ContactoEmergencia> contactosEmergencia) {
        this.contactosEmergencia = contactosEmergencia;
    }

    public Parametros getParametros() {
        return parametros;
    }

    public void setParametros(Parametros parametros) {
        this.parametros = parametros;
    }
}
