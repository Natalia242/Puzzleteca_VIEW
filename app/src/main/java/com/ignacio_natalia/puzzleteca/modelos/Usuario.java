package com.ignacio_natalia.puzzleteca.modelos;

public class Usuario {

    public enum TipoUsuario {
        Admin, Bloqueado, Usuario
    }

    private String nombre;
    private String apellido;
    private String email;
    private String contrasena;
    private TipoUsuario tipousuario;

    public Usuario(String nombre, String apellido, String email, String contrasena, TipoUsuario tipousuario) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipousuario = tipousuario;
    }

    // Getters y Setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public TipoUsuario getTipousuario() {
        return tipousuario;
    }

    public void setTipousuario(TipoUsuario tipousuario) {
        this.tipousuario = tipousuario;
    }

}