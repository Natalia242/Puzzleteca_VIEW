package com.ignacio_natalia.puzzleteca.modelos.actualizar;

public class ActualizarPuzzleRequest {

    private Integer idUsuario;
    private Integer idPuzzle;
    private String atributo;
    private String cambio;

    public ActualizarPuzzleRequest() {}

    public ActualizarPuzzleRequest(Integer idUsuario, Integer idPuzzle,
                                   String atributo, String cambio) {
        this.idUsuario = idUsuario;
        this.idPuzzle  = idPuzzle;
        this.atributo  = atributo;
        this.cambio    = cambio;
    }

    public Integer getIdUsuario() { return idUsuario; }
    public Integer getIdPuzzle()  { return idPuzzle;  }
    public String  getAtributo()  { return atributo;  }
    public String  getCambio()    { return cambio;    }

    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }
    public void setIdPuzzle(Integer idPuzzle)   { this.idPuzzle  = idPuzzle;  }
    public void setAtributo(String atributo)    { this.atributo  = atributo;  }
    public void setCambio(String cambio)        { this.cambio    = cambio;    }
}