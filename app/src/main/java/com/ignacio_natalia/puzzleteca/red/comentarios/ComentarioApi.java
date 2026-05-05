package com.ignacio_natalia.puzzleteca.red.comentarios;

import com.ignacio_natalia.puzzleteca.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComentarioApi {

    private static final String BASE_URL = BuildConfig.BASE_URL;
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