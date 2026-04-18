package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class Foro extends Fragment {

    private ForoViewModel viewModel;
    private LinearLayout contenedor;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ForoViewModel.class);

        // ---------- FONDO DEGRADADO ----------
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

        // ---------- SCROLL PRINCIPAL ----------
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        scroll.setBackground(fondo);

        // ---------- CONTENEDOR DE TARJETAS ----------
        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(32, 32, 32, 32);

        scroll.addView(contenedor);

        // ---------- OBSERVADORES ----------
        viewModel.getPuzzles().observe(getViewLifecycleOwner(), this::mostrarPuzzles);
        viewModel.getError().observe(getViewLifecycleOwner(), this::mostrarError);

        // Obtener token de sesión (ajusta según tu implementación)
        String token = obtenerToken();
        viewModel.cargarPuzzles(token);

        return scroll;
    }

    private void mostrarPuzzles(List<Puzzle> puzzles) {
        contenedor.removeAllViews();
        for (Puzzle puzzle : puzzles) {
            contenedor.addView(crearTarjeta(puzzle));
        }
    }

    @SuppressLint("SetTextI18n")
    private View crearTarjeta(Puzzle puzzle) {

        // ---------- TARJETA (card) ----------
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable fondoTarjeta = new GradientDrawable();
        fondoTarjeta.setColor(Color.WHITE);
        fondoTarjeta.setCornerRadius(40);

        tarjeta.setBackground(fondoTarjeta);
        tarjeta.setClipToOutline(true);

        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTarjeta.setMargins(0, 0, 0, 40);
        tarjeta.setLayoutParams(paramsTarjeta);

        // ---------- IMAGEN ----------
        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400
        ));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap bitmap = puzzle.getBitmap();
        if (bitmap != null) {
            imagen.setImageBitmap(bitmap);
        } else {
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
            imagen.setBackgroundColor(Color.parseColor("#C8E6C9"));
        }

        // ---------- CUERPO ----------
        LinearLayout cuerpo = new LinearLayout(requireContext());
        cuerpo.setOrientation(LinearLayout.VERTICAL);
        cuerpo.setPadding(40, 30, 40, 30);

        // Título
        TextView titulo = new TextView(requireContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextColor(Color.parseColor("#2E3A2E"));
        titulo.setTextSize(18);
        titulo.setTypeface(null, android.graphics.Typeface.BOLD);

        // Autor
        TextView autor = new TextView(requireContext());
        autor.setText("Autor: " + puzzle.getAutor());
        autor.setTextColor(Color.parseColor("#607D5E"));
        autor.setTextSize(14);

        LinearLayout.LayoutParams paramsAutor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsAutor.setMargins(0, 8, 0, 24);
        autor.setLayoutParams(paramsAutor);

        // ---------- BOTÓN DE LIKES ----------
        TextView botonLikes = crearBotonLikes(puzzle);

        // Footer con el botón a la derecha
        LinearLayout footer = new LinearLayout(requireContext());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        footer.setGravity(Gravity.END);
        footer.addView(botonLikes);

        cuerpo.addView(titulo);
        cuerpo.addView(autor);
        cuerpo.addView(footer);

        tarjeta.addView(imagen);
        tarjeta.addView(cuerpo);

        return tarjeta;
    }

    private TextView crearBotonLikes(Puzzle puzzle) {
        TextView boton = new TextView(requireContext());

        // Estado mutable del contador
        final int[] likes = {puzzle.getValoracion() != null ? puzzle.getValoracion() : 0};

        actualizarTextoLikes(boton, likes[0]);

        boton.setTextColor(Color.WHITE);
        boton.setTextSize(15);
        boton.setPadding(40, 20, 40, 20);

        GradientDrawable fondoBoton = new GradientDrawable();
        fondoBoton.setColor(Color.parseColor("#F06292"));
        fondoBoton.setCornerRadius(60);

        boton.setBackground(fondoBoton);

        boton.setOnClickListener(v -> {
            likes[0]++;
            actualizarTextoLikes(boton, likes[0]);
        });

        return boton;
    }

    private void actualizarTextoLikes(TextView boton, int cantidad) {
        boton.setText("♥  " + cantidad);
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null) return;
        contenedor.removeAllViews();
        TextView error = new TextView(requireContext());
        error.setText(mensaje);
        error.setTextColor(Color.parseColor("#D32F2F"));
        error.setTextSize(16);
        error.setGravity(Gravity.CENTER);
        error.setPadding(40, 80, 40, 0);
        contenedor.addView(error);
    }

    private String obtenerToken() {
        return GestorSesion.obtenerToken(requireContext());
    }
}