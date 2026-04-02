package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class AppPrincipal extends AppCompatActivity {
    private LinearLayout contenedorPuzzles;
    private PuzzleViewModel puzzleViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#DFF5C9"));

        // ── Fondo degradado igual que el resto de la app ──
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

        // ── Layout raíz vertical ──
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(fondo);

        // ── Cabecera ──
        TextView titulo = new TextView(this);
        titulo.setText("🧩 Puzzles disponibles");
        titulo.setTextSize(22);
        titulo.setTextColor(Color.parseColor("#F06292"));
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(40, 60, 40, 30);
        root.addView(titulo);

        // ── ScrollView con la lista de tarjetas ──
        ScrollView scroll = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scroll.setLayoutParams(scrollParams);

        contenedorPuzzles = new LinearLayout(this);
        contenedorPuzzles.setOrientation(LinearLayout.VERTICAL);
        contenedorPuzzles.setPadding(30, 10, 30, 30);
        scroll.addView(contenedorPuzzles);
        root.addView(scroll);

        setContentView(root);

        // ── ViewModel + carga de datos ──
        puzzleViewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);
        String token = GestorSesion.obtenerToken(this);

        puzzleViewModel.getPuzzles().observe(this, this::mostrarPuzzles);
        puzzleViewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show());

        puzzleViewModel.cargarPuzzles(token);
    }

    private void mostrarPuzzles(List<Puzzle> lista) {
        contenedorPuzzles.removeAllViews();
        for (Puzzle p : lista) {
            if (p.isPublico()) {
                contenedorPuzzles.addView(crearTarjeta(p));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjeta(Puzzle puzzle) {

        // Tarjeta blanca redondeada con borde verde
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(40, 35, 40, 35);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 28);
        tarjeta.setLayoutParams(params);

        GradientDrawable fondoTarjeta = new GradientDrawable();
        fondoTarjeta.setColor(Color.WHITE);
        fondoTarjeta.setCornerRadius(40);
        fondoTarjeta.setStroke(3, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(fondoTarjeta);

        // Título del puzzle
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("🧩 " + puzzle.getAutor());
        tvTitulo.setTextSize(17);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(tvTitulo);

        // Descripción
        TextView tvDesc = new TextView(this);
        tvDesc.setText(puzzle.getDescripcion());
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(Color.parseColor("#78909C"));
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        descParams.setMargins(0, 8, 0, 16);
        tvDesc.setLayoutParams(descParams);
        tarjeta.addView(tvDesc);

        // Fila inferior: dificultad + botón Jugar
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvDificultad = new TextView(this);
        tvDificultad.setText("⭐ " + (puzzle.getDificultad() != null ? puzzle.getDificultad() : "Normal"));
        tvDificultad.setTextSize(13);
        tvDificultad.setTextColor(Color.parseColor("#26A69A"));
        LinearLayout.LayoutParams difParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvDificultad.setLayoutParams(difParams);

        Button btnJugar = new Button(this);
        btnJugar.setText("Jugar");
        btnJugar.setTextColor(Color.WHITE);
        btnJugar.setTextSize(14);
        btnJugar.setAllCaps(false);
        btnJugar.setPadding(50, 15, 50, 15);
        GradientDrawable btnShape = new GradientDrawable();
        btnShape.setColor(Color.parseColor("#F06292"));
        btnShape.setCornerRadius(50);
        btnJugar.setBackground(btnShape);

        fila.addView(tvDificultad);
        fila.addView(btnJugar);
        tarjeta.addView(fila);

        return tarjeta;
    }
}
