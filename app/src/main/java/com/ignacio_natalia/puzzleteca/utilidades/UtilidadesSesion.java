package com.ignacio_natalia.puzzleteca.utilidades;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class UtilidadesSesion {

    public static void cerrarSesion(Context context) {

        GestorSesion.cerrarSesion(context);

        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void eliminarCuenta(Context context, Runnable onSuccess) {

        UsuarioRepositorio repositorio = new UsuarioRepositorio();

        String email = GestorSesion.obtenerEmail(context);

        repositorio.borrarCuenta(email, new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                if (response.isSuccessful()) {

                    Toast.makeText(context, "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();

                    cerrarSesion(context);

                    if (onSuccess != null) {
                        onSuccess.run();
                    }

                } else {
                    Toast.makeText(context, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void mostrarDialogoEliminarCuenta(Context context, Runnable onConfirm) {

        new AlertDialog.Builder(context)
                .setTitle("Eliminar cuenta")
                .setMessage("¿Seguro que quieres eliminar tu cuenta? Esta acción es irreversible.")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    if (onConfirm != null) onConfirm.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public static void mostrarDialogoCerrarSesion(Context context, Runnable onConfirm) {

        new AlertDialog.Builder(context)
                .setTitle("Cerrar sesión")
                .setMessage("¿Seguro que quieres cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> {
                    if (onConfirm != null) onConfirm.run();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
