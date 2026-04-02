package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal;
import android.view.VerifiedInputEvent;

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
    public LiveData<List<Puzzle>> getPuzzles() { return puzzles; }
    public LiveData<String> getError() { return error; }

    public void cargarPuzzles(String token) {
        repositorio.obtenerPuzzles(token, new Callback<List<Puzzle>>() {

            @Override
            public void onResponse(Call<List<Puzzle>> call, Response<List<Puzzle>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    puzzles.setValue(response.body());

                } else {
                    error.setValue("Error " + response.code() + " al cargar los puzzles");
                }
            }

            @Override
            public void onFailure(Call<List<Puzzle>> call, Throwable t) {
                error.setValue("Fallo de red: " + t.getMessage());

            }
        });
    }
}
