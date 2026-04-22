package com.ignacio_natalia.puzzleteca.red.comentarios;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComentarioApi {

    private static final String BASE_URL = "http://10.0.2.2:8080/comentarios/";
    private static Retrofit retrofit = null;

    // Metodo para obtener el cliente Retrofit singleton
    public static Retrofit getComentario() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }

}