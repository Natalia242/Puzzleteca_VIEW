package com.ignacio_natalia.puzzleteca.modelos;

public class ConfirmarCambioContrasenaRequest {

    private final String email;
    private final String codigo;
    private final String nuevaContrasena;

    public ConfirmarCambioContrasenaRequest(String email, String codigo, String nuevaContrasenna) {
        this.email = email;
        this.codigo = codigo;
        this.nuevaContrasena = nuevaContrasenna;
    }

    public String getEmail() { return email; }
    public String getCodigo() { return codigo; }
    public String getNuevaContrasena() { return nuevaContrasena; }

}