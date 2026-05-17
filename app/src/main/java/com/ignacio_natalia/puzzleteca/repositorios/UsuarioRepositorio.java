package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.actualizar.ActualizarUsuarioRequest;
import com.ignacio_natalia.puzzleteca.modelos.cambioContrasenna.ConfirmarCambioContrasenaRequest;
import com.ignacio_natalia.puzzleteca.modelos.login.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.login.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.modelos.cambioContrasenna.SolicitarCodigoRequest;
import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;
import com.ignacio_natalia.puzzleteca.red.clientes.ClienteApi;
import com.ignacio_natalia.puzzleteca.red.clientes.ServiciosAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class UsuarioRepositorio {

    private final ServiciosAPI servicioApi;

    public UsuarioRepositorio() {
        servicioApi = ClienteApi.getCliente().create(ServiciosAPI.class);
    }

    public void listarUsuarios(String token, Callback<List<Usuario>> callback) {
        Call<List<Usuario>> call = servicioApi.obtenerUsuarios("Bearer" + token);
        call.enqueue(callback);
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

    public void confirmarCambioContrasena(String email, String codigo, String nuevaContrasena, Callback<Void> callback) {
        ConfirmarCambioContrasenaRequest request = new ConfirmarCambioContrasenaRequest(email, codigo, nuevaContrasena);
        Call<Void> call = servicioApi.confirmarCambioContrasena(request);
        call.enqueue(callback);
    }

    public void borrarCuenta(String email, Callback<Void> callback) {
        Call<Void> call = servicioApi.eliminarCuenta(email);
        call.enqueue(callback);
    }

    public void actualizarEstado(String email, String tipoUsuario, Callback<Void> callback) {
        Call<Void> call = servicioApi.cambiarEstado(email, tipoUsuario);
        call.enqueue(callback);
    }

    public void actualizarPerfil(ActualizarUsuarioRequest request, Callback<Void> callback) {
        Call<Void> call = servicioApi.actualizarUsuario(request);
        call.enqueue(callback);
    }

}