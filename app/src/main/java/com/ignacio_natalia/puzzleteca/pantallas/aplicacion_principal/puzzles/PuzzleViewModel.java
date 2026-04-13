package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class PuzzleViewModel extends ViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();
    public LiveData<List<Puzzle>> getPuzzles() {
        return puzzles;
    }
    public LiveData<String> getError() {
        return error;
    }

    public void cargarPuzzles(String token) {

        repositorio.obtenerPuzzles(token, new Callback<>() {

            @Override
            public void onResponse(@NonNull Call<List<Puzzle>> call, @NonNull Response<List<Puzzle>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    puzzles.setValue(response.body());
                } else {
                    error.setValue("Error " + response.code() + " al cargar los puzzles");
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Puzzle>> call, @NonNull Throwable excepcion) {
                error.setValue("Fallo de red: " + excepcion.getMessage());

            }

        });

    }

}
