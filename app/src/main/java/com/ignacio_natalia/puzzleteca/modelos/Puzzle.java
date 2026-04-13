package com.ignacio_natalia.puzzleteca.modelos;

public class Puzzle {

    public enum Estados {
        Publico, Privado, Bloqueado
    }

    public enum Dificultades {
        Facil, Media, Dificil, Extremo
    }

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
    public boolean isPublico() { return estado == Estados.Publico; }

}