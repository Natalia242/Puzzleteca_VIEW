package com.ignacio_natalia.puzzleteca.red;

import com.ignacio_natalia.puzzleteca.modelos.ConfirmarCambioContrasenaRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.modelos.SolicitarCodigoRequest;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServiciosAPI {

    @GET("listarUsuarios")
    Call<List<Usuario>> obtenerUsuarios(@Header("Authorization") String token);

    @POST("login")
    Call<LoginRespuesta> login(@Body LoginRequest request);

    @POST("registrar")
    Call<Void> crearUsuario(@Body Usuario usuario);

    @POST("recuperarPassword/solicitarCodigo")
    Call<Void> solicitarCodigo(@Body SolicitarCodigoRequest request);

    @POST("recuperarPassword/confirmar")
    Call<Void> confirmarCambioContrasena(@Body ConfirmarCambioContrasenaRequest request);
    @DELETE("eliminarCuenta")
    Call<Void> eliminarCuenta(@Query("email") String email);

    @PUT("cambiarEstado")
    Call<Void> cambiarEstado(@Query("email") String email, @Query("tipo") String tipo);

}