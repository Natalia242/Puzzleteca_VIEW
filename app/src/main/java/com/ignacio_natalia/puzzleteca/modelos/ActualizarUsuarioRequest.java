package com.ignacio_natalia.puzzleteca.modelos;

public class ActualizarUsuarioRequest {

    private String email;
    private String atributo;
    private String cambio;

    public ActualizarUsuarioRequest(String email, String atributo, String cambio) {
        this.email = email;
        this.atributo = atributo;
        this.cambio = cambio;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAtributo() { return atributo; }
    public void setAtributo(String atributo) { this.atributo = atributo; }

    public String getCambio() { return cambio; }
    public void setCambio(String cambio) { this.cambio = cambio; }
}