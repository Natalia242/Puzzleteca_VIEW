package com.ignacio_natalia.puzzleteca.modelos;

public class ConfirmarCambioContrasenaRequest {
    private String email;
    private String codigo;
    private String nuevaPassword;

    public ConfirmarCambioContrasenaRequest(String email, String codigo, String nuevaPassword) {
        this.email = email;
        this.codigo = codigo;
        this.nuevaPassword = nuevaPassword;
    }

    public String getEmail() { return email; }
    public String getCodigo() { return codigo; }
    public String getNuevaPassword() { return nuevaPassword; }

}
