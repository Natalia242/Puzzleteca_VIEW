package com.ignacio_natalia.puzzleteca.red;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServiciosApiPuzzles {

    @GET("obtenerPuzzles")
    Call<List<Puzzle>> obtenerPuzzles(@Header("Authorization") String token);

    @POST("registrarPuzzle")
    Call<Void> crearPuzzle(@Body Puzzle puzzle);
}