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
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class PanelUsuario extends Fragment {

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

        // ── Título ──
        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText("User Dashboard");
        tvTitulo.setTextSize(20);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.parseColor("#37474F"));
        tvTitulo.setGravity(Gravity.CENTER);
        layout.addView(tvTitulo);
        espacio(layout, 30);

        // ── Tarjeta bienvenida ──
        LinearLayout tarjeta = crearTarjeta();
        tarjeta.setPadding(50, 40, 50, 40);
        tarjeta.setOrientation(LinearLayout.VERTICAL);

        TextView tvHola = new TextView(requireContext());
        tvHola.setText("👤 Hola, " + GestorSesion.obtenerRol(requireContext()));
        tvHola.setTextSize(17);
        tvHola.setTypeface(null, Typeface.BOLD);
        tvHola.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(tvHola);
        layout.addView(tarjeta);
        espacio(layout, 20);

        // ── Fila: Mis Puzzles + Editar Perfil ──
        LinearLayout fila1 = new LinearLayout(requireContext());
        fila1.setOrientation(LinearLayout.HORIZONTAL);
        fila1.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        fila1.addView(crearBotonAccion("🧩", "Mis Puzzles", "#F48FB1"));
        fila1.addView(crearBotonAccion("✏️", "Editar Perfil", "#A5D6A7"));
        layout.addView(fila1);
        espacio(layout, 16);

        // ── Tarjeta Ranking ──
        LinearLayout tarjetaRanking = crearTarjeta();
        tarjetaRanking.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaRanking.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaRanking.setPadding(50, 30, 50, 30);

        LinearLayout infoRanking = new LinearLayout(requireContext());
        infoRanking.setOrientation(LinearLayout.VERTICAL);
        infoRanking.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvRankLabel = new TextView(requireContext());
        tvRankLabel.setText("🏆 Ranking");
        tvRankLabel.setTextSize(15);
        tvRankLabel.setTypeface(null, Typeface.BOLD);
        tvRankLabel.setTextColor(Color.parseColor("#37474F"));

        TextView tvMejorTiempo = new TextView(requireContext());
        tvMejorTiempo.setText("Mejor tiempo:");
        tvMejorTiempo.setTextSize(12);
        tvMejorTiempo.setTextColor(Color.parseColor("#78909C"));

        infoRanking.addView(tvRankLabel);
        infoRanking.addView(tvMejorTiempo);

        TextView tvBadge = new TextView(requireContext());
        tvBadge.setText("02:15");
        tvBadge.setTextSize(14);
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setTextColor(Color.WHITE);
        tvBadge.setPadding(30, 14, 30, 14);
        GradientDrawable badgeShape = new GradientDrawable();
        badgeShape.setColor(Color.parseColor("#26A69A"));
        badgeShape.setCornerRadius(60);
        tvBadge.setBackground(badgeShape);

        tarjetaRanking.addView(infoRanking);
        tarjetaRanking.addView(tvBadge);
        layout.addView(tarjetaRanking);
        espacio(layout, 24);

        // ── Botón Crear Nuevo Puzzle ──
        layout.addView(crearBotonPrimario("➕  Crear Nuevo Puzzle", "#F06292"));
        espacio(layout, 14);

        // ── Botón Eliminar Cuenta ──
        layout.addView(crearBotonSecundario("Eliminar Cuenta  ›"));

        scroll.addView(layout);
        return scroll;
    }

    private LinearLayout crearTarjeta() {
        LinearLayout t = new LinearLayout(requireContext());
        t.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(2, Color.parseColor("#A5D6A7"));
        t.setBackground(fondo);
        return t;
    }

    private LinearLayout crearBotonAccion(String emoji, String texto, String colorBorde) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(30, 40, 30, 40);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        p.setMargins(0, 0, 10, 0);
        card.setLayoutParams(p);
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(2, Color.parseColor(colorBorde));
        card.setBackground(fondo);

        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(26);
        tvEmoji.setGravity(Gravity.CENTER);

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText(texto);
        tvLabel.setTextSize(13);
        tvLabel.setGravity(Gravity.CENTER);
        tvLabel.setTextColor(Color.parseColor("#37474F"));

        card.addView(tvEmoji);
        card.addView(tvLabel);
        return card;
    }

    private Button crearBotonPrimario(String texto, String color) {
        Button btn = new Button(requireContext());
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(16);
        btn.setAllCaps(false);
        btn.setPadding(30, 30, 30, 30);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor(color));
        shape.setCornerRadius(60);
        btn.setBackground(shape);
        return btn;
    }
    private Button crearBotonSecundario(String texto) {
        Button btn = new Button(requireContext());
        btn.setText(texto);
        btn.setTextColor(Color.parseColor("#78909C"));
        btn.setTextSize(14);
        btn.setAllCaps(false);
        btn.setPadding(30, 20, 30, 20);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.parseColor("#FFF9C4"));
        shape.setCornerRadius(60);
        shape.setStroke(2, Color.parseColor("#F0CC50"));
        btn.setBackground(shape);
        return btn;
    }
    private void espacio(LinearLayout parent, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        parent.addView(v);
    }
}