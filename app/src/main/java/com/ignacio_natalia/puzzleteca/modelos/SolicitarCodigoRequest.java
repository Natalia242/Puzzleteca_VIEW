package com.ignacio_natalia.puzzleteca.modelos;

public class SolicitarCodigoRequest {
    private String email;

    public SolicitarCodigoRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }
}
