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

    // Tabs (ahora LinearLayout en lugar de TextView)
    private LinearLayout btnInicio, btnPuzzles, btnForo, btnRanking, btnPerfil;
    // Indicador deslizante
    private View activeIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#DFF5C9"));

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
        titulo.setText("Puzzles disponibles");
        titulo.setTextSize(22);
        titulo.setTextColor(Color.parseColor("#F06292"));
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setGravity(Gravity.CENTER);
        titulo.setPadding(40, 60, 40, 30);
        root.addView(titulo);

        // ── ScrollView con lista de tarjetas (Inicio) ──
        scrollPuzzles = new ScrollView(this);
        scrollPuzzles.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

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
        root.addView(construirNavBar());

        setContentView(root);

        // ── ViewModel + carga ──
        puzzleViewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);
        String token = GestorSesion.obtenerToken(this);
        puzzleViewModel.getPuzzles().observe(this, this::mostrarPuzzles);
        puzzleViewModel.getError().observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
        puzzleViewModel.cargarPuzzles(token);

        // Seleccionar Inicio por defecto tras layout
        root.post(() -> seleccionarTab(btnInicio, null));
    }

    // ── Navegación ────────────────────────────────────────────────────────────

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

    // ── Nav bar ───────────────────────────────────────────────────────────────

    private LinearLayout construirNavBar() {
        // Contenedor raíz vertical (indicador + barra)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);
        root.setElevation(16f);

        // Indicador deslizante superior
        activeIndicator = new View(this);
        activeIndicator.setBackgroundColor(Color.parseColor("#F06292"));
        LinearLayout.LayoutParams indParams = new LinearLayout.LayoutParams(dpToPx(32), dpToPx(3));
        indParams.gravity = Gravity.START;
        activeIndicator.setLayoutParams(indParams);
        root.addView(activeIndicator);

        // Barra horizontal de tabs
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setPadding(0, dpToPx(6), 0, dpToPx(6));

        btnInicio  = crearTab(ContextCompat.getDrawable(this, R.drawable.ic_home),   "Inicio");
        btnPuzzles = crearTab(ContextCompat.getDrawable(this, R.drawable.ic_puzzle),  "Puzzles");
        btnRanking = crearTab(ContextCompat.getDrawable(this, R.drawable.ic_trophy),  "Ranking");
        btnForo    = crearTab(ContextCompat.getDrawable(this, R.drawable.ic_forum),   "Foro");
        btnPerfil  = crearTab(ContextCompat.getDrawable(this, R.drawable.ic_person),  "Perfil");

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        btnInicio.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        btnPuzzles.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        btnForo.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        btnRanking.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        btnPerfil.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        btnInicio.setOnClickListener(v  -> seleccionarTab(btnInicio,  () -> mostrarInicio()));
        btnPuzzles.setOnClickListener(v -> seleccionarTab(btnPuzzles, () -> mostrarFragmento(new Foro())));
        btnForo.setOnClickListener(v    -> seleccionarTab(btnForo,    () -> mostrarFragmento(new Foro())));
        btnRanking.setOnClickListener(v -> seleccionarTab(btnRanking, () -> mostrarFragmento(new Ranking())));
        btnPerfil.setOnClickListener(v  -> {
            String rol = GestorSesion.obtenerRol(this);
            seleccionarTab(btnPerfil, () -> mostrarFragmento(
                    "Admin".equals(rol) ? new PanelAdmin() : new PanelUsuario()));
        });

        nav.addView(btnInicio);
        nav.addView(btnPuzzles);
        nav.addView(btnRanking);
        nav.addView(btnForo);
        nav.addView(btnPerfil);
        root.addView(nav);

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

        ImageView iv = new ImageView(this);
        iv.setImageDrawable(icono);
        iv.setColorFilter(Color.parseColor("#90A4AE"));
        LinearLayout.LayoutParams ivParams = new LinearLayout.LayoutParams(dpToPx(22), dpToPx(22));
        iv.setLayoutParams(ivParams);

        TextView tv = new TextView(this);
        tv.setText(etiqueta);
        tv.setTextSize(10f);
        tv.setTextColor(Color.parseColor("#90A4AE"));
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.topMargin = dpToPx(3);
        tv.setLayoutParams(tvParams);

        tab.addView(iv);
        tab.addView(tv);
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

        for (LinearLayout t : new LinearLayout[]{btnInicio, btnPuzzles, btnForo, btnRanking, btnPerfil}) {
            boolean activo = t == tab;

            if (t.getChildAt(0) instanceof ImageView) {
                ImageView iv = (ImageView) t.getChildAt(0);
                if (t == btnRanking) {
                    if (activo) {
                        iv.clearColorFilter();
                    } else {
                        iv.setColorFilter(Color.parseColor("#90A4AE"));
                    }
                } else {
                    iv.setColorFilter(activo ? colorActivo : colorInactivo);
                }
                iv.animate()
                        .scaleX(activo ? 1.15f : 1f)
                        .scaleY(activo ? 1.15f : 1f)
                        .setDuration(180)
                        .start();
            }

            if (t.getChildAt(1) instanceof TextView) {
                TextView tv = (TextView) t.getChildAt(1);
                tv.setTextColor(activo ? colorActivo : colorInactivo);
                tv.setTypeface(null, activo ? Typeface.BOLD : Typeface.NORMAL);
            }
        }
    }

    private void animarIndicador(LinearLayout tab) {
        tab.post(() -> {
            int[] tabs = {0, 1, 2, 3, 4};  // 5 tabs
            int idx = getTabIndex(tab);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            float tabWidth = screenWidth / 5f;
            float targetX  = idx * tabWidth + (tabWidth / 2f) - dpToPx(16);

            activeIndicator.animate()
                    .translationX(targetX)
                    .setDuration(250)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();
        });
    }

    private int getTabIndex(LinearLayout tab) {
        if (tab == btnInicio)  return 0;
        if (tab == btnPuzzles) return 1;
        if (tab == btnRanking) return 2;
        if (tab == btnForo)    return 3;
        return 4;
    }

    // ── Pantalla de bloqueo ───────────────────────────────────────────────────

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
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

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
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mp.setMargins(0, 24, 0, 0);
        tvMensaje.setLayoutParams(mp);

        centro.addView(tvIcono);
        centro.addView(tvMensaje);
        layout.addView(centro);
        setContentView(layout);
    }

    // ── Puzzles ───────────────────────────────────────────────────────────────

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
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 28);
        tarjeta.setLayoutParams(params);

        GradientDrawable fondoTarjeta = new GradientDrawable();
        fondoTarjeta.setColor(Color.WHITE);
        fondoTarjeta.setCornerRadius(40);
        fondoTarjeta.setStroke(3, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(fondoTarjeta);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(puzzle.getAutor());
        tvTitulo.setTextSize(17);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.parseColor("#37474F"));
        tarjeta.addView(tvTitulo);

        TextView tvDesc = new TextView(this);
        tvDesc.setText(puzzle.getDescripcion());
        tvDesc.setTextSize(14);
        tvDesc.setTextColor(Color.parseColor("#78909C"));
        LinearLayout.LayoutParams descParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
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

    // ── Utilidades ────────────────────────────────────────────────────────────

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}