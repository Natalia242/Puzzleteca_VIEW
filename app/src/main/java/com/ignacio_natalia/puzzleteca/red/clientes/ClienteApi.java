package com.ignacio_natalia.puzzleteca.red.clientes;

import com.ignacio_natalia.puzzleteca.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClienteApi {

    private static final String BASE_URL = BuildConfig.BASE_URL;
    private static Retrofit retrofit = null;

    // Metodo para obtener el cliente Retrofit singleton
    public static Retrofit getCliente() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;

    }

}