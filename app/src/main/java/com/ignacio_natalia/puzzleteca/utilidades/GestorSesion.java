package com.ignacio_natalia.puzzleteca.utilidades;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Clase para manejar la sesión del usuario.
 * Guarda y recupera el JWT en SharedPreferences.
 */
public class GestorSesion {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_TOKEN = "JWT_TOKEN";

    // Guardar el token
    public static void guardarToken(Context context, String token) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    // Obtener el token
    public static String obtenerToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    // Cerrar sesión
    public static void cerrarSesion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_TOKEN).apply();
    }
}