package com.ignacio_natalia.puzzleteca.modelos;

import android.graphics.Bitmap;

public class Puzzle {

    public enum Estados {
        Publico, Privado, Bloqueado
    }

    public enum Dificultades {
        Facil, Media, Dificil, Extremo
    }

    private Integer id_puzzle;
    private String titulo;
    private String autor;
    private Integer tiempo;
    private Integer piezas;
    private Dificultades dificultad;
    private String descripcion;
    private Boolean color;
    private Integer valoracion;
    private Integer idUsuario;
    private Estados estado;
    private String imagenBase64;
    private Bitmap bitmap;


    // ---------- GETTERS ----------

    public Integer getId_puzzle() { return id_puzzle; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public Integer getTiempo() { return tiempo; }
    public Integer getPiezas() { return piezas; }
    public Dificultades getDificultad() { return dificultad; }
    public String getDescripcion() { return descripcion; }
    public Boolean isColor() { return color; }
    public Integer getValoracion() { return valoracion; }
    public Integer getIdUsuario() { return idUsuario; }
    public Estados getEstado() { return estado; }
    public String getImagenBase64() { return imagenBase64; }
    public Bitmap getBitmap() { return bitmap; }
    public boolean isPublico() {
        return estado == Estados.Publico;
    }

    public void setId_puzzle(Integer id_puzzle) { this.id_puzzle = id_puzzle; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setTiempo(Integer tiempo) { this.tiempo = tiempo; }
    public void setPiezas(Integer piezas) { this.piezas = piezas; }
    public void setDificultad(Dificultades dificultad) { this.dificultad = dificultad; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setColor(Boolean color) { this.color = color; }
    public void setValoracion(Integer valoracion) { this.valoracion = valoracion; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public void setEstado(Estados estado) { this.estado = estado; }
    public void setImagenBase64(String imagenBase64) { this.imagenBase64 = imagenBase64; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }

}