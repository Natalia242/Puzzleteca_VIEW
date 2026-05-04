package com.ignacio_natalia.puzzleteca.red.ranking;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RankingApi {

    // Mismo host que los demás clientes del proyecto
    private static final String BASE_URL = "http://10.0.2.2:8080/ranking/";
    private static Retrofit retrofit = null;

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