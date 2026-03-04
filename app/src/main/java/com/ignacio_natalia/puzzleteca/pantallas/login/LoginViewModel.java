package com.ignacio_natalia.puzzleteca.pantallas.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.LoginResponse;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends ViewModel {

    private final UsuarioRepositorio repositorio;
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<LoginResponse> loginExitoso = new MutableLiveData<>();

    public LoginViewModel() {
        repositorio = new UsuarioRepositorio();
    }

    public LiveData<String> getError() { return error; }
    public LiveData<LoginResponse> getLoginExitoso() { return loginExitoso; }

    public void iniciarSesion(String email, String password) {
        repositorio.login(email, password, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    loginExitoso.postValue(response.body());
                } else {
                    error.postValue("Credenciales incorrectas");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                error.postValue("Error de conexión");
            }
        });
    }
}