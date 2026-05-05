package com.ignacio_natalia.puzzleteca.red.ranking;

import com.ignacio_natalia.puzzleteca.modelos.RankingUsuario;
import com.ignacio_natalia.puzzleteca.modelos.ValorarRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ServiciosApiRanking {

    /** GET /ranking/diario — lista ordenada por media DESC */
    @GET("ranking/diario")
    Call<List<RankingUsuario>> obtenerRankingDiario(
            @Header("Authorization") String token
    );

    /** POST /ranking/valorar — envía la puntuación de un puzzle */
    @POST("ranking/valorar")
    Call<Void> valorarPuzzle(
            @Header("Authorization") String token,
            @Body ValorarRequest body
    );
}