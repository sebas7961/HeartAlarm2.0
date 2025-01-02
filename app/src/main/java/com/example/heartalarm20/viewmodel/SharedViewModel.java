package com.example.heartalarm20.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Short> _parametrosBajo = new MutableLiveData<>();
    public LiveData<Short> parametrosBajo = _parametrosBajo;

    private final MutableLiveData<Short> _parametrosAlto = new MutableLiveData<>();
    public LiveData<Short> parametrosAlto = _parametrosAlto;

    private final MutableLiveData<Short> _monitoreoPulso = new MutableLiveData<>();
    public LiveData<Short> monitoreoPulso = _monitoreoPulso;

    private final MutableLiveData<String> _alerta = new MutableLiveData<>();
    public LiveData<String> alerta = _alerta;

    private final MutableLiveData<Boolean> _conexionBluetooth = new MutableLiveData<>(false);
    public LiveData<Boolean> conexionBluetooth = _conexionBluetooth;

    private final MutableLiveData<String> _signalStrength = new MutableLiveData<>();
    public LiveData<String> signalStrength = _signalStrength;

    private final MutableLiveData<String> _parametrosESP = new MutableLiveData<>();
    public LiveData<String> parametrosESP = _parametrosESP;

    public void setParametrosBajo(Short valor) {
        if (valor != null) {
            _parametrosBajo.postValue(valor);
        }
    }

    public Short getParametrosBajo() {
        return _parametrosBajo.getValue();
    }

    public void setParametrosAlto(Short valor) {
        if (valor != null) {
            _parametrosAlto.postValue(valor);
        }
    }

    public Short getParametrosAlto() {
        return _parametrosAlto.getValue();
    }

    public void actualizarMonitoreoPulso(Short datoLeido) {
        if (datoLeido != null) {
            _monitoreoPulso.postValue(datoLeido);
        }
    }

    public Short getMonitoreoPulso() {
        return _monitoreoPulso.getValue();
    }

    public void setAlerta(String mensaje) {
        if (mensaje != null && !mensaje.isEmpty()) {
            _alerta.postValue(mensaje);
        }
    }

    public String getAlerta() {
        return _alerta.getValue();
    }

    public void setConexionBluetooth(Boolean estado) {
        if (estado != null) {
            _conexionBluetooth.postValue(estado);
        }
    }

    public Boolean getConexionBluetooth() {
        return _conexionBluetooth.getValue();
    }

    public void setSignalStrength(String strength) {
        if (strength != null && !strength.isEmpty()) {
            _signalStrength.postValue(strength);
        }
    }

    public String getSignalStrength() {
        return _signalStrength.getValue();
    }

    public void setParametrosESP(String parametros) {
        if (parametros != null && !parametros.isEmpty()) {
            _parametrosESP.postValue(parametros);
        }
    }

    public String getParametrosESP() {
        return _parametrosESP.getValue();
    }

}