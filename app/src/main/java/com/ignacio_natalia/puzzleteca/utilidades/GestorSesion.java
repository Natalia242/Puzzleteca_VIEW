package com.ignacio_natalia.puzzleteca.utilidades;

import android.content.Context;

/**
 * Clase para manejar la sesión del usuario.
 * Guarda y recupera el JWT en SharedPreferences.
 */
public class GestorSesion {

    private static final String PREFS_NAME = "app_prefs";
    private static final String KEY_TOKEN = "JWT_TOKEN";
    private static final String KEY_ROL = "TIPO_USUARIO";
    private static final String KEY_ID_USUARIO = "ID_USUARIO";
    public static void guardarToken(Context context, String token) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_TOKEN, token).apply();
    }

    public static String obtenerToken(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }
    public static void guardarRol(Context context, String rol) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putString(KEY_ROL, rol).apply();
    }
    public static String obtenerRol(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_ROL, "Usuario");
    }

    public static void guardarId_usuario(Context context, int idUsuario) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putInt(KEY_ID_USUARIO, idUsuario).apply();
    }

    public static int obtenerId_usuario(Context context) {
         return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                 .getInt(KEY_ID_USUARIO, -1);
    }

    public static boolean esAdmin(Context context) {
        return "Admin".equals(obtenerRol(context));
    }

    public static void cerrarSesion(Context context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_TOKEN)
                .remove(KEY_ROL)
                .remove(KEY_ID_USUARIO)
                .apply();
    }

}