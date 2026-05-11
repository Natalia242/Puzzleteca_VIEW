package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.MisPuzzles;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.PuzzleDialogFragment;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro.Foro;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.PanelAdmin;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.PanelUsuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.Ranking;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.PuzzleViewModel;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class AppPrincipal extends AppCompatActivity {

    public static final int FRAGMENTO_ID = 0xF4A001;

    private LinearLayout contenedorPuzzles;
    private ScrollView scrollPuzzles;
    private LinearLayout barraNavegacionRoot;
    private FrameLayout contenedorFragmento;
    private ImageView tituloPantalla;

    // Tabs (ahora LinearLayout en lugar de TextView)
    private LinearLayout botonInicio, botonPuzzles, botonRanking, botonForo, botonPerfil;
    // Indicador deslizante
    private View indicadorActivo;
    PuzzleViewModel puzzleViewModel;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle instanciaEstadoGuardado) {
        super.onCreate(instanciaEstadoGuardado);

        getWindow().setStatusBarColor(Color.parseColor("#DFF5C9"));

        if ("Bloqueado".equals(GestorSesion.obtenerRol(this))) {
            mostrarPantallaBloqueo();
            return;
        }

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#F1B2CA"), Color.parseColor("#FFF6F9")}
        );

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackground(fondo);

        // ── Cabecera ──
        tituloPantalla = new ImageView(this);
        tituloPantalla.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        tituloPantalla.setAdjustViewBounds(true);
        tituloPantalla.setScaleType(ImageView.ScaleType.FIT_CENTER);
        tituloPantalla.setPadding(40, 60, 40, 30);
        tituloPantalla.setElevation(10f);
        tituloPantalla.setPadding(40, 40, 40, 20);

        root.addView(tituloPantalla);

        // ── ScrollView con lista de tarjetas (Inicio) ──
        scrollPuzzles = new ScrollView(this);
        scrollPuzzles.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        scrollPuzzles.setOnScrollChangeListener(new View.OnScrollChangeListener() {

            private int ultimoScrollY = 0;
            private boolean oculta = false;

            @Override
            public void onScrollChange(View v, int scrollX, int scrollY,
                                       int oldScrollX, int oldScrollY) {

                if (scrollY > oldScrollY + 10 && !oculta) {
                    // 👉 scroll hacia abajo → ocultar
                    ocultarBarra();
                    oculta = true;

                } else if (scrollY < oldScrollY - 10 && oculta) {
                    // 👈 scroll hacia arriba → mostrar
                    mostrarBarra();
                    oculta = false;
                }
            }
        });

        contenedorPuzzles = new LinearLayout(this);
        contenedorPuzzles.setOrientation(LinearLayout.VERTICAL);
        contenedorPuzzles.setPadding(30, 10, 30, 30);
        scrollPuzzles.addView(contenedorPuzzles);
        root.addView(scrollPuzzles);

        // ── Contenedor de fragmentos ──
        contenedorFragmento = new FrameLayout(this);
        contenedorFragmento.setId(FRAGMENTO_ID);
        contenedorFragmento.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        contenedorFragmento.setVisibility(FrameLayout.GONE);
        root.addView(contenedorFragmento);

        // ── Nav inferior ──
        root.addView(construirBarraNavegacion());

        setContentView(root);
        tituloPantalla.setImageResource(R.drawable.titulo_inicio_recortado);

        // ── ViewModel + carga ──
        puzzleViewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);
        String token = GestorSesion.obtenerToken(this);
        puzzleViewModel.getPuzzles().observe(this, this::mostrarPuzzles);
        puzzleViewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
        puzzleViewModel.cargarPuzzles(token);

        // Seleccionar Inicio por defecto tras layout
        root.post(() -> seleccionarTab(botonInicio, null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (puzzleViewModel != null) {
            String token = GestorSesion.obtenerToken(this);
            puzzleViewModel.cargarPuzzles(token);
        }
    }

    // ── Navegación ────────────────────────────────────────────────────────────

    private void mostrarInicio() {
        scrollPuzzles.setVisibility(ScrollView.VISIBLE);
        contenedorFragmento.setVisibility(FrameLayout.GONE);
    }

    private void actualizarTituloPantalla(int drawableRes) {
        tituloPantalla.setImageResource(drawableRes);
    }

    public void ocultarTitulo() {
        tituloPantalla.setVisibility(View.GONE);
    }

    public void mostrarTitulo() {
        tituloPantalla.setVisibility(View.VISIBLE);
    }

    private void mostrarFragmento(Fragment fragmento) {

        scrollPuzzles.setVisibility(ScrollView.GONE);
        contenedorFragmento.setVisibility(FrameLayout.VISIBLE);

        FragmentTransaction transaccion = getSupportFragmentManager().beginTransaction();
        transaccion.replace(FRAGMENTO_ID, fragmento);
        transaccion.commit();

    }

    // ── Nav bar ───────────────────────────────────────────────────────────────
    private LinearLayout construirBarraNavegacion() {

        // Contenedor raíz vertical (indicador + barra)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setElevation(16f);

        // Indicador deslizante superior
        indicadorActivo = new View(this);
        indicadorActivo.setBackgroundColor(Color.parseColor("#F06292"));
        LinearLayout.LayoutParams parametrosIndicador = new LinearLayout.LayoutParams(dpToPx(32), dpToPx(3));
        parametrosIndicador.gravity = Gravity.START;
        indicadorActivo.setLayoutParams(parametrosIndicador);
        root.addView(indicadorActivo);

        // Barra horizontal de tabs
        LinearLayout barraNavegacion = new LinearLayout(this);
        barraNavegacion.setOrientation(LinearLayout.HORIZONTAL);
        barraNavegacion.setGravity(Gravity.CENTER_VERTICAL);
        barraNavegacion.setPadding(0, dpToPx(6), 0, dpToPx(6));

        botonInicio = crearTab(ContextCompat.getDrawable(this, R.drawable.home),   "Inicio");
        botonPuzzles = crearTab(ContextCompat.getDrawable(this, R.drawable.puzzle),  "Puzzles");
        botonRanking = crearTab(ContextCompat.getDrawable(this, R.drawable.trophy),  "Ranking");
        botonForo = crearTab(ContextCompat.getDrawable(this, R.drawable.forum),   "Foro");
        botonPerfil = crearTab(ContextCompat.getDrawable(this, R.drawable.person),  "Perfil");

        //LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        botonInicio.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        botonPuzzles.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        botonForo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        botonRanking.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        botonPerfil.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        botonInicio.setOnClickListener(vista ->
                seleccionarTab(botonInicio, () -> {
                    actualizarTituloPantalla(R.drawable.titulo_inicio_recortado);
                    mostrarInicio();
                })
        );

        botonPuzzles.setOnClickListener(vista ->
                seleccionarTab(botonPuzzles, () -> {
                    actualizarTituloPantalla(R.drawable.titulo_mis_puzzles_recortado);
                    mostrarFragmento(new MisPuzzles());
                })
        );

        botonRanking.setOnClickListener(vista ->
                seleccionarTab(botonRanking, () -> {
                    actualizarTituloPantalla(R.drawable.titulo_ranking_diario_recortado);
                    mostrarFragmento(new Ranking());
                })
        );

        botonForo.setOnClickListener(vista ->
                seleccionarTab(botonForo, () -> {
                    actualizarTituloPantalla(R.drawable.titulo_foro_recortado);
                    mostrarFragmento(new Foro());
                })
        );

        botonPerfil.setOnClickListener(vista -> {
            String rol = GestorSesion.obtenerRol(this);
            seleccionarTab(botonPerfil, () -> {
                actualizarTituloPantalla(0);
                mostrarFragmento(
                        "Admin".equals(rol) ? new PanelAdmin() : new PanelUsuario()
                );
            });
        });

        barraNavegacion.addView(botonInicio);
        barraNavegacion.addView(botonPuzzles);
        barraNavegacion.addView(botonRanking);
        barraNavegacion.addView(botonForo);
        barraNavegacion.addView(botonPerfil);

        barraNavegacionRoot = root;

        root.addView(barraNavegacion);

        return root;

    }

    private LinearLayout crearTab(Drawable icono, String etiqueta) {

        LinearLayout tab = new LinearLayout(this);
        tab.setOrientation(LinearLayout.VERTICAL);
        tab.setGravity(Gravity.CENTER);
        tab.setPadding(0, dpToPx(4), 0, dpToPx(4));

        // Ripple nativo
        android.util.TypedValue ripple = new android.util.TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, ripple, true);
        tab.setBackgroundResource(ripple.resourceId);

        ImageView imagen = new ImageView(this);
        imagen.setImageDrawable(icono);
        imagen.setColorFilter(Color.parseColor("#90A4AE"));
        LinearLayout.LayoutParams parametrosImagen = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(22));
        imagen.setLayoutParams(parametrosImagen);

        TextView texto = new TextView(this);
        texto.setText(etiqueta);
        texto.setTextSize(10f);
        texto.setTextColor(Color.parseColor("#90A4AE"));
        texto.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams parametrosTexto = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        parametrosTexto.topMargin = dpToPx(3);
        texto.setLayoutParams(parametrosTexto);

        tab.addView(imagen);
        tab.addView(texto);

        return tab;

    }

    // ── Lógica de selección de tab ────────────────────────────────────────────

    private void seleccionarTab(LinearLayout tab, Runnable accion) {
        marcarTab(tab);
        animarIndicador(tab);
        if (accion != null) accion.run();
    }

    private void marcarTab(LinearLayout tab) {

        int colorActivo   = Color.parseColor("#F06292");
        int colorInactivo = Color.parseColor("#90A4AE");

        for (LinearLayout boton : new LinearLayout[] {botonInicio, botonPuzzles, botonForo, botonRanking, botonPerfil}) {

            boolean activo = boton == tab;

            if (boton.getChildAt(0) instanceof ImageView) {

                ImageView imagen = (ImageView) boton.getChildAt(0);

                if (boton == botonRanking) {

                    if (activo) {
                        imagen.clearColorFilter();
                    } else {
                        imagen.setColorFilter(Color.parseColor("#90A4AE"));
                    }

                } else {
                    imagen.setColorFilter(activo ? colorActivo : colorInactivo);
                }

                imagen.animate()
                        .scaleX(activo ? 1.15f : 1f)
                        .scaleY(activo ? 1.15f : 1f)
                        .setDuration(180)
                        .start();

            }

            if (boton.getChildAt(1) instanceof TextView) {
                TextView texto = (TextView) boton.getChildAt(1);
                texto.setTextColor(activo ? colorActivo : colorInactivo);
                texto.setTypeface(null, activo ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
    }

    private void animarIndicador(LinearLayout tab) {

        tab.post(() -> {
            //int[] tabs = {0, 1, 2, 3, 4};  // 5 tabs
            int indiceTab = getTabIndex(tab);
            int anchoPantalla = getResources().getDisplayMetrics().widthPixels;
            float anchoTab = anchoPantalla / 5f;
            float posicion  = indiceTab * anchoTab + (anchoTab / 2f) - dpToPx(16);

            indicadorActivo.animate()
                    .translationX(posicion)
                    .setDuration(250)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        });

    }

    private int getTabIndex(LinearLayout tab) {
        if (tab == botonInicio)  return 0;
        if (tab == botonPuzzles) return 1;
        if (tab == botonRanking) return 2;
        if (tab == botonForo)    return 3;
        return 4;
    }

    // ── Pantalla de bloqueo ───────────────────────────────────────────────────

    @SuppressLint("SetTextI18n")
    private void mostrarPantallaBloqueo() {

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);

        LinearLayout centro = new LinearLayout(this);
        centro.setOrientation(LinearLayout.VERTICAL);
        centro.setGravity(Gravity.CENTER);
        centro.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        TextView textoIcono = new TextView(this);
        textoIcono.setText("🚫");
        textoIcono.setTextSize(64);
        textoIcono.setGravity(Gravity.CENTER);

        TextView textoMensaje = new TextView(this);
        textoMensaje.setText("Usuario bloqueado");
        textoMensaje.setTextSize(22);
        textoMensaje.setTypeface(null, Typeface.BOLD);
        textoMensaje.setTextColor(Color.parseColor("#C62828"));
        textoMensaje.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams parametrosMensaje = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parametrosMensaje.setMargins(0, 24, 0, 0);
        textoMensaje.setLayoutParams(parametrosMensaje);

        centro.addView(textoIcono);
        centro.addView(textoMensaje);
        layout.addView(centro);
        setContentView(layout);

    }

    // ── Puzzles ───────────────────────────────────────────────────────────────

    private void mostrarPuzzles(List<Puzzle> lista) {

        contenedorPuzzles.removeAllViews();

        for (Puzzle p : lista) {
            contenedorPuzzles.addView(crearTarjeta(p));
        }

    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjeta(Puzzle puzzle) {

        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(40, 35, 40, 35);

        LinearLayout.LayoutParams parametrosTarjeta = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parametrosTarjeta.setMargins(0, 0, 0, 28);
        tarjeta.setLayoutParams(parametrosTarjeta);

        GradientDrawable fondoTarjeta = new GradientDrawable();
        fondoTarjeta.setColor(Color.WHITE);
        fondoTarjeta.setCornerRadius(40);
        fondoTarjeta.setStroke(3, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(fondoTarjeta);

        TextView textoTitulo = new TextView(this);
        textoTitulo.setText(puzzle.getAutor());
        textoTitulo.setTextSize(17);
        textoTitulo.setTypeface(null, Typeface.BOLD);
        textoTitulo.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(textoTitulo);

        TextView textoDescripcion = new TextView(this);
        textoDescripcion.setText(puzzle.getDescripcion());
        textoDescripcion.setTextSize(14);
        textoDescripcion.setTextColor(Color.parseColor("#78909C"));
        LinearLayout.LayoutParams parametrosDescripcion = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parametrosDescripcion.setMargins(0, 8, 0, 16);
        textoDescripcion.setLayoutParams(parametrosDescripcion);
        tarjeta.addView(textoDescripcion);

        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);

        TextView textoDificultad = new TextView(this);
        textoDificultad.setText("⭐ " + (puzzle.getDificultad() != null ? puzzle.getDificultad() : "Normal"));
        textoDificultad.setTextSize(13);
        textoDificultad.setTextColor(Color.parseColor("#26A69A"));
        textoDificultad.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        fila.addView(textoDificultad);
        tarjeta.addView(fila);

        // Abrir dialog de detalle y valoración al pulsar la tarjeta
        tarjeta.setOnClickListener(v ->
                PuzzleDialogFragment.newInstance(puzzle)
                        .show(getSupportFragmentManager(), "puzzle_dialog")
        );

        return tarjeta;

    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    public void ocultarBarra() {
        if (barraNavegacionRoot == null) return;

        barraNavegacionRoot.animate()
                .translationY(barraNavegacionRoot.getHeight())
                .alpha(0f)
                .setDuration(250)
                .start();
    }

    public void mostrarBarra() {
        if (barraNavegacionRoot == null) return;

        barraNavegacionRoot.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(250)
                .start();
    }

}