package com.ignacio_natalia.puzzleteca.modelos;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Puzzle implements Serializable {

    public enum Estados {Publico, Privado, Bloqueado}
    public enum Dificultades {Facil, Media, Dificil, Extremo}

    private Integer id;
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
    /** URL pública de la imagen (nueva estrategia: fichero en servidor, no base64) */
    private String imagenUrl;
    private transient Bitmap bitmap;

    // ---------- GETTERS ----------
    public Integer getId() { return id; }

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
    public String getImagenUrl() { return imagenUrl; }
    public Bitmap getBitmap() { return bitmap; }
    public boolean isPublico() {
        return estado == Estados.Publico;
    }

    // ---------- SETTERS ----------
    public void setId(Integer id) { this.id = id; }

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
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    @NonNull
    @Override
    public String toString() {
        return "Puzzle{" +
                "id_puzzle=" + id +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", tiempo=" + tiempo +
                ", piezas=" + piezas +
                ", dificultad=" + dificultad +
                ", descripcion='" + descripcion + '\'' +
                ", color=" + color +
                ", valoracion=" + valoracion +
                ", idUsuario=" + idUsuario +
                ", estado=" + estado +
                '}';
    }

}