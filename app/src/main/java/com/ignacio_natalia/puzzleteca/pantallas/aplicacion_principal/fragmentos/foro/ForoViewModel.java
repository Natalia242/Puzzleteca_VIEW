package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Comentario;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.ComentarioRepositorio;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();
    private final ComentarioRepositorio comentarioRepositorio = new ComentarioRepositorio();

    // ===================== COMENTARIOS POR PUZZLE =====================
    private final Map<Integer, MutableLiveData<List<Comentario>>> comentariosPorPuzzle = new HashMap<>();

    public ForoViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Puzzle>> getPuzzles() {
        return puzzles;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<List<Comentario>> getComentariosPorPuzzle(Integer idPuzzle) {
        if (!comentariosPorPuzzle.containsKey(idPuzzle)) {
            comentariosPorPuzzle.put(idPuzzle, new MutableLiveData<>());
        }
        return comentariosPorPuzzle.get(idPuzzle);
    }

    public void cargarComentarios(String token, Integer idPuzzle) {

        comentarioRepositorio.obtenerComentarios(token, idPuzzle, new Callback<List<Comentario>>() {
            @Override
            public void onResponse(Call<List<Comentario>> call, Response<List<Comentario>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    if (!comentariosPorPuzzle.containsKey(idPuzzle)) {
                        comentariosPorPuzzle.put(idPuzzle, new MutableLiveData<>());
                    }

                    comentariosPorPuzzle.get(idPuzzle).setValue(response.body());

                } else {
                    error.setValue("Error " + response.code() + " al cargar comentarios");
                }
            }

            @Override
            public void onFailure(Call<List<Comentario>> call, Throwable t) {
                error.setValue("Fallo de conexión: " + t.getMessage());
            }
        });
    }

    public void crearComentario(Comentario comentario, String token) {

        comentarioRepositorio.crearComentario(comentario, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    // recargar comentarios del puzzle correspondiente
                    cargarComentarios(token, comentario.getId_puzzle());

                } else {
                    error.setValue("Error " + response.code() + " al crear comentario");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                error.setValue("Fallo de conexión: " + t.getMessage());
            }
        });
    }

    public void cargarPuzzles(String token) {

        repositorio.obtenerPuzzles(token, new Callback<>() {
            @Override
            public void onResponse(Call<List<Puzzle>> call, Response<List<Puzzle>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    for (Puzzle puzzle : response.body()) {

                        String base64 = puzzle.getImagenBase64();
                        Bitmap bitmap;

                        if (base64 != null && !base64.isEmpty()) {

                            try {
                                bitmap = decodarBase64(base64);

                            } catch (Exception e) {
                                bitmap = BitmapFactory.decodeResource(
                                        getApplication().getResources(),
                                        R.drawable.foto_predeterminada

                                );
                            }

                        } else {
                            bitmap = BitmapFactory.decodeResource(
                                    getApplication().getResources(),
                                    R.drawable.foto_predeterminada
                            );
                        }

                        puzzle.setBitmap(bitmap);
                    }

                    puzzles.setValue(response.body());


                } else {
                    error.setValue("Error " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Puzzle>> call, Throwable t) {
                error.setValue("Fallo de conexión: " + t.getMessage());
            }
        });
    }

    public Bitmap decodarBase64(String imagenBase64) throws Exception {

        if (imagenBase64.contains(",")) {
            imagenBase64 = imagenBase64.split(",")[1];
        }

        byte[] bytes = Base64.decode(imagenBase64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}