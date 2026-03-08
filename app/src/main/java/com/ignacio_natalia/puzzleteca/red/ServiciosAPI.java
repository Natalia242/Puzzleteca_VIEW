package com.ignacio_natalia.puzzleteca.red;

import com.ignacio_natalia.puzzleteca.modelos.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginResponse;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiciosAPI {
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
    @POST("registrar")
    Call<Void> crearUsuario(@Body Usuario usuario);

}