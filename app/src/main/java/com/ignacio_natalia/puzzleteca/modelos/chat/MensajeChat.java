package com.ignacio_natalia.puzzleteca.modelos.chat;

public class MensajeChat {

    private Long idMensaje;
    private Long idConversacion;
    private Long idUsuario;
    private String nombre;
    private String contenido;
    private String creadoEn;

    public Long getIdMensaje() { return idMensaje; }
    public Long getIdConversacion() { return idConversacion; }
    public Long getIdUsuario() { return idUsuario; }
    public String getNombre() { return nombre; }
    public String getContenido() { return contenido; }
    public String getCreadoEn() { return creadoEn; }
}