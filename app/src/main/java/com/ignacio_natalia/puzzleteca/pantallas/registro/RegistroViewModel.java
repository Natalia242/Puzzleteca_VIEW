package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.util.Log;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.MutableLiveData;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistroViewModel extends ViewModel {

    private UsuarioRepositorio usuarioRepositorio;
    private MutableLiveData<Boolean> usuarioCreado;

    public RegistroViewModel() {
        usuarioRepositorio = new UsuarioRepositorio();
        usuarioCreado = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getUsuarioCreado() {
        return usuarioCreado;
    }

    public void crearUsuario(Usuario usuario) {
        usuarioRepositorio.crearUsuario(usuario, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
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
            public void onFailure(Call<Void> call, Throwable t) {
                // Log de error en caso de fallo en la petición
                Log.e("RegistroViewModel", "Error en la conexión: " + t.getMessage(), t);
                usuarioCreado.setValue(false);
            }
        });
    }
}