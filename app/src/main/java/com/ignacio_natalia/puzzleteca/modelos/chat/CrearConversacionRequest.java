package com.ignacio_natalia.puzzleteca.modelos.chat;

import java.util.List;

public class CrearConversacionRequest {

    private List<String> participantes;

    public CrearConversacionRequest(List<String> participantes) {
        this.participantes = participantes;
    }

    public List<String> getParticipantes() {
        return participantes;
    }
}