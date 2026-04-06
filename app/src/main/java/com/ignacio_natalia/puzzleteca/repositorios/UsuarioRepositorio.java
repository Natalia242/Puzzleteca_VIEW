package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.ConfirmarCambioContrasenaRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.modelos.SolicitarCodigoRequest;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.red.*;
import retrofit2.Call;
import retrofit2.Callback;

public class UsuarioRepositorio {

    private ServiciosAPI servicioApi;

    public UsuarioRepositorio() {
        servicioApi = ClienteApi.getCliente().create(ServiciosAPI.class);
    }

    public void login(String email, String contrasena, Callback<LoginRespuesta> callback) {
        LoginRequest request = new LoginRequest(email, contrasena);
        Call<LoginRespuesta> call = servicioApi.login(request);
        call.enqueue(callback);
    }

    public void crearUsuario(Usuario usuario, Callback<Void> callback) {
        Call<Void> call = servicioApi.crearUsuario(usuario);
        call.enqueue(callback); // Realiza la petición asincrona
    }

    public void solicitarCodigo(String email, Callback<Void> callback) {
        SolicitarCodigoRequest request = new SolicitarCodigoRequest(email);
        Call<Void> call = servicioApi.solicitarCodigo(request);
        call.enqueue(callback);
    }

    public void confirmarCambioPassword(String email, String codigo, String nuevaContrasena, Callback<Void> callback) {
        ConfirmarCambioContrasenaRequest request = new ConfirmarCambioContrasenaRequest(email, codigo, nuevaContrasena);
        Call<Void> call = servicioApi.confirmarCambioPassword(request);
        call.enqueue(callback);
    }

}