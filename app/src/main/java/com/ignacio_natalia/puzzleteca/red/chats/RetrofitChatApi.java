package com.ignacio_natalia.puzzleteca.red.chats;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitChatApi {

    private static final String BASE_URL = "http://10.0.2.2:8080/chat/";
    private static Retrofit retrofit;

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