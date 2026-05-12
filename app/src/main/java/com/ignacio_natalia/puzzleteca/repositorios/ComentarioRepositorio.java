package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.Comentario;
import com.ignacio_natalia.puzzleteca.red.comentarios.ComentarioApi;
import com.ignacio_natalia.puzzleteca.red.comentarios.ServicioApiComentario;
import com.ignacio_natalia.puzzleteca.utilidades.PaginacionComentarios;

import retrofit2.Call;
import retrofit2.Callback;

public class ComentarioRepositorio {

    private final ServicioApiComentario servicioApiComentario;

    public ComentarioRepositorio() {
        servicioApiComentario =
                ComentarioApi.getComentario().create(ServicioApiComentario.class);
    }

    public void obtenerComentariosPost(
            String token,
            Integer idPost,
            int page,
            int size,
            Callback<PaginacionComentarios> callback
    ) {

        Call<PaginacionComentarios> call =
                servicioApiComentario.obtenerComentariosPorPost(
                        "Bearer " + token,
                        idPost,
                        page,
                        size
                );

        call.enqueue(callback);
    }

    public void crearComentario(
            String token,
            Comentario comentario,
            Callback<Comentario> callback
    ) {

        Call<Comentario> call =
                servicioApiComentario.crearComentario(
                        "Bearer " + token,
                        comentario
                );

        call.enqueue(callback);
    }

    public void eliminarComentario(
            String token,
            Integer idComentario,
            Callback<Void> callback
    ) {

        Call<Void> call =
                servicioApiComentario.eliminarComentario(
                        "Bearer " + token,
                        idComentario
                );

        call.enqueue(callback);
    }
}