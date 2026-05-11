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

    private final MutableLiveData<List<Puzzle>> puzzles      = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      puzzleCreado = new MutableLiveData<>();
    private final MutableLiveData<Boolean>      puzzleActualizado = new MutableLiveData<>();
    private final MutableLiveData<String>       error        = new MutableLiveData<>();

    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();

    public LiveData<List<Puzzle>> getPuzzles()          { return puzzles; }
    public LiveData<Boolean>      getPuzzleCreado()     { return puzzleCreado; }
    public LiveData<Boolean>      getPuzzleActualizado(){ return puzzleActualizado; }
    public LiveData<String>       getError()            { return error; }

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

    /**
     * Actualiza un único atributo del puzzle en el backend.
     * Los errores de red se publican en getError().
     */
    public void actualizarPuzzle(String token, int idUsuario, int idPuzzle,
                                 String atributo, String cambio) {
        repositorio.actualizarPuzzle(token, idUsuario, idPuzzle, atributo, cambio,
                new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(@androidx.annotation.NonNull retrofit2.Call<Void> call,
                                           @androidx.annotation.NonNull retrofit2.Response<Void> response) {
                        if (!response.isSuccessful()) {
                            error.setValue("Error " + response.code()
                                    + " al actualizar " + atributo);
                        }
                    }
                    @Override
                    public void onFailure(@androidx.annotation.NonNull retrofit2.Call<Void> call,
                                          @androidx.annotation.NonNull Throwable t) {
                        error.setValue("Fallo de red al actualizar " + atributo + ": " + t.getMessage());
                    }
                });
    }

    /** Cambia el estado del puzzle (Publico/Privado) usando el endpoint específico. */
    public void cambiarEstadoPuzzle(int idUsuario, int idPuzzle, String nuevoEstado) {
        repositorio.cambiarEstadoPuzzle(idUsuario, idPuzzle, nuevoEstado,
                new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(@androidx.annotation.NonNull retrofit2.Call<Void> call,
                                           @androidx.annotation.NonNull retrofit2.Response<Void> response) {
                        if (!response.isSuccessful()) {
                            error.setValue("Error " + response.code() + " al cambiar el estado");
                        }
                    }
                    @Override
                    public void onFailure(@androidx.annotation.NonNull retrofit2.Call<Void> call,
                                          @androidx.annotation.NonNull Throwable t) {
                        error.setValue("Fallo de red al cambiar estado: " + t.getMessage());
                    }
                });
    }

    /** Llama a este método para indicar que todas las actualizaciones se han enviado. */
    public void notificarActualizacion() {
        puzzleActualizado.setValue(true);
    }
}