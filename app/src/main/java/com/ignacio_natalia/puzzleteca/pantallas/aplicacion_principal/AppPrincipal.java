package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.Foro;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.PanelAdmin;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.PanelUsuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.Ranking;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class AppPrincipal extends AppCompatActivity {

    private static final int FRAGMENTO_ID = 0xF4A001;

    private LinearLayout contenedorPuzzles;
    private ScrollView scrollPuzzles;
    private FrameLayout contenedorFragmento;
    private PuzzleViewModel puzzleViewModel;
    private TextView btnInicio, btnPuzzles, btnRanking, btnPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#DFF5C9"));

        // ── Bloqueo ──
        if ("Bloqueado".equals(GestorSesion.obtenerRol(this))) {
            mostrarPantallaBloqueo();
            return;
        }

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

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

        // ── ScrollView con la lista de tarjetas (Inicio) ──
        scrollPuzzles = new ScrollView(this);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollPuzzles.setLayoutParams(scrollParams);

        contenedorPuzzles = new LinearLayout(this);
        contenedorPuzzles.setOrientation(LinearLayout.VERTICAL);
        contenedorPuzzles.setPadding(30, 10, 30, 30);
        scrollPuzzles.addView(contenedorPuzzles);
        root.addView(scrollPuzzles);

        // ── Contenedor de fragmentos (oculto al inicio) ──
        contenedorFragmento = new FrameLayout(this);
        contenedorFragmento.setId(FRAGMENTO_ID);
        contenedorFragmento.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        contenedorFragmento.setVisibility(FrameLayout.GONE);
        root.addView(contenedorFragmento);

        // ── Nav inferior ──
        root.addView(construirNavBar());

        setContentView(root);

        // ── ViewModel + carga ──
        puzzleViewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);
        String token = GestorSesion.obtenerToken(this);

        puzzleViewModel.getPuzzles().observe(this, this::mostrarPuzzles);
        puzzleViewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show());

        puzzleViewModel.cargarPuzzles(token);

        marcarTab(btnInicio);
    }

    // ── Mostrar contenido de Inicio vs Fragmento ──
    private void mostrarInicio() {
        scrollPuzzles.setVisibility(ScrollView.VISIBLE);
        contenedorFragmento.setVisibility(FrameLayout.GONE);
    }

    private void mostrarFragmento(Fragment fragmento) {
        scrollPuzzles.setVisibility(ScrollView.GONE);
        contenedorFragmento.setVisibility(FrameLayout.VISIBLE);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(FRAGMENTO_ID, fragmento);
        ft.commit();
    }

    // ── Nav bar ──
    private LinearLayout construirNavBar() {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setBackgroundColor(Color.WHITE);
        nav.setElevation(16f);
        nav.setPadding(0, 14, 0, 14);

        btnInicio  = crearTab("🏠", "Inicio");
        btnPuzzles = crearTab("🧩", "Puzzles");
        btnRanking = crearTab("🏆", "Ranking");
        btnPerfil  = crearTab("👤", "Perfil");

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnInicio.setLayoutParams(p);
        btnPuzzles.setLayoutParams(p);
        btnRanking.setLayoutParams(p);
        btnPerfil.setLayoutParams(p);

        btnInicio.setOnClickListener(v -> {
            mostrarInicio();
            marcarTab(btnInicio);
        });
        btnPuzzles.setOnClickListener(v -> {
            mostrarFragmento(new Foro());
            marcarTab(btnPuzzles);
        });
        btnRanking.setOnClickListener(v -> {
            mostrarFragmento(new Ranking());
            marcarTab(btnRanking);
        });
        btnPerfil.setOnClickListener(v -> {
            String rol = GestorSesion.obtenerRol(this);
            mostrarFragmento("Admin".equals(rol) ? new PanelAdmin() : new PanelUsuario());
            marcarTab(btnPerfil);
        });

        nav.addView(btnInicio);
        nav.addView(btnPuzzles);
        nav.addView(btnRanking);
        nav.addView(btnPerfil);
        return nav;
    }

    private TextView crearTab(String emoji, String etiqueta) {
        TextView tv = new TextView(this);
        tv.setText(emoji + "\n" + etiqueta);
        tv.setTextSize(11);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(Color.parseColor("#90A4AE"));
        tv.setPadding(8, 8, 8, 8);
        return tv;
    }

    private void marcarTab(TextView seleccionado) {
        for (TextView t : new TextView[]{btnInicio, btnPuzzles, btnRanking, btnPerfil}) {
            t.setTextColor(Color.parseColor("#90A4AE"));
            t.setTypeface(null, Typeface.NORMAL);
        }
        seleccionado.setTextColor(Color.parseColor("#F06292"));
        seleccionado.setTypeface(null, Typeface.BOLD);
    }

    // ── Pantalla de bloqueo ──
    private void mostrarPantallaBloqueo() {
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );
        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);

        LinearLayout centro = new LinearLayout(this);
        centro.setOrientation(LinearLayout.VERTICAL);
        centro.setGravity(Gravity.CENTER);
        centro.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        TextView tvIcono = new TextView(this);
        tvIcono.setText("🚫");
        tvIcono.setTextSize(64);
        tvIcono.setGravity(Gravity.CENTER);

        TextView tvMensaje = new TextView(this);
        tvMensaje.setText("Usuario bloqueado");
        tvMensaje.setTextSize(22);
        tvMensaje.setTypeface(null, Typeface.BOLD);
        tvMensaje.setTextColor(Color.parseColor("#C62828"));
        tvMensaje.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams mp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mp.setMargins(0, 24, 0, 0);
        tvMensaje.setLayoutParams(mp);

        centro.addView(tvIcono);
        centro.addView(tvMensaje);
        layout.addView(centro);
        setContentView(layout);
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

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("🧩 " + puzzle.getAutor());
        tvTitulo.setTextSize(17);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(tvTitulo);

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

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);

        TextView tvDificultad = new TextView(this);
        tvDificultad.setText("⭐ " + (puzzle.getDificultad() != null ? puzzle.getDificultad() : "Normal"));
        tvDificultad.setTextSize(13);
        tvDificultad.setTextColor(Color.parseColor("#26A69A"));
        tvDificultad.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        fila.addView(tvDificultad);
        tarjeta.addView(fila);

        return tarjeta;
    }
}