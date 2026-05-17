package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionUsuarioViewModel extends ViewModel {
    private final MutableLiveData<List<Usuario>> usuarios = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final UsuarioRepositorio repositorio = new UsuarioRepositorio();
    public LiveData<List<Usuario>> getUsuarios() {
        return usuarios;
    }
    public LiveData<String> getError() {
        return error;
    }

    public void cargarUsuarios(String token, String emailUsuarioLogado) {

        repositorio.listarUsuarios(token, new Callback<>() {

            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Usuario> listaUsuariosApi = response.body();
                    List<Usuario> listaFiltrada = new ArrayList<>();

                    for (Usuario u: listaUsuariosApi) {

                        if (!u.getEmail().equals(emailUsuarioLogado)) {
                            listaFiltrada.add(u);
                        }

                    }

                    usuarios.setValue(listaFiltrada);

                } else {
                    error.setValue("Error " + response.code() + " al cargar los usuarios");
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable excepcion) {
                error.setValue("Fallo de red: " + excepcion.getMessage());

            }
        });
    }

    private final MutableLiveData<Boolean> usuarioActualizado = new MutableLiveData<>();
    public void actualizarEstadoUsuario(String email, String tipo) {

        repositorio.actualizarEstado(email, tipo, new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    usuarioActualizado.setValue(true);
                } else {
                    usuarioActualizado.setValue(false);
                    error.setValue("Error " + response.code() + " al actualizar");
                }

            }

            @Override
            public void onFailure(Call<Void> call, Throwable excepcion) {
                usuarioActualizado.setValue(false);
                error.setValue("Fallo de red: " + excepcion.getMessage());
            }
        });

    }
}
