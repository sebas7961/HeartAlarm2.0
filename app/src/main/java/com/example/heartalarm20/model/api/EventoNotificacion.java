package com.example.heartalarm20.model.api;

import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.*;

public class EventoNotificacion {

    private static final String BACKEND_URL = "http://192.168.18.33:5000/enviar-notificacion";

    public static void enviarEvento(String titulo, String mensaje, List<String> tokens) {
        OkHttpClient client = new OkHttpClient();

        int index = 0;
        for(String token : tokens){
            token = "\""+token+"\"";
            tokens.set(index,token);
            index++;
        }

        // Crea el cuerpo de la solicitud
        String json = "{\"titulo\":\"" + titulo + "\",\"mensaje\":\"" + mensaje + "\",\"tokens\":" + tokens + "}";
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        // Crea la solicitud HTTP
        Request request = new Request.Builder()
                .url(BACKEND_URL)
                .post(body)
                .build();

        Log.d("EventoNotificacion", json);

        // Envía la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Maneja el error
                Log.d("EventoNotificacion", "EnvíoFallido1\n"+ e.getMessage() + "\n"+ call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("EventoNotificacion", "EnvíoExitoso\n"+ response);
                } else {
                    Log.d("EventoNotificacion", "EnvíoFallido2\n" + response + "\n"+call.toString());
                }
            }
        });
    }
}