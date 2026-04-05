package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PanelAdmin extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // ── Tarjeta Admin ──
        LinearLayout tarjetaAdmin = crearTarjeta();
        tarjetaAdmin.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaAdmin.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaAdmin.setPadding(40, 30, 40, 30);

        TextView tvIcono = new TextView(requireContext());
        tvIcono.setText("🛡️");
        tvIcono.setTextSize(36);
        tvIcono.setPadding(0, 0, 24, 0);

        LinearLayout infoAdmin = new LinearLayout(requireContext());
        infoAdmin.setOrientation(LinearLayout.VERTICAL);

        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText("Admin Panel");
        tvNombre.setTextSize(18);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(Color.parseColor("#37474F"));

        TextView tvId = new TextView(requireContext());
        tvId.setText("Admin to: 00001");
        tvId.setTextSize(13);
        tvId.setTextColor(Color.parseColor("#78909C"));

        infoAdmin.addView(tvNombre);
        infoAdmin.addView(tvId);

        tarjetaAdmin.addView(tvIcono);
        tarjetaAdmin.addView(infoAdmin);
        layout.addView(tarjetaAdmin);
        espacio(layout, 20);

        // ── Opciones ──
        layout.addView(crearOpcion("✏️", "Editar Perfil"));
        layout.addView(crearOpcion("🧩", "Mis Puzzles"));
        layout.addView(crearOpcion("💬", "Mis Chats Privados"));
        espacio(layout, 10);

        // ── Tarjeta Mejor Puzzle ──
        LinearLayout tarjetaMejor = crearTarjeta();
        tarjetaMejor.setOrientation(LinearLayout.VERTICAL);
        tarjetaMejor.setPadding(40, 30, 40, 30);

        TextView tvMejorLabel = new TextView(requireContext());
        tvMejorLabel.setText("⭐ Mejor Puzzle");
        tvMejorLabel.setTextSize(15);
        tvMejorLabel.setTypeface(null, Typeface.BOLD);
        tvMejorLabel.setTextColor(Color.parseColor("#37474F"));
        tarjetaMejor.addView(tvMejorLabel);
        espacio(tarjetaMejor, 10);

        LinearLayout filaMejor = new LinearLayout(requireContext());
        filaMejor.setOrientation(LinearLayout.HORIZONTAL);
        filaMejor.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvPuzzleNombre = new TextView(requireContext());
        tvPuzzleNombre.setText("🌸 Flor Amarilla");
        tvPuzzleNombre.setTextSize(14);
        tvPuzzleNombre.setTextColor(Color.parseColor("#37474F"));
        tvPuzzleNombre.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout colDerecha = new LinearLayout(requireContext());
        colDerecha.setOrientation(LinearLayout.VERTICAL);
        colDerecha.setGravity(Gravity.END);

        TextView tvValoracion = new TextView(requireContext());
        tvValoracion.setText("4.5");
        tvValoracion.setTextSize(13);
        tvValoracion.setTextColor(Color.parseColor("#78909C"));

        TextView tvBadge = new TextView(requireContext());
        tvBadge.setText("02:10");
        tvBadge.setTextSize(13);
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setTextColor(Color.WHITE);
        tvBadge.setPadding(20, 10, 20, 10);
        GradientDrawable badgeShape = new GradientDrawable();
        badgeShape.setColor(Color.parseColor("#26A69A"));
        badgeShape.setCornerRadius(60);
        tvBadge.setBackground(badgeShape);

        colDerecha.addView(tvValoracion);
        colDerecha.addView(tvBadge);

        filaMejor.addView(tvPuzzleNombre);
        filaMejor.addView(colDerecha);
        tarjetaMejor.addView(filaMejor);
        layout.addView(tarjetaMejor);
        espacio(layout, 10);

        // ── Opciones de gestión ──
        layout.addView(crearOpcion("🧩", "Gestionar Puzzles"));
        layout.addView(crearOpcion("👥", "Gestionar Usuarios"));
        layout.addView(crearOpcion("🏆", "Ranking Diario"));
        layout.addView(crearOpcion("🔒", "Bloquear / Desbloquear"));

        scroll.addView(layout);
        return scroll;
    }

    private LinearLayout crearTarjeta() {
        LinearLayout t = new LinearLayout(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 14);
        t.setLayoutParams(lp);
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(2, Color.parseColor("#A5D6A7"));
        t.setBackground(fondo);
        return t;
    }

    private LinearLayout crearOpcion(String emoji, String texto) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(40, 28, 40, 28);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, 10);
        fila.setLayoutParams(lp);
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(2, Color.parseColor("#A5D6A7"));
        fila.setBackground(fondo);

        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(20);
        tvEmoji.setPadding(0, 0, 20, 0);

        TextView tvTexto = new TextView(requireContext());
        tvTexto.setText(texto);
        tvTexto.setTextSize(15);
        tvTexto.setTextColor(Color.parseColor("#37474F"));
        tvTexto.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvFlecha = new TextView(requireContext());
        tvFlecha.setText("›");
        tvFlecha.setTextSize(20);
        tvFlecha.setTextColor(Color.parseColor("#90A4AE"));

        fila.addView(tvEmoji);
        fila.addView(tvTexto);
        fila.addView(tvFlecha);
        return fila;
    }

    private void espacio(LinearLayout parent, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        parent.addView(v);
    }
}