package com.example.heartalarm20.model.entities;

import java.util.List;

public class Paciente {
    private String nombrePaciente;
    private String dni;
    private short edad;
    private float peso;
    private enum gradoMonitoreo{Bajo, Medio, Alto;}
    private List<String> contactosEmergencia;
    private Parametros parametros;

}
