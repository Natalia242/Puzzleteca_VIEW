package com.ignacio_natalia.puzzleteca.modelos.comentarios;

import com.google.gson.annotations.SerializedName;
public class ComentarioPost {

    @SerializedName("id")
    private Integer id;

    @SerializedName("contenido")
    private String contenido;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;

    @SerializedName("idPost")
    private Integer idPost;

    public Integer getId()           { return id; }
    public String getContenido()     { return contenido; }
    public String getFechaCreacion() { return fechaCreacion; }
    public Integer getIdUsuario()    { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public Integer getIdPost()       { return idPost; }

    public void setId(Integer id)                       { this.id = id; }
    public void setContenido(String contenido)          { this.contenido = contenido; }
    public void setFechaCreacion(String fechaCreacion)  { this.fechaCreacion = fechaCreacion; }
    public void setIdUsuario(Integer idUsuario)         { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario)  { this.nombreUsuario = nombreUsuario; }
    public void setIdPost(Integer idPost)               { this.idPost = idPost; }
}