package com.ignacio_natalia.puzzleteca.modelos;

public class LoginRespuesta {

    private String token;
    private Integer id_usuario;
    private String tipoUsuario;
    private String nombre;

    public String getToken() {
        return token;
    }

    public Integer getId_usuario() { return id_usuario; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getNombre() { return nombre; }

}