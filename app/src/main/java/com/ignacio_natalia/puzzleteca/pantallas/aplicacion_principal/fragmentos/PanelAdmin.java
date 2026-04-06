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
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle instanciaEstadoGuardado) {

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // ── Tarjeta Admin ──
        LinearLayout tarjetaAdministrador = crearTarjeta();
        tarjetaAdministrador.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaAdministrador.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaAdministrador.setPadding(40, 30, 40, 30);

        TextView iconoAdministrador = new TextView(requireContext());
        iconoAdministrador.setText("🛡️");
        iconoAdministrador.setTextSize(36);
        iconoAdministrador.setPadding(0, 0, 24, 0);

        LinearLayout infoAdministrador = new LinearLayout(requireContext());
        infoAdministrador.setOrientation(LinearLayout.VERTICAL);

        TextView textoNombre = new TextView(requireContext());
        textoNombre.setText("Panel Admin");
        textoNombre.setTextSize(18);
        textoNombre.setTypeface(null, Typeface.BOLD);
        textoNombre.setTextColor(Color.parseColor("#37474F"));

        TextView textoId = new TextView(requireContext());
        textoId.setText("Admin to: 00001");
        textoId.setTextSize(13);
        textoId.setTextColor(Color.parseColor("#78909C"));

        infoAdministrador.addView(textoNombre);
        infoAdministrador.addView(textoId);

        tarjetaAdministrador.addView(iconoAdministrador);
        tarjetaAdministrador.addView(infoAdministrador);
        layout.addView(tarjetaAdministrador);
        espacio(layout, 20);

        // ── Opciones ──
        layout.addView(crearOpcion("✏️", "Editar Perfil"));
        espacio(layout, 10);

        // ── Tarjeta Mejor Puzzle ──
        LinearLayout tarjetaMejorPuzzle = crearTarjeta();
        tarjetaMejorPuzzle.setOrientation(LinearLayout.VERTICAL);
        tarjetaMejorPuzzle.setPadding(40, 30, 40, 30);

        TextView textoMejorPuzzle = new TextView(requireContext());
        textoMejorPuzzle.setText("⭐ Mejor Puzzle");
        textoMejorPuzzle.setTextSize(15);
        textoMejorPuzzle.setTypeface(null, Typeface.BOLD);
        textoMejorPuzzle.setTextColor(Color.parseColor("#37474F"));
        tarjetaMejorPuzzle.addView(textoMejorPuzzle);
        espacio(tarjetaMejorPuzzle, 10);

        LinearLayout filaMejorPuzzle = new LinearLayout(requireContext());
        filaMejorPuzzle.setOrientation(LinearLayout.HORIZONTAL);
        filaMejorPuzzle.setGravity(Gravity.CENTER_VERTICAL);

        TextView textoNombrePuzzle = new TextView(requireContext());
        textoNombrePuzzle.setText("🌸 Flor Amarilla");
        textoNombrePuzzle.setTextSize(14);
        textoNombrePuzzle.setTextColor(Color.parseColor("#37474F"));
        textoNombrePuzzle.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout columnaDerecha = new LinearLayout(requireContext());
        columnaDerecha.setOrientation(LinearLayout.VERTICAL);
        columnaDerecha.setGravity(Gravity.END);

        TextView textoValoracion = new TextView(requireContext());
        textoValoracion.setText("4.5");
        textoValoracion.setTextSize(13);
        textoValoracion.setTextColor(Color.parseColor("#78909C"));

        TextView textoTiempo = new TextView(requireContext());
        textoTiempo.setText("02:10");
        textoTiempo.setTextSize(13);
        textoTiempo.setTypeface(null, Typeface.BOLD);
        textoTiempo.setTextColor(Color.WHITE);
        textoTiempo.setPadding(20, 10, 20, 10);

        GradientDrawable formaTiempo = new GradientDrawable();
        formaTiempo.setColor(Color.parseColor("#26A69A"));
        formaTiempo.setCornerRadius(60);
        textoTiempo.setBackground(formaTiempo);

        columnaDerecha.addView(textoValoracion);
        columnaDerecha.addView(textoTiempo);

        filaMejorPuzzle.addView(textoNombrePuzzle);
        filaMejorPuzzle.addView(columnaDerecha);
        tarjetaMejorPuzzle.addView(filaMejorPuzzle);
        layout.addView(tarjetaMejorPuzzle);
        espacio(layout, 10);

        // ── Opciones de gestión ──
        layout.addView(crearOpcion("🧩", "Gestionar Puzzles"));
        layout.addView(crearOpcion("👥", "Gestionar Usuarios"));

        scroll.addView(layout);
        return scroll;

    }

    private LinearLayout crearTarjeta() {

        LinearLayout tarjeta = new LinearLayout(requireContext());
        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        parametrosTarjeta.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(parametrosTarjeta);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(forma);

        return tarjeta;

    }

    private LinearLayout crearOpcion(String emoji, String opcion) {

        LinearLayout fila = new LinearLayout(requireContext());

        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(40, 28, 40, 28);

        LinearLayout.LayoutParams parametrosFila = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        parametrosFila.setMargins(0, 0, 0, 10);
        fila.setLayoutParams(parametrosFila);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        fila.setBackground(forma);

        TextView textoEmoji = new TextView(requireContext());
        textoEmoji.setText(emoji);
        textoEmoji.setTextSize(20);
        textoEmoji.setPadding(0, 0, 20, 0);

        TextView textoOpcion = new TextView(requireContext());
        textoOpcion.setText(opcion);
        textoOpcion.setTextSize(15);
        textoOpcion.setTextColor(Color.parseColor("#37474F"));
        textoOpcion.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView textoFlecha = new TextView(requireContext());
        textoFlecha.setText("›");
        textoFlecha.setTextSize(20);
        textoFlecha.setTextColor(Color.parseColor("#90A4AE"));

        fila.addView(textoEmoji);
        fila.addView(textoOpcion);
        fila.addView(textoFlecha);

        return fila;

    }

    private void espacio(LinearLayout layout, int dp) {
        View vista = new View(requireContext());

        vista.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(vista);
    }

}