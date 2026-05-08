package com.ignacio_natalia.puzzleteca.modelos.chat;

import java.util.List;

public class CrearConversacionRequest {

    private List<Integer> participantes;

    public CrearConversacionRequest(List<Integer> participantes) {
        this.participantes = participantes;
    }

    public List<Integer> getParticipantes() {
        return participantes;
    }
}