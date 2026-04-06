package com.ignacio_natalia.puzzleteca.modelos;

public class LoginRequest {

    private String email;
    private String contrasena;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.contrasena = password;
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

}