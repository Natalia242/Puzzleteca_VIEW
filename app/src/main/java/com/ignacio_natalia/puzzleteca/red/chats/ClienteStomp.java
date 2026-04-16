package com.ignacio_natalia.puzzleteca.red.chats;

import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class ClienteStomp {

    private static StompClient stompClient;

    public static StompClient conectar() {

        if (stompClient != null) {
            stompClient.disconnect();
        }

        stompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://10.0.2.2:8080/ws"
        );

        stompClient.connect();

        return stompClient;
    }

    public static StompClient getClient() {
        return stompClient;
    }
}