package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import android.net.Uri;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    //Crear puzzles
    private final MutableLiveData<Boolean> puzzleCreado = new MutableLiveData<>();

    public LiveData<Boolean> getPuzzleCreado() {
        return puzzleCreado;
    }

    public void crearPuzzle(Puzzle puzzle) {

        repositorio.crearPuzzle(puzzle, new Callback<>() {
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
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable excepcion) {
                puzzleCreado.setValue(false);
                error.setValue("Fallo de red: " + excepcion.getMessage());
            }
        });
    }

    //Metodo auxiliar para convertir imágenes a base 64
    public String convertirImagenBase64(Uri uri, android.content.ContentResolver contentResolver) {
        try (InputStream inputStream = contentResolver.openInputStream(uri)) {
            if (inputStream == null) return null;

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] chunk = new byte[4096];
            int leidos;
            while ((leidos = inputStream.read(chunk)) != -1) {
                buffer.write(chunk, 0, leidos);
            }

            return Base64.encodeToString(buffer.toByteArray(), Base64.NO_WRAP);
        } catch (IOException e) {
            return null;
        }
    }


}
