package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
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

        // ---------- SCROLL ----------
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        scroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity instanceof AppPrincipal) {

                boolean haciaAbajo = scrollY > oldScrollY;

                if (haciaAbajo) {
                    ((AppPrincipal) activity).ocultarBarra();
                } else {
                    ((AppPrincipal) activity).mostrarBarra();
                }
            }
        });

        // ---------- CONTENEDOR ----------
        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(16), dp(16), dp(16), dp(16));

        scroll.addView(contenedor);

        // ---------- OBSERVADORES ----------
        viewModel.getPuzzles().observe(getViewLifecycleOwner(), this::mostrarPuzzles);
        viewModel.getError().observe(getViewLifecycleOwner(), this::mostrarError);

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

        // ---------- TARJETA ----------
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable fondoTarjeta = new GradientDrawable();
        fondoTarjeta.setColor(Color.WHITE);
        fondoTarjeta.setCornerRadius(dp(20));

        tarjeta.setBackground(fondoTarjeta);

        LinearLayout.LayoutParams paramsTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsTarjeta.setMargins(0, 0, 0, dp(16));
        tarjeta.setLayoutParams(paramsTarjeta);

        // ---------- IMAGEN ----------
        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(200)
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
        cuerpo.setPadding(dp(16), dp(12), dp(16), dp(12));

        // Título
        TextView titulo = new TextView(requireContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextColor(Color.parseColor("#2E3A2E"));
        titulo.setTextSize(18);
        titulo.setTypeface(null, Typeface.BOLD);

        // Autor
        TextView autor = new TextView(requireContext());
        autor.setText("Autor: " + puzzle.getAutor());
        autor.setTextColor(Color.parseColor("#607D5E"));
        autor.setTextSize(14);

        LinearLayout.LayoutParams paramsAutor = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        paramsAutor.setMargins(0, dp(4), 0, dp(12));
        autor.setLayoutParams(paramsAutor);

        // ---------- BOTÓN LIKE ----------
        LinearLayout botonLikes = crearBotonLikes(puzzle);

        // Footer
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

    private LinearLayout crearBotonLikes(Puzzle puzzle) {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setPadding(dp(12), dp(8), dp(12), dp(8));

        // ICONO
        ImageView icono = new ImageView(requireContext());
        LinearLayout.LayoutParams iconParams =
                new LinearLayout.LayoutParams(dp(24), dp(24));
        icono.setLayoutParams(iconParams);

        // TEXTO
        TextView texto = new TextView(requireContext());
        texto.setTextSize(15);
        texto.setTextColor(Color.parseColor("#2E3A2E"));

        LinearLayout.LayoutParams textParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        textParams.setMargins(dp(8), 0, 0, 0);
        texto.setLayoutParams(textParams);

        int likesIniciales = puzzle.getValoracion() != null ? puzzle.getValoracion() : 0;

        final boolean[] liked = {false};
        final int[] likes = {likesIniciales};

        actualizarUI(icono, texto, liked[0], likes[0]);

        layout.setOnClickListener(vista -> {

            liked[0] = !liked[0];

            if (liked[0]) {
                likes[0]++;

            } else {
                likes[0] --;

            }
            actualizarUI(icono, texto, liked[0], likes[0]);

        });

        layout.addView(icono);
        layout.addView(texto);

        return layout;
    }

    private void actualizarUI(ImageView icono, TextView texto, boolean liked, int cantidad) {
        if (liked) {
            icono.setImageResource(R.drawable.ic_like_filled);
        } else {
            icono.setImageResource(R.drawable.ic_like_outline);
        }
        texto.setText(String.valueOf(cantidad));
    }

    private void mostrarError(String mensaje) {
        if (mensaje == null) return;

        contenedor.removeAllViews();

        TextView error = new TextView(requireContext());
        error.setText(mensaje);
        error.setTextColor(Color.parseColor("#D32F2F"));
        error.setTextSize(16);
        error.setGravity(Gravity.CENTER);
        error.setPadding(dp(16), dp(40), dp(16), 0);

        contenedor.addView(error);
    }

    private int dp(int value) {
        return (int) (value * requireContext().getResources().getDisplayMetrics().density);
    }

    private String obtenerToken() {
        return GestorSesion.obtenerToken(requireContext());
    }
}