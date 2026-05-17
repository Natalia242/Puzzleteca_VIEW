package com.ignacio_natalia.puzzleteca.modelos.clases;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    public Usuario() {}

    public enum TipoUsuario {Admin, Bloqueado, Usuario}

    private Integer id;
    private String nombre;
    private String apellido;
    private String email;
    private String contrasenna;
    private TipoUsuario tipoUsuario;
    private List<Puzzle> puzzles;

    public Usuario(String nombre, String apellido, String email, String contrasenna, TipoUsuario tipoUsuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasenna = contrasenna;
        this.tipoUsuario = tipoUsuario;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasenna() { return contrasenna; }
    public void setContrasenna(String contrasenna) { this.contrasenna = contrasenna; }

    public TipoUsuario getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(TipoUsuario tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public List<Puzzle> getPuzzles() { return puzzles; }
    public void setPuzzles(List<Puzzle> puzzles) { this.puzzles = puzzles; }

    @NonNull
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", email='" + email + '\'' +
                ", tipoUsuario=" + tipoUsuario +
                '}';
    }
}