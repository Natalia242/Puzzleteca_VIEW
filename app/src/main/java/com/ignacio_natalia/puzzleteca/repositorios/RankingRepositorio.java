package com.ignacio_natalia.puzzleteca.repositorios;

import com.ignacio_natalia.puzzleteca.modelos.RankingUsuario;
import com.ignacio_natalia.puzzleteca.modelos.ValorarRequest;
import com.ignacio_natalia.puzzleteca.red.ranking.RankingApi;
import com.ignacio_natalia.puzzleteca.red.ranking.ServiciosApiRanking;

import java.util.List;

import retrofit2.Callback;

public class RankingRepositorio {

    private final ServiciosApiRanking api;

    public RankingRepositorio() {
        api = RankingApi.getCliente().create(ServiciosApiRanking.class);
    }

    public void obtenerRankingDiario(String token, Callback<List<RankingUsuario>> callback) {
        api.obtenerRankingDiario("Bearer " + token).enqueue(callback);
    }

    public void valorarPuzzle(String token, Integer idPuzzle,
                              Integer idUsuario, Integer valoracion,
                              Callback<Void> callback) {
        ValorarRequest body = new ValorarRequest(idPuzzle, idUsuario, valoracion);
        api.valorarPuzzle("Bearer " + token, body).enqueue(callback);
    }
}