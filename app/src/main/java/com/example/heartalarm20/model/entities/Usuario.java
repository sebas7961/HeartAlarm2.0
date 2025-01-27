package com.example.heartalarm20.model.entities;

public class Usuario {
    private String nombreUsuario;
    private String dni;
    private String correo;
    private String password;
    private boolean isPaciente;

    public Usuario(String nombreUsuario, String dni, String correo, String password, boolean isPaciente) {
        this.nombreUsuario = nombreUsuario;
        this.dni = dni;
        this.correo = correo;
        this.password = password;
        this.isPaciente = isPaciente;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isPaciente() {
        return isPaciente;
    }

    public void setPaciente(boolean paciente) {
        isPaciente = paciente;
    }
}
