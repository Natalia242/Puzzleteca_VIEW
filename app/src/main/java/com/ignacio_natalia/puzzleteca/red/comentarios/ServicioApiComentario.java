package com.ignacio_natalia.puzzleteca.red.comentarios;

import com.ignacio_natalia.puzzleteca.modelos.Comentario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServicioApiComentario {

    @GET("puzzle/{idPuzzle}")
    Call<List<Comentario>> obtenerComentarioPorPuzzle(
            @Header("Authorization") String token,
            @Path("idPuzzle") Integer idPuzzle
    );

    @POST("comentario")
    Call<Void> crearComentario(@Body Comentario comentario);
}
