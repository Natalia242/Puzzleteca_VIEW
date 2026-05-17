package com.ignacio_natalia.puzzleteca.modelos.ranking;

/**
 * Body JSON para POST /ranking/valorar
 */
public class ValorarRequest {

    private Integer idPuzzle;
    private Integer idUsuario;
    private Integer valoracion;

    public ValorarRequest(Integer idPuzzle, Integer idUsuario, Integer valoracion) {
        this.idPuzzle   = idPuzzle;
        this.idUsuario  = idUsuario;
        this.valoracion = valoracion;
    }

    public Integer getIdPuzzle()   { return idPuzzle; }
    public Integer getIdUsuario()  { return idUsuario; }
    public Integer getValoracion() { return valoracion; }
}