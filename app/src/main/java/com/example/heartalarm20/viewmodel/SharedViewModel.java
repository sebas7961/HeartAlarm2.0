package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel  extends ViewModel {
    private short parametros;
    private final MutableLiveData<Short> monitoreoPulso = new MutableLiveData<>();

    public short getParametros() {
        return parametros;
    }

    public void setParametros(short parametros) {
        this.parametros = parametros;
    }

    public LiveData<Short> getMonitoreoPulso() {
        return monitoreoPulso;
    }

    public void actualizarMonitoreoPulso(Short datoLeido){
        monitoreoPulso.setValue(datoLeido);
    }

}
