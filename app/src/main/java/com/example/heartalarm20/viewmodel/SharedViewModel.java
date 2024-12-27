package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private short parametros;

    private final MutableLiveData<Short> _monitoreoPulso = new MutableLiveData<>();
    public LiveData<Short> monitoreoPulso = _monitoreoPulso;

    private final MutableLiveData<String> _alerta = new MutableLiveData<>();
    public LiveData<String> alerta = _alerta;

    private final MutableLiveData<Boolean> _conexionBluetooth = new MutableLiveData<>(false);
    public LiveData<Boolean> conexionBluetooth = _conexionBluetooth;

    private final MutableLiveData<String> _signalStrength = new MutableLiveData<>();
    public LiveData<String> signalStrength = _signalStrength;

    public short getParametros() {
        return parametros;
    }

    public void setParametros(short parametros) {
        this.parametros = parametros;
    }

    public void actualizarMonitoreoPulso(Short datoLeido) {
        if (datoLeido != null) {
            _monitoreoPulso.postValue(datoLeido);
        }
    }

    public void setAlerta(String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            _alerta.postValue(mensaje);
        }
    }

    public void setConexionBluetooth(Boolean estado) {
        if (estado != null) {
            _conexionBluetooth.postValue(estado);
        }
    }

    public void setSignalStrength(String strength) {
        if (strength != null && !strength.isEmpty()) {
            _signalStrength.postValue(strength);
        }
    }
}