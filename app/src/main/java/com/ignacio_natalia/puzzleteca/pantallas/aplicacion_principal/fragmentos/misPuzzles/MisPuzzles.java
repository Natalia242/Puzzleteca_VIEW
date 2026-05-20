package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.misPuzzles;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.EditarPuzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.NuevoPuzzle;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.ArrayList;
import java.util.List;

public class MisPuzzles extends Fragment {

    // ── Estado de la pantalla ─────────────────────────────────────────────────
    private LinearLayout contenedor;
    private ScrollView   scroll;
    private FrameLayout  root;
    private TextView     tvContador;

    public MisPuzzles() {}

    // ══════════════════════════════════════════════════════════════════════════
    //  Ciclo de vida
    // ══════════════════════════════════════════════════════════════════════════

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        root = new FrameLayout(requireContext());

        // ── ScrollView principal ──────────────────────────────────────────────
        scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        scroll.setClipToPadding(false);

        // Columna raíz dentro del scroll
        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(16), dp(16), dp(16), dp(90)); // margen inferior para el FAB

        scroll.addView(contenedor);
        root.addView(scroll);

        // Cabecera (se rellena al cargar datos)
        contenedor.addView(crearCabecera());

        // Shimmer / skeleton mientras carga
        contenedor.addView(crearSkeleton());

        cargarDatos();

        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Carga de datos
    // ══════════════════════════════════════════════════════════════════════════

    private void cargarDatos() {
        String token    = GestorSesion.obtenerToken(requireContext());
        int    idUsuario = GestorSesion.obtenerId_usuario(requireContext());

        new PuzzleRepositorio().misPuzzles(token, idUsuario,
                new retrofit2.Callback<List<Puzzle>>() {

                    @Override
                    public void onResponse(@NonNull retrofit2.Call<List<Puzzle>> call,
                                           @NonNull retrofit2.Response<List<Puzzle>> response) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null) {
                                mostrarMisPuzzles(response.body());
                            } else if (response.code() == 404) {
                                mostrarMisPuzzles(new ArrayList<>());
                            } else {
                                Toast.makeText(getContext(),
                                        "Error " + response.code() + " al cargar tus puzzles",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(@NonNull retrofit2.Call<List<Puzzle>> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(),
                                        "Sin conexión: " + t.getMessage(),
                                        Toast.LENGTH_LONG).show());
                    }
                });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Renderizado de la lista
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarMisPuzzles(List<Puzzle> lista) {
        // Borrar skeleton y tarjetas anteriores (conservar cabecera en pos 0)
        while (contenedor.getChildCount() > 1) {
            contenedor.removeViewAt(1);
        }
        // Borrar FAB anterior si existe
        if (root.getChildCount() > 1) {
            root.removeViews(1, root.getChildCount() - 1);
        }

        List<Puzzle> visibles = new ArrayList<>();
        for (Puzzle p : lista) {
            if (p.getEstado() != Puzzle.Estados.Bloqueado) visibles.add(p);
        }

        // Actualizar contador en cabecera
        if (tvContador != null) {
            tvContador.setText(visibles.size() + (visibles.size() == 1 ? " puzzle" : " puzzles"));
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

    // ══════════════════════════════════════════════════════════════════════════
    //  Cabecera
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearCabecera() {
        LinearLayout cab = new LinearLayout(requireContext());
        cab.setOrientation(LinearLayout.HORIZONTAL);
        cab.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(16);
        cab.setLayoutParams(lp);

        LinearLayout textos = new LinearLayout(requireContext());
        textos.setOrientation(LinearLayout.VERTICAL);
        textos.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        tvContador = new TextView(requireContext());
        tvContador.setText("Cargando…");
        tvContador.setTextSize(24);
        tvContador.setTypeface(null, Typeface.BOLD);
        tvContador.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));

        textos.addView(tvContador);
        cab.addView(textos);

        // Emoji decorativo
        TextView emoji = new TextView(requireContext());
        emoji.setText("🧩");
        emoji.setTextSize(28);
        cab.addView(emoji);

        return cab;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Skeleton de carga
    // ══════════════════════════════════════════════════════════════════════════

    private LinearLayout crearSkeleton() {
        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        for (int i = 0; i < 3; i++) {
            View block = new View(requireContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, dp(180));
            lp.bottomMargin = dp(16);
            block.setLayoutParams(lp);
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(ContextCompat.getColor(requireContext(), R.color.app_borde_gris));
            bg.setCornerRadius(dp(16));
            block.setBackground(bg);
            col.addView(block);
        }
        return col;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Tarjeta de puzzle
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjeta(Puzzle puzzle) {

        // ── Tarjeta raíz ─────────────────────────────────────────────────────
        LinearLayout tarjeta = new LinearLayout(getContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setElevation(dp(3));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(16);
        tarjeta.setLayoutParams(lp);

        int r = dp(16);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(r);
        tarjeta.setBackground(bg);
        tarjeta.setClipToOutline(true);
        tarjeta.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View v, android.graphics.Outline o) {
                o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), r);
            }
        });

        // ── Bloque imagen + título superpuesto ────────────────────────────────
        FrameLayout imgFrame = new FrameLayout(getContext());
        imgFrame.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(175)));

        ImageView imagen = new ImageView(getContext());
        imagen.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imagen.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_gris_light));

        String url = puzzle.getImagenUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(this)
                    .load(url)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_gallery)
                    .centerCrop()
                    .into(imagen);
        } else {
            // Fondo de color degradado cuando no hay imagen
            GradientDrawable placeholder = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{ContextCompat.getColor(requireContext(), R.color.app_teal_borde), ContextCompat.getColor(requireContext(), R.color.app_teal_borde_light)});
            imagen.setBackground(placeholder);
            // Emoji centrado como placeholder
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // Degradado inferior
        View gradOverlay = new View(getContext());
        FrameLayout.LayoutParams gradLP =
                new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dp(80));
        gradLP.gravity = Gravity.BOTTOM;
        gradOverlay.setLayoutParams(gradLP);
        GradientDrawable grad = new GradientDrawable(
                GradientDrawable.Orientation.BOTTOM_TOP,
                new int[]{0xEE000000, 0x00000000});
        gradOverlay.setBackground(grad);

        // Chip de estado (arriba derecha)
        TextView chipEstado = crearChipEstado(puzzle.getEstado());
        FrameLayout.LayoutParams chipLP = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        chipLP.gravity = Gravity.TOP | Gravity.END;
        chipLP.setMargins(0, dp(10), dp(10), 0);
        chipEstado.setLayoutParams(chipLP);

        // Título del puzzle sobre el degradado
        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText(puzzle.getTitulo());
        tvTitulo.setTextSize(17);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(Color.WHITE);
        tvTitulo.setShadowLayer(4f, 0f, 2f, 0x99000000);
        tvTitulo.setMaxLines(2);
        tvTitulo.setEllipsize(android.text.TextUtils.TruncateAt.END);
        FrameLayout.LayoutParams titLP = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        titLP.gravity = Gravity.BOTTOM;
        titLP.setMargins(dp(14), 0, dp(50), dp(10));
        tvTitulo.setLayoutParams(titLP);

        imgFrame.addView(imagen);
        imgFrame.addView(gradOverlay);
        imgFrame.addView(chipEstado);
        imgFrame.addView(tvTitulo);

        // ── Cuerpo de la tarjeta ──────────────────────────────────────────────
        LinearLayout cuerpo = new LinearLayout(getContext());
        cuerpo.setOrientation(LinearLayout.VERTICAL);
        cuerpo.setPadding(dp(14), dp(12), dp(14), dp(14));

        // Descripción (truncada a 2 líneas)
        if (puzzle.getDescripcion() != null && !puzzle.getDescripcion().isEmpty()) {
            TextView tvDesc = new TextView(getContext());
            tvDesc.setText(puzzle.getDescripcion());
            tvDesc.setTextSize(13);
            tvDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
            tvDesc.setMaxLines(2);
            tvDesc.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams descLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            descLP.bottomMargin = dp(10);
            tvDesc.setLayoutParams(descLP);
            cuerpo.addView(tvDesc);
        }

        // ── Fila de chips de info ─────────────────────────────────────────────
        LinearLayout filaChips = new LinearLayout(getContext());
        filaChips.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams chipsLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        chipsLP.bottomMargin = dp(10);
        filaChips.setLayoutParams(chipsLP);

        if (puzzle.getDificultad() != null) {
            filaChips.addView(crearChipInfo(
                    emojiDificultad(puzzle.getDificultad()) + " " + puzzle.getDificultad().name(),
                    ContextCompat.getColor(requireContext(), R.color.app_teal_dark), ContextCompat.getColor(requireContext(), R.color.app_teal_soft)));
        }
        if (puzzle.getPiezas() != null) {
            filaChips.addView(crearChipInfo("🧩 " + puzzle.getPiezas() + " piezas",
                    ContextCompat.getColor(requireContext(), R.color.app_admin_indigo), ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft)));
        }
        if (puzzle.getTiempo() != null) {
            filaChips.addView(crearChipInfo("⏱ " + puzzle.getTiempo() + " h",
                    ContextCompat.getColor(requireContext(), R.color.app_naranja), ContextCompat.getColor(requireContext(), R.color.app_naranja_soft)));
        }
        cuerpo.addView(filaChips);

        // ── Fila inferior: valoración + botón editar ──────────────────────────
        LinearLayout filaBottom = new LinearLayout(getContext());
        filaBottom.setOrientation(LinearLayout.HORIZONTAL);
        filaBottom.setGravity(Gravity.CENTER_VERTICAL);
        filaBottom.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Mini estrellas + texto valoración
        LinearLayout filaVal = new LinearLayout(getContext());
        filaVal.setOrientation(LinearLayout.HORIZONTAL);
        filaVal.setGravity(Gravity.CENTER_VERTICAL);
        filaVal.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        MiniStarsView stars = new MiniStarsView(getContext(), puzzle.getValoracion());
        LinearLayout.LayoutParams starsLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        starsLP.setMargins(0, 0, dp(6), 0);
        stars.setLayoutParams(starsLP);

        TextView tvVal = new TextView(getContext());
        @SuppressLint("DefaultLocale") String valTxt = (puzzle.getValoracion() != null && puzzle.getValoracion() > 0)
                ? String.format("%.1f", puzzle.getValoracion()) + "/5"
                : "Sin valorar";
        tvVal.setText(valTxt);
        tvVal.setTextSize(12);
        tvVal.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));

        filaVal.addView(stars);
        filaVal.addView(tvVal);

        // Botón editar
        LinearLayout btnEditar = new LinearLayout(getContext());
        btnEditar.setOrientation(LinearLayout.HORIZONTAL);
        btnEditar.setGravity(Gravity.CENTER);
        btnEditar.setPadding(dp(14), dp(8), dp(14), dp(8));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_teal_soft));
        btnBg.setCornerRadius(dp(20));
        btnEditar.setBackground(btnBg);

        TextView tvEditarEmoji = new TextView(getContext());
        tvEditarEmoji.setText("✏️");
        tvEditarEmoji.setTextSize(14);

        TextView tvEditarTexto = new TextView(getContext());
        tvEditarTexto.setText(" Editar");
        tvEditarTexto.setTextSize(13);
        tvEditarTexto.setTypeface(null, Typeface.BOLD);
        tvEditarTexto.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_teal_dark));

        btnEditar.addView(tvEditarEmoji);
        btnEditar.addView(tvEditarTexto);

        filaBottom.addView(filaVal);
        filaBottom.addView(btnEditar);
        cuerpo.addView(filaBottom);

        // ── Montaje ───────────────────────────────────────────────────────────
        tarjeta.addView(imgFrame);
        tarjeta.addView(cuerpo);

        // Click en el botón editar
        btnEditar.setOnClickListener(v -> irAEditar(puzzle));
        // Click en la tarjeta completa también abre edición
        tarjeta.setOnClickListener(v -> irAEditar(puzzle));

        return tarjeta;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Vista vacía
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarVistaVacia() {
        LinearLayout vacio = new LinearLayout(getContext());
        vacio.setOrientation(LinearLayout.VERTICAL);
        vacio.setGravity(Gravity.CENTER_HORIZONTAL);
        vacio.setPadding(dp(32), dp(80), dp(32), dp(40));
        vacio.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // Círculo con emoji
        TextView tvEmoji = new TextView(getContext());
        tvEmoji.setText("🧩");
        tvEmoji.setTextSize(48);
        tvEmoji.setGravity(Gravity.CENTER);
        int circleSize = dp(110);
        LinearLayout.LayoutParams emojiLP =
                new LinearLayout.LayoutParams(circleSize, circleSize);
        emojiLP.bottomMargin = dp(24);
        tvEmoji.setLayoutParams(emojiLP);
        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_rosa_soft));
        tvEmoji.setBackground(circleBg);

        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText("Aún no tienes puzzles");
        tvTitulo.setTextSize(20);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        tvTitulo.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        titLP.bottomMargin = dp(8);
        tvTitulo.setLayoutParams(titLP);

        TextView tvSub = new TextView(getContext());
        tvSub.setText("Crea tu primer puzzle y compártelo\ncon la comunidad");
        tvSub.setTextSize(14);
        tvSub.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        tvSub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        subLP.bottomMargin = dp(32);
        tvSub.setLayoutParams(subLP);

        // Botón crear
        LinearLayout btnCrear = new LinearLayout(getContext());
        btnCrear.setOrientation(LinearLayout.HORIZONTAL);
        btnCrear.setGravity(Gravity.CENTER);
        btnCrear.setPadding(dp(28), dp(14), dp(28), dp(14));
        GradientDrawable btnBg = new GradientDrawable();
        btnBg.setColor(ContextCompat.getColor(requireContext(), R.color.dark_pink));
        btnBg.setCornerRadius(dp(14));
        btnCrear.setBackground(btnBg);
        btnCrear.setElevation(dp(4));

        TextView tvBtnEmoji = new TextView(getContext());
        tvBtnEmoji.setText("➕");
        tvBtnEmoji.setTextSize(16);
        tvBtnEmoji.setTextColor(Color.WHITE);

        TextView tvBtnTexto = new TextView(getContext());
        tvBtnTexto.setText("  Crear mi primer puzzle");
        tvBtnTexto.setTextSize(15);
        tvBtnTexto.setTypeface(null, Typeface.BOLD);
        tvBtnTexto.setTextColor(Color.WHITE);

        btnCrear.addView(tvBtnEmoji);
        btnCrear.addView(tvBtnTexto);
        btnCrear.setOnClickListener(v -> irARegistrar());

        vacio.addView(tvEmoji);
        vacio.addView(tvTitulo);
        vacio.addView(tvSub);
        vacio.addView(btnCrear);

        contenedor.addView(vacio);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  FAB flotante
    // ══════════════════════════════════════════════════════════════════════════

    private void mostrarBotonFlotante() {
        // Pill "Nuevo puzzle" en lugar del icono genérico
        LinearLayout fab = new LinearLayout(getContext());
        fab.setOrientation(LinearLayout.HORIZONTAL);
        fab.setGravity(Gravity.CENTER);
        fab.setPadding(dp(20), 0, dp(20), 0);

        FrameLayout.LayoutParams fabLP = new FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, dp(52));
        fabLP.gravity = Gravity.BOTTOM | Gravity.END;
        fabLP.setMargins(0, 0, dp(20), dp(20));
        fab.setLayoutParams(fabLP);

        GradientDrawable fabBg = new GradientDrawable();
        fabBg.setColor(ContextCompat.getColor(requireContext(), R.color.dark_pink));
        fabBg.setCornerRadius(dp(26));
        fab.setBackground(fabBg);
        fab.setElevation(dp(8));

        TextView tvPlus = new TextView(getContext());
        tvPlus.setText("＋");
        tvPlus.setTextSize(20);
        tvPlus.setTypeface(null, Typeface.BOLD);
        tvPlus.setTextColor(Color.WHITE);

        TextView tvLabel = new TextView(getContext());
        tvLabel.setText("  Nuevo puzzle");
        tvLabel.setTextSize(14);
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setTextColor(Color.WHITE);

        fab.addView(tvPlus);
        fab.addView(tvLabel);
        fab.setOnClickListener(v -> irARegistrar());

        root.addView(fab);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers de UI
    // ══════════════════════════════════════════════════════════════════════════

    private TextView crearChipEstado(Puzzle.Estados estado) {
        TextView chip = new TextView(getContext());
        boolean esPublico = (estado == Puzzle.Estados.Publico);
        chip.setText(esPublico ? "🌍 Público" : "🔒 Privado");
        chip.setTextSize(11);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setTextColor(Color.WHITE);
        chip.setPadding(dp(10), dp(4), dp(10), dp(4));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(esPublico ? 0xCC2E7D6E : 0xCC546E7A);
        bg.setCornerRadius(dp(20));
        chip.setBackground(bg);
        return chip;
    }

    private TextView crearChipInfo(String texto, int colorTexto, int colorFondo) {
        TextView chip = new TextView(getContext());
        chip.setText(texto);
        chip.setTextSize(11);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setTextColor(colorTexto);
        chip.setPadding(dp(10), dp(5), dp(10), dp(5));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(8), 0);
        chip.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(colorFondo);
        bg.setCornerRadius(dp(20));
        chip.setBackground(bg);
        return chip;
    }

    private String emojiDificultad(Puzzle.Dificultades d) {
        switch (d) {
            case Facil:  return "🟢";
            case Media:  return "🟡";
            case Dificil:return "🟠";
            case Extremo:return "🔴";
            default:     return "⚪";
        }
    }

    private void irAEditar(Puzzle puzzle) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(AppPrincipal.FRAGMENTO_ID, EditarPuzzle.newInstance(puzzle))
                .addToBackStack(null)
                .commit();
    }

    private void irARegistrar() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(AppPrincipal.FRAGMENTO_ID, new NuevoPuzzle())
                .addToBackStack(null)
                .commit();
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Vista de mini estrellas (reutilizada del PuzzleDialogFragment)
    // ══════════════════════════════════════════════════════════════════════════

    public static class MiniStarsView extends android.view.View {

        private static final int   NUM   = 5;
        private static final float SIZE  = 14f;
        private static final float GAP   = 3f;
        private static final float CR    = 2f;

        private final Paint pFill  = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint pEmpty = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final float sizePx, gapPx, crPx;
        private final int   rating;

        public MiniStarsView(android.content.Context ctx, Double rating) {
            super(ctx);
            float d = ctx.getResources().getDisplayMetrics().density;
            sizePx  = SIZE * d;
            gapPx   = GAP  * d;
            crPx    = CR   * d;
            this.rating = (rating != null) ? Math.max(0, Math.min(NUM, (int) Math.round(rating))) : 0;
            pFill.setStyle(Paint.Style.FILL);
            pEmpty.setStyle(Paint.Style.FILL);
            pEmpty.setColor(ContextCompat.getColor(getContext(), R.color.app_borde_gris_light));
        }

        @Override
        protected void onMeasure(int wSpec, int hSpec) {
            int w = (int)(NUM * sizePx + (NUM - 1) * gapPx);
            setMeasuredDimension(resolveSize(w, wSpec), resolveSize((int) sizePx, hSpec));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float cx = sizePx / 2f, cy = sizePx / 2f;
            for (int i = 0; i < NUM; i++) {
                float ox = i * (sizePx + gapPx);
                if (i < rating) {
                    pFill.setShader(new LinearGradient(
                            ox, 0, ox, sizePx,
                            ContextCompat.getColor(getContext(), R.color.app_estrella_light),
                            ContextCompat.getColor(getContext(), R.color.app_estrella_dark),
                            Shader.TileMode.CLAMP));
                    canvas.drawPath(roundedStar(cx + ox, cy,
                            sizePx * 0.45f, sizePx * 0.18f, crPx), pFill);
                } else {
                    canvas.drawPath(roundedStar(cx + ox, cy,
                            sizePx * 0.45f, sizePx * 0.18f, crPx), pEmpty);
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
                int prev = (i-1+n)%n, next = (i+1)%n;
                float inDx = vx[i]-vx[prev], inDy = vy[i]-vy[prev];
                float outDx = vx[next]-vx[i], outDy = vy[next]-vy[i];
                float inL = (float)Math.hypot(inDx,inDy), outL = (float)Math.hypot(outDx,outDy);
                float r2 = (i%2==0) ? cr*2f : cr*0.8f;
                r2 = Math.min(r2, Math.min(inL,outL)*0.35f);
                float t1x = vx[i]-inDx/inL*r2,  t1y = vy[i]-inDy/inL*r2;
                float t2x = vx[i]+outDx/outL*r2, t2y = vy[i]+outDy/outL*r2;
                if (i == 0) path.moveTo(t1x,t1y); else path.lineTo(t1x,t1y);
                path.quadTo(vx[i],vy[i],t2x,t2y);
            }
            path.close();
            return path;
        }
    }
}