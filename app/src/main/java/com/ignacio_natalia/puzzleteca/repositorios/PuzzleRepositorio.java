package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.red.puzzles.PuzzleApi;
import com.ignacio_natalia.puzzleteca.red.puzzles.ServiciosApiPuzzles;

import java.io.File;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class PuzzleRepositorio {

    private final ServiciosApiPuzzles api;

    public PuzzleRepositorio() {
        api = PuzzleApi.getCliente().create(ServiciosApiPuzzles.class);
    }

    public void obtenerPuzzles(String token, String estado, Callback<List<Puzzle>> callback) {
        api.obtenerPuzzles("Bearer " + token, estado).enqueue(callback);
    }

    public void misPuzzles(String token, Integer idUsuario, Callback<List<Puzzle>> callback) {
        api.misPuzzles("Bearer " + token, idUsuario).enqueue(callback);
    }

    /**
     * Envía el puzzle al backend como multipart/form-data.
     * Si hay imagen la incluye como Part binario; el backend guarda la ruta en disco.
     */
    public void crearPuzzle(String token, Puzzle puzzle, File imagenFile, Callback<Void> callback) {
        RequestBody titulo      = toText(puzzle.getTitulo());
        RequestBody autor       = toText(puzzle.getAutor());
        RequestBody tiempo      = toText(String.valueOf(puzzle.getTiempo()));
        RequestBody piezas      = toText(String.valueOf(puzzle.getPiezas()));
        RequestBody dificultad  = toText(puzzle.getDificultad().name());
        RequestBody descripcion = toText(puzzle.getDescripcion() != null ? puzzle.getDescripcion() : "");
        RequestBody color       = toText(String.valueOf(puzzle.isColor()));
        RequestBody estado      = toText(puzzle.getEstado().name());
        RequestBody idUsuario   = toText(String.valueOf(puzzle.getIdUsuario()));

        if (imagenFile != null && imagenFile.exists()) {
            RequestBody imagenBody = RequestBody.create(MediaType.parse("image/jpeg"), imagenFile);
            MultipartBody.Part imagenPart = MultipartBody.Part.createFormData("imagen",
                    imagenFile.getName(), imagenBody);
            api.crearPuzzle("Bearer " + token, titulo, autor, tiempo, piezas,
                            dificultad, descripcion, color, estado, idUsuario, imagenPart)
                    .enqueue(callback);
        } else {
            api.crearPuzzleSinImagen("Bearer " + token, titulo, autor, tiempo, piezas,
                            dificultad, descripcion, color, estado, idUsuario)
                    .enqueue(callback);
        }
    }

    public void cambiarEstadoPuzzle(Integer id_usuario, Integer id_puzzle, String tipo,
                                    Callback<Void> callback) {
        api.cambiarEstadoPuzzle(id_usuario, id_puzzle, tipo).enqueue(callback);
    }

    private static RequestBody toText(String value) {
        return RequestBody.create(MediaType.parse("text/plain"),
                value != null ? value : "");
    }
}
