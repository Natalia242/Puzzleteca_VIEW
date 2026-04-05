package com.ignacio_natalia.puzzleteca.pantallas.registro;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecuperarContrasennaViewModel extends ViewModel {
    private final UsuarioRepositorio repositorio = new UsuarioRepositorio();

    public final MutableLiveData<Boolean> codigoEnviado = new MutableLiveData<>();
    public final MutableLiveData<Boolean> passwordCambiada = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();
    public void solicitarCodigo(String email) {

        codigoEnviado.postValue(true);
        repositorio.solicitarCodigo(email, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    codigoEnviado.postValue(true);

                } else {
                    error.postValue("No se pudo enviar el código. Comprueba el email");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue("Error de conexión: " + t.getMessage());
            }
        });
    }
    public void confirmarCambioPassword(String email, String codigo, String nuevaPassword) {

        repositorio.confirmarCambioPassword(email, codigo, nuevaPassword, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {
                    passwordCambiada.postValue(true);

                } else if (response.code() == 401) {
                    error.postValue("Código incorrecto o expirado.");

                } else {
                    error.postValue("Error al cambiar la contraseña.");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.postValue("Error de conexión: " + t.getMessage());

            }
        });
    }

}

