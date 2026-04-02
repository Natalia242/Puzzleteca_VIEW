package com.ignacio_natalia.puzzleteca.red;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
public interface ServiciosApiPuzzles {

    @GET("obtenerPuzzles")
    Call<List<Puzzle>> obtenerPuzzles(@Header("Authorization") String token);
}