package com.ignacio_natalia.puzzleteca.modelos.post;

import com.google.gson.annotations.SerializedName;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
public class Post {

    @SerializedName("id")
    private Integer id;

    @SerializedName("contenido")
    private String contenido;

    /**
     * URL pública de la imagen ya construida por el backend.
     * Ej: "http://10.0.2.2:8080/imagenes/posts/uuid.jpg"
     */
    @SerializedName("imagenUrl")
    private String imagenUrl;

    @SerializedName("fechaCreacion")
    private String fechaCreacion;

    @SerializedName("totalLikes")
    private Integer totalLikes;

    @SerializedName("totalComentarios")
    private Integer totalComentarios;

    @SerializedName("idUsuario")
    private Integer idUsuario;

    @SerializedName("nombreUsuario")
    private String nombreUsuario;
    @SerializedName("puzzle")
    private Puzzle puzzle;


    public Integer getId()             { return id; }
    public String getContenido()       { return contenido; }
    public String getImagenUrl()       { return imagenUrl; }
    public String getFechaCreacion()   { return fechaCreacion; }
    public Integer getTotalLikes()     { return totalLikes != null ? totalLikes : 0; }
    public Integer getTotalComentarios(){ return totalComentarios != null ? totalComentarios : 0; }
    public Integer getIdUsuario()      { return idUsuario; }
    public String getNombreUsuario()   { return nombreUsuario; }

    public Puzzle getPuzzle() { return puzzle; }


    public void setId(Integer id)                        { this.id = id; }
    public void setContenido(String contenido)           { this.contenido = contenido; }
    public void setImagenUrl(String imagenUrl)           { this.imagenUrl = imagenUrl; }
    public void setFechaCreacion(String fechaCreacion)   { this.fechaCreacion = fechaCreacion; }
    public void setTotalLikes(Integer totalLikes)        { this.totalLikes = totalLikes; }
    public void setTotalComentarios(Integer total)       { this.totalComentarios = total; }
    public void setIdUsuario(Integer idUsuario)          { this.idUsuario = idUsuario; }
    public void setNombreUsuario(String nombreUsuario)   { this.nombreUsuario = nombreUsuario; }

    public void setPuzzle(Puzzle puzzle) { this.puzzle = puzzle; }

}