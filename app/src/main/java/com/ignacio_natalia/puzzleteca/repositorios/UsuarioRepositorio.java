package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.LoginRequest;
import com.ignacio_natalia.puzzleteca.modelos.LoginResponse;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.red.*;
import retrofit2.Call;
import retrofit2.Callback;

public class UsuarioRepositorio {

    private ServiciosAPI servicioApi;

    public UsuarioRepositorio() {
        servicioApi = ClienteApi.getCliente().create(ServiciosAPI.class);
    }

    public void login(String email, String password, Callback<LoginResponse> callback) {
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = servicioApi.login(request);
        call.enqueue(callback);
    }

    public void crearUsuario(Usuario usuario, Callback<Void> callback) {
        Call<Void> call = servicioApi.crearUsuario(usuario);
        call.enqueue(callback); // Realiza la petición asincrona
    }

}