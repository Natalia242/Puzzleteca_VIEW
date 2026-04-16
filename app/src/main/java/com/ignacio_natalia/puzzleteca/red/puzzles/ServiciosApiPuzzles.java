package com.ignacio_natalia.puzzleteca.red.puzzles;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ServiciosApiPuzzles {

    @GET("obtenerPuzzles")
    Call<List<Puzzle>> obtenerPuzzles(@Header("Authorization") String token);

    @POST("registrarPuzzle")
    Call<Void> crearPuzzle(@Body Puzzle puzzle);

    @PUT("actualizarEstado")
    Call<Void> cambiarEstadoPuzzle(@Query("id_usuario") Integer id_usuario,
                                   @Query("id_puzzle") Integer id_puzzle,
                                   @Query("tipo") String tipo);
}