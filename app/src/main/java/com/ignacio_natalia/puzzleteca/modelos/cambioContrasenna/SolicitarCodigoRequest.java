package com.ignacio_natalia.puzzleteca.modelos.cambioContrasenna;

public class SolicitarCodigoRequest {

    private final String email;

    public SolicitarCodigoRequest(String email) {
        this.email = email;
    }

    public String getEmail() { return email; }

}