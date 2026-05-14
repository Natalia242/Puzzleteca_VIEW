package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;

import com.bumptech.glide.Glide;

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

        puzzleViewModel.getPuzzleCreado().observe(this, creado -> {
            if (Boolean.TRUE.equals(creado)) {
                puzzleViewModel.cargarPuzzles(GestorSesion.obtenerToken(this));
            }
        });

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

    public void actualizarTituloPantalla(int drawableRes) {
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
        botonPuzzles = crearTab(ContextCompat.getDrawable(this, R.drawable.puzzle),  "Mis Puzzles");
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

        botonPuzzles.setOnClickListener(vista -> {
            if (esInvitado()) {
                mostrarMensajeAccesoRestringido();
                return;
            }
            seleccionarTab(botonPuzzles, () -> {
                actualizarTituloPantalla(R.drawable.titulo_mis_puzzles_recortado);
                mostrarFragmento(new MisPuzzles());
            });
        });

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
            if (esInvitado()) {
                mostrarMensajeAccesoRestringido();
                return;
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lista = lista.reversed();
        }

        for (Puzzle p : lista) {
            contenedorPuzzles.addView(crearTarjeta(p));
        }

    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjeta(Puzzle puzzle) {
        int r = dpToPx(16);

        // ── Tarjeta raíz ─────────────────────────────────────────────────────
        LinearLayout tarjeta = new LinearLayout(this);
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setElevation(dpToPx(3));

        LinearLayout.LayoutParams cardLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLP.setMargins(0, 0, 0, dpToPx(16));
        tarjeta.setLayoutParams(cardLP);

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.WHITE);
        cardBg.setCornerRadius(r);
        cardBg.setStroke(dpToPx(1), Color.parseColor("#E8F5E9"));
        tarjeta.setBackground(cardBg);
        tarjeta.setClipToOutline(true);
        tarjeta.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View v, android.graphics.Outline o) {
                o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), r);
            }
        });

        // ── Bloque superior: imagen con título superpuesto ────────────────────
        FrameLayout imgFrame = new FrameLayout(this);
        imgFrame.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dpToPx(160)));

        ImageView imagen = new ImageView(this);
        imagen.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagen.setBackgroundColor(Color.parseColor("#F0F4F8"));

        String url = puzzle.getImagenUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .into(imagen);
        } else {
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Degradado inferior sobre la imagen
        View gradOverlay = new View(this);
        FrameLayout.LayoutParams gradLP = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, dpToPx(72));
        gradLP.gravity = Gravity.BOTTOM;
        gradOverlay.setLayoutParams(gradLP);
        GradientDrawable grad = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xDD000000, 0x00000000});
        gradOverlay.setBackground(grad);

        // Título del puzzle sobre el degradado
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(puzzle.getTitulo());
        tvTitulo.setTextSize(16);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.WHITE);
        tvTitulo.setShadowLayer(4f, 0f, 2f, 0x99000000);
        tvTitulo.setMaxLines(1);
        tvTitulo.setEllipsize(android.text.TextUtils.TruncateAt.END);
        FrameLayout.LayoutParams titLP = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        titLP.gravity = Gravity.BOTTOM;
        titLP.setMargins(dpToPx(14), 0, dpToPx(14), dpToPx(10));
        tvTitulo.setLayoutParams(titLP);

        imgFrame.addView(imagen);
        imgFrame.addView(gradOverlay);
        imgFrame.addView(tvTitulo);

        // ── Bloque inferior: autor + estrellas + flecha ───────────────────────
        LinearLayout infoRow = new LinearLayout(this);
        infoRow.setOrientation(LinearLayout.HORIZONTAL);
        infoRow.setGravity(Gravity.CENTER_VERTICAL);
        infoRow.setPadding(dpToPx(14), dpToPx(10), dpToPx(12), dpToPx(12));

        // Avatar circular con inicial del autor
        TextView avatar = new TextView(this);
        String autorStr = puzzle.getAutor() != null ? puzzle.getAutor() : "?";
        avatar.setText(String.valueOf(autorStr.charAt(0)).toUpperCase());
        avatar.setTextSize(14);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.WHITE);
        avatar.setGravity(Gravity.CENTER);
        int avatarSize = dpToPx(34);
        LinearLayout.LayoutParams avatarLP = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarLP.setMargins(0, 0, dpToPx(10), 0);
        avatar.setLayoutParams(avatarLP);
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(Color.parseColor("#2E7D6E"));
        avatar.setBackground(avatarBg);

        // Columna: nombre autor + estrellas
        LinearLayout colInfo = new LinearLayout(this);
        colInfo.setOrientation(LinearLayout.VERTICAL);
        colInfo.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvAutor = new TextView(this);
        tvAutor.setText(autorStr);
        tvAutor.setTextSize(13);
        tvAutor.setTypeface(null, Typeface.BOLD);
        tvAutor.setTextColor(Color.parseColor("#37474F"));
        tvAutor.setMaxLines(1);
        tvAutor.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // Estrellas mini visuales
        MiniStarsView miniStars = new MiniStarsView(this, puzzle.getValoracion());
        LinearLayout.LayoutParams starsLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        starsLP.setMargins(0, dpToPx(3), 0, 0);
        miniStars.setLayoutParams(starsLP);

        colInfo.addView(tvAutor);
        colInfo.addView(miniStars);

        // Flecha derecha
        TextView flecha = new TextView(this);
        flecha.setText("›");
        flecha.setTextSize(28);
        flecha.setTextColor(Color.parseColor("#A5D6A7"));
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams flechaLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        flechaLP.setMargins(dpToPx(6), 0, dpToPx(4), 0);
        flechaLP.gravity = Gravity.CENTER_VERTICAL;
        flecha.setLayoutParams(flechaLP);

        infoRow.addView(avatar);
        infoRow.addView(colInfo);
        infoRow.addView(flecha);

        // ── Montaje ───────────────────────────────────────────────────────────
        tarjeta.addView(imgFrame);
        tarjeta.addView(infoRow);

        // Feedback táctil + abrir dialog
        tarjeta.setOnClickListener(v ->
                PuzzleDialogFragment.newInstance(puzzle)
                        .show(getSupportFragmentManager(), "puzzle_dialog"));

        return tarjeta;
    }

    // ── Vista de estrellas mini (solo visual, sin interacción) ────────────────

    private static class MiniStarsView extends View {

        private static final int   NUM   = 5;
        private static final float SIZE  = 13f;   // dp
        private static final float GAP   = 3f;    // dp
        private static final float CR    = 1.8f;  // corner radius dp

        private final Paint pFill   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pEmpty  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float sizePx, gapPx, crPx;
        private final int   rating;

        MiniStarsView(android.content.Context ctx, Integer rating) {
            super(ctx);
            float d = ctx.getResources().getDisplayMetrics().density;
            sizePx = SIZE * d;
            gapPx  = GAP  * d;
            crPx   = CR   * d;
            this.rating = (rating != null) ? Math.max(0, Math.min(NUM, rating)) : 0;

            pFill.setStyle(Paint.Style.FILL);
            pEmpty.setStyle(Paint.Style.FILL);
            pEmpty.setColor(Color.parseColor("#DDDDDD"));
        }

        @Override
        protected void onMeasure(int wSpec, int hSpec) {
            int w = (int)(NUM * sizePx + (NUM - 1) * gapPx);
            setMeasuredDimension(resolveSize(w, wSpec), resolveSize((int) sizePx, hSpec));
        }

        @SuppressLint("DrawAllocation")
        @Override
        protected void onDraw(Canvas canvas) {
            float cx = sizePx / 2f, cy = sizePx / 2f;
            for (int i = 0; i < NUM; i++) {
                float ox = i * (sizePx + gapPx);
                if (i < rating) {
                    pFill.setShader(new LinearGradient(
                            ox, 0, ox, sizePx,
                            Color.parseColor("#FFD740"),
                            Color.parseColor("#FF8F00"),
                            Shader.TileMode.CLAMP));
                    canvas.drawPath(roundedStar(cx + ox, cy, sizePx * 0.45f,
                            sizePx * 0.18f, crPx), pFill);
                } else {
                    canvas.drawPath(roundedStar(cx + ox, cy, sizePx * 0.45f,
                            sizePx * 0.18f, crPx), pEmpty);
                }
            }
        }

        private Path roundedStar(float cx, float cy, float outer, float inner, float cr) {
            Path path = new Path();
            int pts = 5;
            double step = Math.PI * 2 / pts;
            double start = -Math.PI / 2;
            float[] vx = new float[pts * 2], vy = new float[pts * 2];
            for (int i = 0; i < pts; i++) {
                double ao = start + i * step, ai = ao + step / 2.0;
                vx[i*2]   = cx + (float)(outer * Math.cos(ao));
                vy[i*2]   = cy + (float)(outer * Math.sin(ao));
                vx[i*2+1] = cx + (float)(inner * Math.cos(ai));
                vy[i*2+1] = cy + (float)(inner * Math.sin(ai));
            }
            int n = vx.length;
            for (int i = 0; i < n; i++) {
                int prev = (i - 1 + n) % n, next = (i + 1) % n;
                float inDx = vx[i]-vx[prev], inDy = vy[i]-vy[prev];
                float outDx = vx[next]-vx[i], outDy = vy[next]-vy[i];
                float inL = (float)Math.hypot(inDx,inDy), outL = (float)Math.hypot(outDx,outDy);
                float r = ((i%2==0) ? cr*2f : cr*0.8f);
                r = Math.min(r, Math.min(inL, outL) * 0.35f);
                float t1x = vx[i]-inDx/inL*r,  t1y = vy[i]-inDy/inL*r;
                float t2x = vx[i]+outDx/outL*r, t2y = vy[i]+outDy/outL*r;
                if (i == 0) path.moveTo(t1x, t1y); else path.lineTo(t1x, t1y);
                path.quadTo(vx[i], vy[i], t2x, t2y);
            }
            path.close();
            return path;
        }
    }

    // ── Utilidades ────────────────────────────────────────────────────────────

    private boolean esInvitado() {
        return GestorSesion.esInvitado(this);
    }

    private void mostrarMensajeAccesoRestringido() {
        Toast.makeText(this,
                "Inicia sesión o regístrate para acceder a esta sección",
                Toast.LENGTH_SHORT).show();
    }

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