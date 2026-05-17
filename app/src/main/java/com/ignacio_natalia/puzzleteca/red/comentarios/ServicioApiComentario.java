package com.ignacio_natalia.puzzleteca.red.comentarios;

import com.ignacio_natalia.puzzleteca.modelos.comentarios.Comentario;
import com.ignacio_natalia.puzzleteca.modelos.paginacion.PaginacionComentarios;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ServicioApiComentario {

    @GET("comentarios/post/{idPost}")
    Call<PaginacionComentarios> obtenerComentariosPorPost(
            @Header("Authorization") String token,
            @Path("idPost") Integer idPost,
            @Query("page") int page,
            @Query("size") int size
    );

    @POST("comentarios/crearComentario")
    Call<Comentario> crearComentario(
            @Header("Authorization") String token,
            @Body Comentario comentario
    );

    @DELETE("comentarios/{idComentario}")
    Call<Void> eliminarComentario(
            @Header("Authorization") String token,
            @Path("idComentario") Integer idComentario
    );
}