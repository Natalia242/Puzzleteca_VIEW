package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForoViewModel extends AndroidViewModel {

    private final MutableLiveData<List<Puzzle>> puzzles = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final PuzzleRepositorio repositorio = new PuzzleRepositorio();

    public ForoViewModel(Application application) {
        super(application);
    }

    public LiveData<List<Puzzle>> getPuzzles() {
        return puzzles;
    }

    public LiveData<String> getError() {
        return error;
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

                            } catch (ImagenException e) {

                                // Si falla, usar imagen predeterminada
                                bitmap = BitmapFactory.decodeResource(
                                        getApplication().getResources(),
                                        R.drawable.fotopredeterminada
                                );
                            }

                        } else {
                            // Base64 vacío o nulo, usar imagen predeterminada directamente
                            bitmap = BitmapFactory.decodeResource(
                                    getApplication().getResources(),
                                    R.drawable.fotopredeterminada
                            );
                        }
                        puzzle.setBitmap(bitmap);
                    }
                    puzzles.setValue(response.body());
                } else {
                    error.setValue("Error " + response.code() + " al cargar los puzzles");
                }
            }

            @Override
            public void onFailure(Call<List<Puzzle>> call, Throwable excepcion) {
                error.setValue("Fallo de conexión: " + excepcion.getMessage());
            }
        });
    }

    /*
     * BitMap: imagen digital formada por una cuadrícula de puntos individuales llamados píxeles,
     * donde cada píxel tiene un color y posición definidos
     */
    public Bitmap decodarBase64(String imagenBase64) throws ImagenException {
        try {

            // Quitar cabeceras si las hay, ej: data:image/png;base64,
            if (imagenBase64.contains(",")) {
                imagenBase64 = imagenBase64.split(",")[1];
            }

            byte[] bytesDecodificados = Base64.decode(imagenBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytesDecodificados, 0, bytesDecodificados.length);
            if (bitmap == null) {
                throw new ImagenException("El bitmap decodificado es nulo");
            }

            return bitmap;

        } catch (ImagenException e) {
            throw e;

        } catch (Exception e) {
            throw new ImagenException("Error al decodificar la imagen: " + e.getMessage());
        }
    }
}