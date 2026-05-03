package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionPuzzlesViewModel extends ViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> vacio = new MutableLiveData<>();

    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();

    public MutableLiveData<List<Puzzle>> getPuzzles() {
        return puzzles;
    }

    public MutableLiveData<String> getError() {
        return error;
    }

    public MutableLiveData<Boolean> getVacio() {
        return vacio;
    }

    public void cargarPuzzles(String token) {

        repositorio.obtenerPuzzles(token,"", new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<List<Puzzle>> call,
                                   @NonNull Response<List<Puzzle>> response) {

                if (response.isSuccessful()) {

                    List<Puzzle> lista = response.body();

                    if (lista == null || lista.isEmpty()) {
                        puzzles.setValue(null);
                        vacio.setValue(true);
                    } else {
                        puzzles.setValue(lista);
                        vacio.setValue(false);
                    }

                } else {
                    error.setValue("Error " + response.code() + " al cargar los puzzles");
                    vacio.setValue(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Puzzle>> call,
                                  @NonNull Throwable excepcion) {

                error.setValue("Fallo de red: " + excepcion.getMessage());
                vacio.setValue(true);
            }
        });
    }

    private final MutableLiveData<Boolean> puzzleActualizado = new MutableLiveData<>();

    public void cambiarEstado(Integer id_usuario, Integer id_puzzle, String tipo) {

        repositorio.cambiarEstadoPuzzle(id_usuario, id_puzzle, tipo, new Callback<>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    puzzleActualizado.setValue(true);

                } else {
                    puzzleActualizado.setValue(false);
                    error.setValue("Error " + response.code() + " al actualizar");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable excepcion) {
                puzzleActualizado.setValue(false);
                error.setValue("Fallo de red: " + excepcion.getMessage());
            }
        });
    }
}