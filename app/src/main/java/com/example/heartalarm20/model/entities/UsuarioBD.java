package com.example.heartalarm20.model.entities;

public class UsuarioBD {
    private String correo;
    private String password;
    private boolean isPaciente;

    public UsuarioBD(Usuario user) {
        this.correo = user.getCorreo();
        this.password = user.getPassword();
        this.isPaciente = user.isPaciente();
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
