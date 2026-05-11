package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.EditarPerfil;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.MisPuzzles;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats.MisChats;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.RegistrarPuzzle;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PanelUsuario extends Fragment {

    private UsuarioRepositorio repositorio;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle instanciaEstadoGuardado) {

        repositorio = new UsuarioRepositorio();
        PuzzleRepositorio puzzleRepositorio = new PuzzleRepositorio();

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // ── Título ──
        TextView textoTitulo = new TextView(requireContext());
        textoTitulo.setText("Panel Usuario");
        textoTitulo.setTextSize(20);
        textoTitulo.setTypeface(null, Typeface.BOLD);
        textoTitulo.setTextColor(Color.parseColor("#37474F"));
        textoTitulo.setGravity(Gravity.CENTER);
        layout.addView(textoTitulo);
        espacio(layout, 30);

        // ── Tarjeta bienvenida ──
        LinearLayout tarjeta = crearTarjeta();
        tarjeta.setPadding(50, 40, 50, 40);
        tarjeta.setOrientation(LinearLayout.VERTICAL);

        TextView textoSaludo = new TextView(requireContext());
        textoSaludo.setText("👤 Hola, " + GestorSesion.obtenerRol(requireContext()));
        textoSaludo.setTextSize(17);
        textoSaludo.setTypeface(null, Typeface.BOLD);
        textoSaludo.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(textoSaludo);
        layout.addView(tarjeta);
        espacio(layout, 20);

        Button botonMisChats = crearBotonPrimario("💬 Mis Chats", "#26A69A");
        botonMisChats.setOnClickListener(vista -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, new MisChats())
                    .addToBackStack(null).commit();
        });
        layout.addView(botonMisChats);
        espacio(layout, 14);

        // ── Fila: Mis Puzzles + Editar Perfil ──
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout botonMisPuzzles = crearBotonAccion("🧩", "Mis Puzzles");
        botonMisPuzzles.setOnClickListener(vista -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, new MisPuzzles())
                    .addToBackStack(null).commit();
        });

        LinearLayout botonEditarPerfil = crearBotonAccion("✏️", "Editar Perfil");
        botonEditarPerfil.setOnClickListener(vista -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, new EditarPerfil())
                    .addToBackStack(null).commit();
        });

        fila.addView(botonMisPuzzles);
        fila.addView(botonEditarPerfil);
        layout.addView(fila);
        espacio(layout, 16);

        // ── Tarjeta Ranking ──
        LinearLayout tarjetaRanking = crearTarjeta();
        tarjetaRanking.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaRanking.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaRanking.setPadding(50, 30, 50, 30);

        LinearLayout informacionRanking = new LinearLayout(requireContext());
        informacionRanking.setOrientation(LinearLayout.VERTICAL);
        informacionRanking.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView textoRanking = new TextView(requireContext());
        textoRanking.setText("🏆 Ranking");
        textoRanking.setTextSize(15);
        textoRanking.setTypeface(null, Typeface.BOLD);
        textoRanking.setTextColor(Color.parseColor("#37474F"));

        TextView textoMejorTiempo = new TextView(requireContext());
        textoMejorTiempo.setText("Mejor tiempo:");
        textoMejorTiempo.setTextSize(12);
        textoMejorTiempo.setTextColor(Color.parseColor("#78909C"));

        informacionRanking.addView(textoRanking);
        informacionRanking.addView(textoMejorTiempo);

        TextView textoTiempo = new TextView(requireContext());
        textoTiempo.setText("--:--");
        textoTiempo.setTextSize(14);
        textoTiempo.setTypeface(null, Typeface.BOLD);
        textoTiempo.setTextColor(Color.WHITE);
        textoTiempo.setPadding(30, 14, 30, 14);

        GradientDrawable formaTiempo = new GradientDrawable();
        formaTiempo.setColor(Color.parseColor("#26A69A"));
        formaTiempo.setCornerRadius(60);
        textoTiempo.setBackground(formaTiempo);

        tarjetaRanking.addView(informacionRanking);
        tarjetaRanking.addView(textoTiempo);
        layout.addView(tarjetaRanking);
        espacio(layout, 24);

        // Cargar el mejor tiempo real desde el backend
        String token = GestorSesion.obtenerToken(requireContext());
        puzzleRepositorio.obtenerMejorTiempo(token, new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<Integer> call,
                                   @NonNull retrofit2.Response<Integer> response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        int segundos = response.body();
                        int mm = segundos / 60;
                        int ss = segundos % 60;
                        textoTiempo.setText(String.format("%02d:%02d", mm, ss));
                    } else {
                        textoTiempo.setText("N/A");
                    }
                });
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<Integer> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> textoTiempo.setText("--:--"));
            }
        });

        // ── Botón Crear Nuevo Puzzle ──
        Button botonCrearPuzzle = crearBotonPrimario("➕ Crear Nuevo Puzzle", "#F06292");

        botonCrearPuzzle.setOnClickListener(vista -> {
            Fragment fragment = new RegistrarPuzzle();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        layout.addView(botonCrearPuzzle);
        espacio(layout, 14);

        Button botonCerrarSesion = crearBotonSecundario("🗑 Cerrar Sesión   ›");

        botonCerrarSesion.setOnClickListener(vista -> {

            UtilidadesSesion.mostrarDialogoCerrarSesion(requireContext(), () -> {
                UtilidadesSesion.cerrarSesion(requireContext());
            });
        });

        layout.addView(botonCerrarSesion);
        espacio(layout, 14);

        scroll.addView(layout);
        return scroll;
    }
    private LinearLayout crearTarjeta() {
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(forma);

        return tarjeta;
    }

    private LinearLayout crearBotonAccion(String emoji, String opcion) {

        LinearLayout boton = new LinearLayout(requireContext());
        boton.setOrientation(LinearLayout.VERTICAL);
        boton.setGravity(Gravity.CENTER);
        boton.setPadding(30, 40, 30, 40);

        LinearLayout.LayoutParams parametrosBoton = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);

        parametrosBoton.setMargins(0, 0, 10, 0);
        boton.setLayoutParams(parametrosBoton);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        boton.setBackground(forma);

        TextView textoEmoji = new TextView(requireContext());
        textoEmoji.setText(emoji);
        textoEmoji.setTextSize(26);
        textoEmoji.setGravity(Gravity.CENTER);

        TextView textoOpcion = new TextView(requireContext());
        textoOpcion.setText(opcion);
        textoOpcion.setTextSize(13);
        textoOpcion.setGravity(Gravity.CENTER);
        textoOpcion.setTextColor(Color.parseColor("#37474F"));

        boton.addView(textoEmoji);
        boton.addView(textoOpcion);

        return boton;
    }

    private Button crearBotonPrimario(String texto, String color) {

        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(16);
        boton.setAllCaps(false);
        boton.setPadding(30, 30, 30, 30);

        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor(color));
        forma.setCornerRadius(60);
        boton.setBackground(forma);

        return boton;
    }

    private Button crearBotonSecundario(String texto) {

        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.RED);
        boton.setTextSize(14);
        boton.setAllCaps(false);
        boton.setPadding(30, 20, 30, 20);

        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor("#FFF9C4"));
        forma.setCornerRadius(60);
        forma.setStroke(2, Color.parseColor("#F0CC50"));
        boton.setBackground(forma);

        return boton;
    }

    private void espacio(LinearLayout layout, int dp) {
        View vista = new View(requireContext());
        vista.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(vista);
    }
}