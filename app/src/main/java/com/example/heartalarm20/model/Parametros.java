package com.example.heartalarm20.model;

public class Parametros {
    private short alto;
    private short bajo;

    public Parametros(short alto, short bajo) {
        this.alto = alto;
        this.bajo = bajo;
    }

    public boolean validar(short alto, short bajo){
        return alto>bajo && alto<=220 && bajo>=30;
    }

    public short getAlto() {
        return alto;
    }

    public void setAlto(short alto) {
        this.alto = alto;
    }

    public short getBajo() {
        return bajo;
    }

    public void setBajo(short bajo) {
        this.bajo = bajo;
    }
}
