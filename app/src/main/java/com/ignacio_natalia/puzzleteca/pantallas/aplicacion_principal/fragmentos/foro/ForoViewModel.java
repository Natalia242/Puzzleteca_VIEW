package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles = new MutableLiveData<>();
    private final MutableLiveData<Integer> puzzleActualizado = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();

    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public ForoViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Puzzle>> getPuzzles() { return puzzles; }
    public LiveData<Integer> getPuzzleActualizado() { return puzzleActualizado; }
    public LiveData<String> getError() { return error; }

    public void cargarPuzzles(String token) {
        repositorio.obtenerPuzzles(token, new Callback<>() {
            @Override
            public void onResponse(Call<List<Puzzle>> call, Response<List<Puzzle>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<Puzzle> lista = response.body();
                    puzzles.setValue(lista);

                    for (int i = 0; i < lista.size(); i++) {
                        final int index = i;
                        Puzzle puzzle = lista.get(index);

                        if (puzzle.getBitmap() != null) continue;

                        String base64 = puzzle.getImagenBase64();

                        if (base64 == null || base64.isEmpty()) {
                            Bitmap fallback = BitmapFactory.decodeResource(
                                    getApplication().getResources(),
                                    R.drawable.fotopredeterminada
                            );
                            puzzle.setBitmap(fallback);
                            continue;
                        }

                        executor.execute(() -> {
                            Bitmap bitmap;

                            try {
                                bitmap = decodarBase64Reducido(base64);
                            } catch (ImagenException e) {
                                bitmap = BitmapFactory.decodeResource(
                                        getApplication().getResources(),
                                        R.drawable.fotopredeterminada
                                );
                            }

                            Bitmap finalBitmap = bitmap;

                            mainHandler.post(() -> {
                                puzzle.setBitmap(finalBitmap);
                                puzzleActualizado.setValue(index);
                            });
                        });
                    }

                } else {
                    error.setValue("Error " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Puzzle>> call, Throwable t) {
                error.setValue("Fallo: " + t.getMessage());
            }
        });
    }

    public Bitmap decodarBase64Reducido(String imagenBase64) throws ImagenException {
        try {

            if (imagenBase64.contains(",")) {
                imagenBase64 = imagenBase64.split(",")[1];
            }

            byte[] bytes = Base64.decode(imagenBase64, Base64.DEFAULT);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

            if (bitmap == null) {
                throw new ImagenException("Bitmap nulo");
            }

            return bitmap;

        } catch (Exception e) {
            throw new ImagenException("Error: " + e.getMessage());
        }
    }
}
