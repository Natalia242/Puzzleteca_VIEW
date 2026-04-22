package com.ignacio_natalia.puzzleteca.modelos;

public class Comentario {
    private String contenido;
    private Integer id_usuario;
    private Integer id_puzzle;

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Integer getId_puzzle() {
        return id_puzzle;
    }

    public void setId_puzzle(Integer id_puzzle) {
        this.id_puzzle = id_puzzle;
    }
}
