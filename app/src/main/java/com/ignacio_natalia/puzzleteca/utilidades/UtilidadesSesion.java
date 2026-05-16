package com.ignacio_natalia.puzzleteca.utilidades;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtilidadesSesion {


    // ══════════════════════════════════════════════════════════════════════════
    //  Diálogo personalizado genérico reutilizable en toda la app
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Muestra un diálogo de confirmación completamente estilizado al look de Puzzleteca.
     *
     * @param context        Contexto
     * @param emoji          Emoji grande encabezando el diálogo  (ej. "🚪")
     * @param titulo         Título principal
     * @param mensaje        Texto descriptivo / advertencia
     * @param textoConfirmar Etiqueta del botón de acción
     * @param colorConfirmar Color hex del botón de acción
     * @param onConfirm      Runnable ejecutado al confirmar (puede ser null)
     */
    @SuppressLint("SetTextI18n")
    public static void mostrarDialogoPersonalizado(
            Context context,
            String emoji,
            String titulo,
            String mensaje,
            String textoConfirmar,
            String colorConfirmar,
            Runnable onConfirm) {

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // ── Tarjeta raíz ─────────────────────────────────────────────────────
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(dp(context, 24), dp(context, 28), dp(context, 24), dp(context, 20));

        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setColor(Color.WHITE);
        rootBg.setCornerRadius(dp(context, 20));
        root.setBackground(rootBg);

        // ── Círculo de emoji ──────────────────────────────────────────────────
        boolean esPeligro = "#E53935".equalsIgnoreCase(colorConfirmar);

        TextView tvEmoji = new TextView(context);
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(30);
        tvEmoji.setGravity(Gravity.CENTER);
        int circleSize = dp(context, 68);
        LinearLayout.LayoutParams emojiLP =
                new LinearLayout.LayoutParams(circleSize, circleSize);
        emojiLP.gravity = Gravity.CENTER_HORIZONTAL;
        emojiLP.bottomMargin = dp(context, 16);
        tvEmoji.setLayoutParams(emojiLP);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(ContextCompat.getColor(context, esPeligro ? R.color.app_peligro_bg : R.color.app_teal_soft));
        tvEmoji.setBackground(circleBg);

        // ── Título ────────────────────────────────────────────────────────────
        TextView tvTitulo = new TextView(context);
        tvTitulo.setText(titulo);
        tvTitulo.setTextSize(18);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(context, R.color.app_texto));
        tvTitulo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tituloLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloLP.bottomMargin = dp(context, 8);
        tvTitulo.setLayoutParams(tituloLP);

        // ── Mensaje ───────────────────────────────────────────────────────────
        TextView tvMensaje = new TextView(context);
        tvMensaje.setText(mensaje);
        tvMensaje.setTextSize(14);
        tvMensaje.setTextColor(ContextCompat.getColor(context, R.color.app_subtexto));
        tvMensaje.setGravity(Gravity.CENTER);
        tvMensaje.setLineSpacing(dp(context, 2), 1f);
        LinearLayout.LayoutParams msgLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        msgLP.bottomMargin = dp(context, 24);
        tvMensaje.setLayoutParams(msgLP);

        // ── Botón confirmar ───────────────────────────────────────────────────
        TextView btnConfirmar = new TextView(context);
        btnConfirmar.setText(textoConfirmar);
        btnConfirmar.setTextColor(Color.WHITE);
        btnConfirmar.setTextSize(15);
        btnConfirmar.setTypeface(null, Typeface.BOLD);
        btnConfirmar.setGravity(Gravity.CENTER);
        btnConfirmar.setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 14));
        LinearLayout.LayoutParams btnCP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnCP.bottomMargin = dp(context, 8);
        btnConfirmar.setLayoutParams(btnCP);
        btnConfirmar.setClickable(true);
        btnConfirmar.setFocusable(true);
        GradientDrawable confirmBg = new GradientDrawable();
        confirmBg.setColor(ContextCompat.getColor(context, esPeligro ? R.color.app_peligro : R.color.app_teal_dark));
        confirmBg.setCornerRadius(dp(context, 12));
        btnConfirmar.setBackground(confirmBg);
        btnConfirmar.setOnClickListener(v -> {
            dialog.dismiss();
            if (onConfirm != null) onConfirm.run();
        });

        // ── Botón cancelar ────────────────────────────────────────────────────
        TextView btnCancelar = new TextView(context);
        btnCancelar.setText("Cancelar");
        btnCancelar.setTextColor(ContextCompat.getColor(context, R.color.app_subtexto));
        btnCancelar.setTextSize(14);
        btnCancelar.setGravity(Gravity.CENTER);
        btnCancelar.setPadding(dp(context, 16), dp(context, 12), dp(context, 16), dp(context, 12));
        btnCancelar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        btnCancelar.setClickable(true);
        btnCancelar.setFocusable(true);
        GradientDrawable cancelBg = new GradientDrawable();
        cancelBg.setColor(ContextCompat.getColor(context, R.color.app_fondo_cancelar));
        cancelBg.setCornerRadius(dp(context, 12));
        btnCancelar.setBackground(cancelBg);
        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        // ── Montaje ───────────────────────────────────────────────────────────
        root.addView(tvEmoji);
        root.addView(tvTitulo);
        root.addView(tvMensaje);
        root.addView(btnConfirmar);
        root.addView(btnCancelar);

        // La clave: poner el fondo transparente en la ventana Y fijar el ancho
        // DESPUÉS de show() para que getWindow().setLayout() tenga efecto real.
        dialog.setContentView(root);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

        if (dialog.getWindow() != null) {
            int screenW = context.getResources().getDisplayMetrics().widthPixels;
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (screenW * 0.88f),
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  API pública — misma firma que antes, ahora con diálogos estilizados
    // ══════════════════════════════════════════════════════════════════════════

    public static void cerrarSesion(Context context) {
        GestorSesion.cerrarSesion(context);
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void mostrarDialogoCerrarSesion(Context context, Runnable onConfirm) {
        mostrarDialogoPersonalizado(
                context,
                "🚪",
                "Cerrar sesión",
                "¿Seguro que quieres salir de tu cuenta?",
                "Cerrar sesión",
                "#2E7D6E",
                onConfirm
        );
    }

    public static void mostrarDialogoEliminarCuenta(Context context, Runnable onConfirm) {
        mostrarDialogoPersonalizado(
                context,
                "⚠️",
                "Eliminar cuenta",
                "Esta acción es irreversible.\nTodos tus datos se perderán definitivamente.",
                "Eliminar cuenta",
                "#E53935",
                onConfirm
        );
    }

    public static void eliminarCuenta(Context context, Runnable onSuccess) {
        UsuarioRepositorio repositorio = new UsuarioRepositorio();
        String email = GestorSesion.obtenerEmail(context);
        repositorio.borrarCuenta(email, new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Cuenta eliminada correctamente",
                            Toast.LENGTH_SHORT).show();
                    cerrarSesion(context);
                    if (onSuccess != null) onSuccess.run();
                } else {
                    Toast.makeText(context, "Error al eliminar la cuenta",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private static int dp(Context ctx, int v) {
        return Math.round(v * ctx.getResources().getDisplayMetrics().density);
    }
}