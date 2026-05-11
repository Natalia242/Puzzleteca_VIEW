package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.*;
import android.widget.*;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.ActualizarPuzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.RegistrarPuzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class MisPuzzles extends Fragment {

    private LinearLayout contenedor;
    private ScrollView scroll;
    private FrameLayout root;

    public MisPuzzles() {}

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        root = new FrameLayout(requireContext());

        scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(30, 20, 30, 30);

        scroll.addView(contenedor);
        root.addView(scroll);

        cargarDatos();

        return root;
    }

    private void cargarDatos() {

        String token = GestorSesion.obtenerToken(requireContext());
        int idUsuario = GestorSesion.obtenerId_usuario(requireContext());

        PuzzleRepositorio repositorio = new PuzzleRepositorio();
        repositorio.misPuzzles(token, idUsuario, new retrofit2.Callback<java.util.List<com.ignacio_natalia.puzzleteca.modelos.Puzzle>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<com.ignacio_natalia.puzzleteca.modelos.Puzzle>> call,
                                   retrofit2.Response<java.util.List<com.ignacio_natalia.puzzleteca.modelos.Puzzle>> response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        mostrarMisPuzzles(response.body(), idUsuario);
                    } else if (response.code() == 404) {
                        mostrarMisPuzzles(new java.util.ArrayList<>(), idUsuario);
                    } else {
                        Toast.makeText(getContext(), "Error " + response.code() + " al cargar tus puzzles", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(retrofit2.Call<java.util.List<com.ignacio_natalia.puzzleteca.modelos.Puzzle>> call, Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(getContext(), "Fallo de red: " + t.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void mostrarMisPuzzles(List<Puzzle> lista, int idUsuario) {

        contenedor.removeAllViews();

        if (root.getChildCount() > 1) {
            root.removeViews(1, root.getChildCount() - 1);
        }

        if (lista == null || lista.isEmpty()) {
            mostrarVistaVacia();
        } else {
            List<Puzzle> visibles = new java.util.ArrayList<>();
            for (Puzzle p : lista) {
                if (p.getEstado() != Puzzle.Estados.Bloqueado) {
                    visibles.add(p);
                }
            }
            if (visibles.isEmpty()) {
                mostrarVistaVacia();
            } else {
                for (Puzzle p : visibles) {
                    contenedor.addView(crearTarjeta(p));
                }
                mostrarBotonFlotante();
            }
        }
    }

    // ─────────────────────────────────────────────
    // 🟢 VISTA VACÍA
    // ─────────────────────────────────────────────

    private void mostrarVistaVacia() {

        LinearLayout layoutVacio = new LinearLayout(getContext());
        layoutVacio.setOrientation(LinearLayout.VERTICAL);
        layoutVacio.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        FrameLayout.LayoutParams paramsRoot = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);

        layoutVacio.setLayoutParams(paramsRoot);
        layoutVacio.setPadding(40, dpToPx(120), 40, 40);

        TextView icono = new TextView(getContext());
        icono.setText("🧩");
        icono.setTextSize(48);
        icono.setGravity(Gravity.CENTER);

        TextView titulo = new TextView(getContext());
        titulo.setText("No tienes puzzles");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#6D4C41"));
        titulo.setGravity(Gravity.CENTER);

        TextView subtitulo = new TextView(getContext());
        subtitulo.setText("Crea tu primer puzzle");
        subtitulo.setTextSize(14);
        subtitulo.setTextColor(Color.parseColor("#8D6E63"));
        subtitulo.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams paramsSub = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsSub.setMargins(0, 10, 0, 30);
        subtitulo.setLayoutParams(paramsSub);

        Button botonRegistro = new Button(getContext());
        botonRegistro.setText("Crear Puzzle");
        botonRegistro.setTextColor(Color.WHITE);
        botonRegistro.setAllCaps(false);
        botonRegistro.setTextSize(15);

        GradientDrawable fondoBoton = new GradientDrawable();
        fondoBoton.setColor(Color.parseColor("#F06292"));
        fondoBoton.setCornerRadius(50);

        botonRegistro.setBackground(fondoBoton);
        botonRegistro.setPadding(60, 25, 60, 25);
        botonRegistro.setElevation(6f);

        botonRegistro.setOnClickListener(v -> {
            Fragment fragment = new RegistrarPuzzle();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        layoutVacio.addView(icono);
        layoutVacio.addView(titulo);
        layoutVacio.addView(subtitulo);
        layoutVacio.addView(botonRegistro);

        root.addView(layoutVacio);
    }

    private void mostrarBotonFlotante() {

        ImageButton botonRegistro = new ImageButton(getContext());

        GradientDrawable fondo = new GradientDrawable();

        // Fondos para el boton
        // fondo.setColor(Color.parseColor("#E91E63"));
        fondo.setColor(Color.parseColor("#D16A8A"));
        // fondo.setColor(Color.parseColor("#9C27B0")); //Enseñar por cortesia

        // fondo.setColor(Color.parseColor("#4CAF50"));
        fondo.setShape(GradientDrawable.OVAL);

        botonRegistro.setBackground(fondo);
        botonRegistro.setImageResource(android.R.drawable.ic_input_add);
        botonRegistro.setColorFilter(Color.WHITE);
        botonRegistro.setScaleType(ImageView.ScaleType.CENTER);

        int size = dpToPx(60);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(size, size);
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.setMargins(0, 0, dpToPx(20), dpToPx(20));

        botonRegistro.setLayoutParams(params);
        botonRegistro.setElevation(12f);

        botonRegistro.setOnClickListener(v -> {
            Fragment fragment = new RegistrarPuzzle();

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        root.addView(botonRegistro);
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjeta(Puzzle puzzle) {

        LinearLayout tarjeta = new LinearLayout(getContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(40, 35, 40, 35);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 28);
        tarjeta.setLayoutParams(params);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(3, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(fondo);

        // Fila: título + icono editar
        LinearLayout filaTitulo = new LinearLayout(getContext());
        filaTitulo.setOrientation(LinearLayout.HORIZONTAL);
        filaTitulo.setGravity(Gravity.CENTER_VERTICAL);
        filaTitulo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        // Título
        TextView titulo = new TextView(getContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextSize(17);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#37474F"));
        titulo.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Icono ✏️ editar
        TextView iconoEditar = new TextView(getContext());
        iconoEditar.setText("✏️");
        iconoEditar.setTextSize(16);
        iconoEditar.setPadding(8, 0, 0, 0);

        filaTitulo.addView(titulo);
        filaTitulo.addView(iconoEditar);
        tarjeta.addView(filaTitulo);

        // Descripción
        TextView descripcion = new TextView(getContext());
        descripcion.setText(puzzle.getDescripcion());
        descripcion.setTextSize(14);
        descripcion.setTextColor(Color.parseColor("#78909C"));

        LinearLayout.LayoutParams paramsDesc = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsDesc.setMargins(0, 8, 0, 16);
        descripcion.setLayoutParams(paramsDesc);

        tarjeta.addView(descripcion);

        // Fila info
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);

        TextView dificultad = new TextView(getContext());
        dificultad.setText("⭐ " + (puzzle.getDificultad() != null ? puzzle.getDificultad() : "Normal"));
        dificultad.setTextSize(13);
        dificultad.setTextColor(Color.parseColor("#26A69A"));
        dificultad.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        fila.addView(dificultad);

        TextView estado = new TextView(getContext());
        estado.setText(puzzle.getEstado().toString());
        estado.setTextSize(12);
        estado.setTextColor(Color.parseColor("#EF5350"));

        fila.addView(estado);
        tarjeta.addView(fila);

        // Hint "Toca para editar"
        TextView hint = new TextView(getContext());
        hint.setText("Toca para editar");
        hint.setTextSize(11);
        hint.setTextColor(Color.parseColor("#BDBDBD"));
        LinearLayout.LayoutParams hintParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hintParams.setMargins(0, 8, 0, 0);
        hint.setLayoutParams(hintParams);
        tarjeta.addView(hint);

        // Click → navegar a ActualizarPuzzle
        tarjeta.setClickable(true);
        tarjeta.setFocusable(true);
        tarjeta.setOnClickListener(v -> {
            Fragment fragment = ActualizarPuzzle.newInstance(puzzle);
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return tarjeta;
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}