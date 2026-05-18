package com.ignacio_natalia.puzzleteca.modelos.ranking;

/**
 * Body JSON para POST /ranking/valorar
 * El idUsuario ya NO se envía: el servidor lo extrae del token JWT.
 */
public class ValorarRequest {

    private Integer idPuzzle;
    private Float valoracion;

    public ValorarRequest(Integer idPuzzle, Float valoracion) {
        this.idPuzzle   = idPuzzle;
        this.valoracion = valoracion;
    }

    public Integer getIdPuzzle() {
        return idPuzzle;
    }

    public Float getValoracion() {
        return valoracion;
    }
}