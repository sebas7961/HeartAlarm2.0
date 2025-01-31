package com.example.heartalarm20.model.api;
import android.content.Context;
import android.util.Log;

import com.example.heartalarm20.R;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.List;

public class NotificationSender {
    //clave privada:2kR3MKtf40DC32IksKcAfnENixeAsmu6i-3FQVrOUBk
    //par de claves: BAAdab5Dd6YkJg4uaxS01XZ_Xv_5MjEchU4gTn6MmenwSMbkNFqpWleqZe3QGy1oEEKihJ23yWOmR0TvtFB-dA4"
    //clave de API web: AIzaSyB4tSMEtSX74pSkSx3jq43-Ntkkor0SYYE
    //private static final String FCM_SERVER_KEY = ; // Coloca aquí tu clave FCM
    private static final String FCM_API_URL = "https://fcm.googleapis.com/fcm/send";

    public static void enviarNotificacion(List<String> tokens, String titulo, String mensaje, Context context) {
        OkHttpClient client = new OkHttpClient();

        try {
            JSONObject json = new JSONObject();
            json.put("registration_ids", new JSONArray(tokens)); // Lista de tokens
            JSONObject notification = new JSONObject();
            notification.put("titulo", titulo);
            notification.put("mensaje", mensaje);
            json.put("notification", notification);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            // Asegúrate de que la clave no tenga saltos de línea
            String serverKey = context.getString(R.string.server_key).replace("\n", "").replace("\r", "");
            //serverKey = "392cd15ad6aa59e9067f96d984dece6097fa030a";
            //serverKey = "BAAdab5Dd6YkJg4uaxS01XZ_Xv_5MjEchU4gTn6MmenwSMbkNFqpWleqZe3QGy1oEEKihJ23yWOmR0TvtFB-dA4";
            Request request = new Request.Builder()
                    .url(FCM_API_URL)
                    .post(body)
                    .addHeader("Authorization", "key=" + serverKey)
                    .addHeader("Content-Type", "application/json")
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("Error al enviar notificación: ", e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.e("Notificación enviada: " , response.body().string());
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
