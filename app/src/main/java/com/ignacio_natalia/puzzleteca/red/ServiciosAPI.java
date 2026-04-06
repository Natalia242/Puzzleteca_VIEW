package com.ignacio_natalia.puzzleteca.red;

import com.ignacio_natalia.puzzleteca.modelos.ConfirmarCambioContrasenaRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.modelos.SolicitarCodigoRequest;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiciosAPI {
    @POST("login")
    Call<LoginRespuesta> login(@Body LoginRequest request);
    @POST("registrar")
    Call<Void> crearUsuario(@Body Usuario usuario);

    @POST("recuperarPassword/solicitarCodigo")
    Call<Void> solicitarCodigo(@Body SolicitarCodigoRequest request);

    @POST("recuperarPassword/confirmar")
    Call<Void> confirmarCambioPassword(@Body ConfirmarCambioContrasenaRequest request);

}