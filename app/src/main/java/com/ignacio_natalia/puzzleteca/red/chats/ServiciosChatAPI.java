package com.ignacio_natalia.puzzleteca.red.chats;

import com.ignacio_natalia.puzzleteca.modelos.chat.*;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiciosChatAPI {

    @POST("crearConversacion")
    Call<ConversacionRespuesta> crearConversacion(
            @Body CrearConversacionRequest request
    );

    @GET("mensajes/{id}")
    Call<List<MensajeChat>> obtenerMensajes(
            @Path("id") int idConversacion
    );
}