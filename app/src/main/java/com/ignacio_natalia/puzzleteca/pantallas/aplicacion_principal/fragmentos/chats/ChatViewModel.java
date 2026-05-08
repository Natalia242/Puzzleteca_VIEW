package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.modelos.chat.*;
import com.ignacio_natalia.puzzleteca.red.chats.*;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.StompClient;

public class ChatViewModel extends ViewModel {

    private final ServiciosChatAPI api;
    private StompClient stompClient;
    private boolean conectado = false;

    private final UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();

    private final MutableLiveData<List<Usuario>> usuarios = new MutableLiveData<>();
    private final MutableLiveData<List<MensajeChat>> mensajes = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> conversacionId = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public ChatViewModel() {
        api = RetrofitChatApi.getCliente().create(ServiciosChatAPI.class);
    }

    public LiveData<List<Usuario>> getUsuarios() { return usuarios; }
    public LiveData<List<MensajeChat>> getMensajes() { return mensajes; }
    public LiveData<Integer> getConversacionId() { return conversacionId; }
    public LiveData<String> getError() { return error; }
    public void cargarUsuarios(String token) {
        usuarioRepositorio.listarUsuarios(token, new Callback<List<Usuario>>() {
            @Override
            public void onResponse(Call<List<Usuario>> call, Response<List<Usuario>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    usuarios.setValue(response.body());
                } else {
                    error.setValue("Error al cargar usuarios: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Usuario>> call, Throwable t) {
                error.setValue("Fallo de red: " + t.getMessage());
            }
        });
    }
    public void crearConversacion(CrearConversacionRequest req) {
        api.crearConversacion(req).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<ConversacionRespuesta> call, Response<ConversacionRespuesta> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int id = response.body().getIdConversacion().intValue();
                    Log.d("CHAT", "Conversación creada: " + id);
                    conversacionId.setValue(id);
                } else {
                    error.setValue("Error al crear conversación");
                }
            }

            @Override
            public void onFailure(Call<ConversacionRespuesta> call, Throwable excepcion) {
                error.setValue("Fallo de red: " + excepcion.getMessage());
            }
        });
    }
    public void cargarMensajes(int idConversacion) {

        Log.d("CHAT", "Cargando mensajes conversación: " + idConversacion);

        api.obtenerMensajes(idConversacion).enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<List<MensajeChat>> call, Response<List<MensajeChat>> response) {
                if (response.isSuccessful() && response.body() != null) {

                    List<MensajeChat> lista = response.body();

                    Log.d("CHAT", "Mensajes recibidos REST: " + lista.size());

                    mensajes.setValue(new ArrayList<>(lista));

                } else {
                    Log.e("CHAT", "Error respuesta mensajes: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<MensajeChat>> call, Throwable e) {
                Log.e("CHAT", "Error cargando mensajes", e);
            }
        });
    }
    public void conectarWebSocket(int idConversacion) {

        if (conectado) {
            Log.d("STOMP", "Ya conectado, evitando duplicado");
            return;
        }

        Log.d("STOMP", "Conectando WS...");
        conectado = true;

        stompClient = ClienteStomp.conectar();

        stompClient.lifecycle().subscribe(event -> {

            switch (event.getType()) {

                case OPENED:
                    Log.d("STOMP", "Conectado");

                    stompClient.topic("/topic/conversacion/" + idConversacion)
                            .subscribe(topicMessage -> {

                                MensajeChat mensaje = new Gson()
                                        .fromJson(topicMessage.getPayload(), MensajeChat.class);

                                Log.d("CHAT", "WS recibido: " + mensaje.getContenido());

                                List<MensajeChat> actual = mensajes.getValue();
                                if (actual == null) actual = new ArrayList<>();

                                List<MensajeChat> nueva = new ArrayList<>(actual);
                                nueva.add(mensaje);

                                mensajes.postValue(nueva);

                            }, error -> Log.e("STOMP", "Error topic", error));

                    break;

                case ERROR:
                    Log.e("STOMP", "Error conexión", event.getException());
                    conectado = false;
                    break;

                case CLOSED:
                    Log.d("STOMP", "Cerrado");
                    conectado = false;
                    break;
            }

        }, error -> Log.e("STOMP", "Error lifecycle", error));
    }
    public void enviarMensaje(int idUsuario, int idConversacion, String contenido) {

        if (stompClient == null || !stompClient.isConnected()) {
            Log.e("STOMP", "No conectado");
            return;
        }

        String json = "{"
                + "\"idUsuario\":" + idUsuario + ","
                + "\"idConversacion\":" + idConversacion + ","
                + "\"contenido\":\"" + contenido + "\""
                + "}";

        stompClient.send("/app/chat.enviar", json)
                .subscribe(
                        () -> Log.d("STOMP", "Mensaje enviado"),
                        error -> Log.e("STOMP", "Error enviando", error)
                );
    }

    private String nombreSeleccionado = "";

    public void setNombreSeleccionado(String nombre) {
        this.nombreSeleccionado = nombre;
    }

    public String getNombreSeleccionado() {
        return nombreSeleccionado;
    }

    public void limpiarConversacionId() {
        conversacionId.setValue(null);
    }

    public void desconectar() {
        if (stompClient != null) {
            stompClient.disconnect();
        }
        conectado = false;
    }
}