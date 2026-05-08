package com.ignacio_natalia.puzzleteca.utilidades;

import com.ignacio_natalia.puzzleteca.modelos.Comentario;

import java.util.List;

public class PaginacionComentarios {

    private List<Comentario> content;
    private boolean last;

    public List<Comentario> getContent() {
        return content;
    }

    public void setContent(List<Comentario> content) {
        this.content = content;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}