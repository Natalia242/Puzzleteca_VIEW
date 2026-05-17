package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroViewModel extends ViewModel {

    private final UsuarioRepositorio usuarioRepositorio;
    private final MutableLiveData<Boolean> usuarioCreado;

    public RegistroViewModel() {
        usuarioRepositorio = new UsuarioRepositorio();
        usuarioCreado = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getUsuarioCreado() {
        return usuarioCreado;
    }

    public void crearUsuario(Usuario usuario) {

        usuarioRepositorio.crearUsuario(usuario, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    // Log de éxito
                    Log.d("RegistroViewModel", "Usuario creado exitosamente.");
                    usuarioCreado.setValue(true);
                } else {
                    // Log de error con código de respuesta
                    Log.e("RegistroViewModel", "Error en la respuesta: " + response.code());
                    usuarioCreado.setValue(false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable excepcion) {
                // Log de error en caso de fallo en la petición
                Log.e("RegistroViewModel", "Error en la conexión: " + excepcion.getMessage());
                usuarioCreado.setValue(false);
            }
        });

    }
}