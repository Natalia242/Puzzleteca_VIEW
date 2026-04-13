package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.red.PuzzleApi;
import com.ignacio_natalia.puzzleteca.red.ServiciosApiPuzzles;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;

public class PuzzleRepositorio {

    private ServiciosApiPuzzles serviciosApiPuzzles;

    public PuzzleRepositorio() {
        serviciosApiPuzzles = PuzzleApi.getCliente().create(ServiciosApiPuzzles.class);
    }

    public void obtenerPuzzles(String token, Callback<List<Puzzle>> callback) {
        Call<List<Puzzle>> call = serviciosApiPuzzles.obtenerPuzzles("Bearer " + token);
        call.enqueue(callback);
    }

    public void crearPuzzle(Puzzle puzzle, Callback<Void> callback) {
        Call<Void> call = serviciosApiPuzzles.crearPuzzle(puzzle);
        call.enqueue(callback);
    }
}
