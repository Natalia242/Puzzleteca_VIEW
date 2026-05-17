package com.ignacio_natalia.puzzleteca.red.clientes;

import com.ignacio_natalia.puzzleteca.modelos.actualizar.ActualizarUsuarioRequest;
import com.ignacio_natalia.puzzleteca.modelos.cambioContrasenna.ConfirmarCambioContrasenaRequest;
import com.ignacio_natalia.puzzleteca.modelos.login.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.login.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.modelos.cambioContrasenna.SolicitarCodigoRequest;
import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServiciosAPI {

    @GET("usuarios/listarUsuarios")
    Call<List<Usuario>> obtenerUsuarios(@Header("Authorization") String token);

    @POST("usuarios/login")
    Call<LoginRespuesta> login(@Body LoginRequest request);

    @POST("usuarios/registrar")
    Call<Void> crearUsuario(@Body Usuario usuario);

    @POST("usuarios/recuperarPassword/solicitarCodigo")
    Call<Void> solicitarCodigo(@Body SolicitarCodigoRequest request);

    @POST("usuarios/recuperarPassword/confirmar")
    Call<Void> confirmarCambioContrasena(@Body ConfirmarCambioContrasenaRequest request);
    @DELETE("usuarios/eliminarCuenta")
    Call<Void> eliminarCuenta(@Query("email") String email);

    @PUT("usuarios/cambiarEstado")
    Call<Void> cambiarEstado(@Query("email") String email, @Query("tipo") String tipo);

    @PUT("usuarios/actualizarUsuario")
    Call<Void> actualizarUsuario(@Body ActualizarUsuarioRequest request);

}