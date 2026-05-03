package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PuzzleViewModel extends ViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles    = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      puzzleCreado = new MutableLiveData<>();
    private final MutableLiveData<String>       error      = new MutableLiveData<>();

    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();

    public LiveData<List<Puzzle>> getPuzzles()      { return puzzles; }
    public LiveData<Boolean>      getPuzzleCreado() { return puzzleCreado; }
    public LiveData<String>       getError()        { return error; }

    public void cargarPuzzles(String token) {
        repositorio.obtenerPuzzles(token, "Publico", new Callback<List<Puzzle>>() {
            @Override
            public void onResponse(@NonNull Call<List<Puzzle>> call,
                                   @NonNull Response<List<Puzzle>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    puzzles.setValue(response.body());
                } else if (response.code() == 404) {
                    puzzles.setValue(java.util.Collections.emptyList());
                } else {
                    error.setValue("Error " + response.code() + " al cargar los puzzles");
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<Puzzle>> call, @NonNull Throwable t) {
                error.setValue("Fallo de red: " + t.getMessage());
            }
        });
    }

    /**
     * Crea el puzzle enviando los datos + imagen (File) al backend via multipart.
     *
     * @param token       JWT del usuario
     * @param puzzle      datos del puzzle (sin imagen)
     * @param imagenFile  archivo seleccionado de galería (puede ser null)
     */
    public void crearPuzzle(String token, Puzzle puzzle, File imagenFile) {
        repositorio.crearPuzzle(token, puzzle, imagenFile, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    puzzleCreado.setValue(true);
                } else {
                    puzzleCreado.setValue(false);
                    error.setValue("Error " + response.code() + " al crear el puzzle");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                puzzleCreado.setValue(false);
                error.setValue("Fallo de red: " + t.getMessage());
            }
        });
    }

    public void recargarPuzzles(String token) {
        puzzles.setValue(null);
        cargarPuzzles(token);
    }
}
