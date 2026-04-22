package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.Comentario;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.red.comentarios.ComentarioApi;
import com.ignacio_natalia.puzzleteca.red.comentarios.ServicioApiComentario;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ComentarioRepositorio {

    private ServicioApiComentario servicioApiComentario;

    public ComentarioRepositorio() {
        servicioApiComentario = ComentarioApi.getComentario().create(ServicioApiComentario.class);
    }

    public void obtenerComentarios(String token, Integer idPuzzle, Callback<List<Comentario>> callback) {

        Call<List<Comentario>> call =
                servicioApiComentario.obtenerComentarioPorPuzzle("Bearer " + token, idPuzzle);

        call.enqueue(callback);
    }

    public void crearComentario(Comentario comentario, Callback<Void> callback) {
        Call<Void> call = servicioApiComentario.crearComentario(comentario);
        call.enqueue(callback);
    }

}
