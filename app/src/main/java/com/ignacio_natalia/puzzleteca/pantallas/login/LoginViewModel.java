package com.ignacio_natalia.puzzleteca.pantallas.login;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.LoginRespuesta;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final UsuarioRepositorio repositorio;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<LoginRespuesta> loginExitoso = new MutableLiveData<>();

    public LoginViewModel() {
        repositorio = new UsuarioRepositorio();
    }

    public LiveData<String> getError() { return error; }
    public LiveData<LoginRespuesta> getLoginExitoso() { return loginExitoso; }

    public void iniciarSesion(String email, String contrasena) {
        repositorio.login(email, contrasena, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<LoginRespuesta> call, @NonNull Response<LoginRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginExitoso.postValue(response.body());
                } else {
                    error.postValue("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginRespuesta> call, @NonNull Throwable excepcion) {
                error.postValue("Error de conexión");
            }
        });
    }

}