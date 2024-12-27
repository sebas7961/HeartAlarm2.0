package com.example.heartalarm20.model.entities;

import java.util.ArrayList;
import java.util.List;

public class Preguntas {
    private String dniPaciente;
    private List<String> listaPreguntas;


    public Preguntas(String dniPaciente) {
        this.dniPaciente = dniPaciente;
        listaPreguntas = new ArrayList<>();
    }

    public boolean completarPreguntas(String grado){
        switch (grado){
            case "Alto":
                //agregar lista de preguntas para monitoreo de alto riesgo
                break;
            case "Medio":
                //agregar lista de preguntas para monitoreo de mediano riesgo
                break;
            case "Bajo":
                //agregar lista de preguntas para monitoreo de bajo riesgo
                break;
            default:
                return false;
        }
        return true;
    }

    public String getDniPaciente() {
        return dniPaciente;
    }

    public void setDniPaciente(String dniPaciente) {
        this.dniPaciente = dniPaciente;
    }

    public List<String> getListaPreguntas() {
        return listaPreguntas;
    }

    public void setListaPreguntas(List<String> listaPreguntas) {
        this.listaPreguntas = listaPreguntas;
    }
}
