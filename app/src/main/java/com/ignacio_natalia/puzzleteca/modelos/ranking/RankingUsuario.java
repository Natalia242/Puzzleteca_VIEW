package com.ignacio_natalia.puzzleteca.modelos.ranking;

import com.google.gson.annotations.SerializedName;
public class RankingUsuario {

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("apellido")
    private String apellido;

    @SerializedName("mediaDiaria")
    private Double mediaDiaria;

    @SerializedName("totalValoraciones")
    private Long totalValoraciones;

    // Getters
    public Integer getIdUsuario()         { return idUsuario; }
    public String  getNombre()            { return nombre; }
    public String  getApellido()          { return apellido; }
    public Double  getMediaDiaria()       { return mediaDiaria; }
    public Long    getTotalValoraciones() { return totalValoraciones; }
}