package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.PuzzleDialogFragment;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.ArrayList;
import java.util.List;

public class GestionPuzzles extends Fragment {

    private GestionPuzzlesViewModel viewModel;
    private LinearLayout contenedor;
    private EditText buscador;
    private LinearLayout filtrosRow;

    private List<Puzzle> listaTodos = new ArrayList<>();
    private String filtroEstado = null;

    private TextView chipTodos, chipPublico, chipPrivado, chipBloqueado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft),
                        ContextCompat.getColor(requireContext(), R.color.jungle_green)
                }
        );

        root.setBackground(gradient);

        root.addView(crearCabecera());
        root.addView(crearBuscador());

        filtrosRow = crearFiltros();
        LinearLayout.LayoutParams filtrosParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        filtrosParams.setMargins(dp(16), 0, dp(16), dp(8));
        filtrosRow.setLayoutParams(filtrosParams);
        root.addView(filtrosRow);

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        scroll.setFillViewport(true);

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(16), dp(8), dp(16), dp(24));
        scroll.addView(contenedor);
        root.addView(scroll);

        viewModel = new ViewModelProvider(this).get(GestionPuzzlesViewModel.class);
        viewModel.getPuzzles().observe(getViewLifecycleOwner(), lista -> {
            listaTodos = lista != null ? lista : new ArrayList<>();
            aplicarFiltros();
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
        viewModel.getVacio().observe(getViewLifecycleOwner(), esVacio -> {
            if (Boolean.TRUE.equals(esVacio) && listaTodos.isEmpty()) mostrarVacio();
        });

        String token = GestorSesion.obtenerToken(requireContext());
        viewModel.cargarPuzzles(token != null ? token : "");

        return root;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Cabecera
    // ══════════════════════════════════════════════════════════════════════
    private View crearCabecera() {
        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(16), dp(20), dp(16), dp(12));

        TextView titulo = new TextView(requireContext());
        titulo.setText("🧩  Gestión de Puzzles");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_admin_indigo));
        titulo.setGravity(Gravity.START);
        header.addView(titulo);

        TextView subtitulo = new TextView(requireContext());
        subtitulo.setText("Administra el estado de todos los puzzles");
        subtitulo.setTextSize(12);
        subtitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        subtitulo.setPadding(0, dp(2), 0, 0);
        header.addView(subtitulo);

        return header;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Buscador
    // ══════════════════════════════════════════════════════════════════════
    private View crearBuscador() {
        LinearLayout wrap = new LinearLayout(requireContext());
        wrap.setOrientation(LinearLayout.HORIZONTAL);
        wrap.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapParams.setMargins(dp(16), dp(8), dp(16), dp(8));
        wrap.setLayoutParams(wrapParams);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft));
        wrap.setBackground(bg);
        wrap.setPadding(dp(14), dp(10), dp(14), dp(10));

        TextView lupa = new TextView(requireContext());
        lupa.setText("🔍");
        lupa.setTextSize(16);
        lupa.setPadding(0, 0, dp(8), 0);
        wrap.addView(lupa);

        buscador = new EditText(requireContext());
        buscador.setHint("Buscar por título o autor...");
        buscador.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        buscador.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        buscador.setTextSize(14);
        buscador.setBackground(null);
        buscador.setSingleLine(true);
        buscador.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        buscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { aplicarFiltros(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        wrap.addView(buscador);

        return wrap;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Chips de filtro de estado
    // ══════════════════════════════════════════════════════════════════════
    private LinearLayout crearFiltros() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        chipTodos     = crearChip("Todos",     null,        true);
        chipPublico   = crearChip("Público",   "Publico",   false);
        chipPrivado   = crearChip("Privado",   "Privado",   false);
        chipBloqueado = crearChip("Bloqueado", "Bloqueado", false);

        row.addView(chipTodos);
        row.addView(chipPublico);
        row.addView(chipPrivado);
        row.addView(chipBloqueado);

        return row;
    }

    private TextView crearChip(String etiqueta, String estadoFiltro, boolean activo) {
        TextView chip = new TextView(requireContext());
        chip.setText(etiqueta);
        chip.setTextSize(12);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setPadding(dp(14), dp(6), dp(14), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(8), 0);
        chip.setLayoutParams(lp);
        aplicarEstiloChip(chip, activo, estadoFiltro);

        chip.setOnClickListener(v -> {
            filtroEstado = estadoFiltro;
            aplicarEstiloChip(chipTodos,     filtroEstado == null,             null);
            aplicarEstiloChip(chipPublico,   "Publico".equals(filtroEstado),   "Publico");
            aplicarEstiloChip(chipPrivado,   "Privado".equals(filtroEstado),   "Privado");
            aplicarEstiloChip(chipBloqueado, "Bloqueado".equals(filtroEstado), "Bloqueado");
            aplicarFiltros();
        });

        return chip;
    }

    private void aplicarEstiloChip(TextView chip, boolean activo, String estado) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(20));

        int colorTexto;
        if (activo) {
            int colorFondo;
            if ("Publico".equals(estado)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_green_success_text);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else if ("Privado".equals(estado)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_naranja);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else if ("Bloqueado".equals(estado)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_admin_indigo);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            }
            bg.setColor(colorFondo);
        } else {
            bg.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_pagina));
            bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_borde_gris));
            colorTexto = ContextCompat.getColor(requireContext(), R.color.app_subtexto);
        }

        chip.setBackground(bg);
        chip.setTextColor(colorTexto);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Filtrado y renderizado
    // ══════════════════════════════════════════════════════════════════════
    private void aplicarFiltros() {
        String query = buscador != null ? buscador.getText().toString().trim().toLowerCase() : "";

        List<Puzzle> filtrados = new ArrayList<>();
        for (Puzzle p : listaTodos) {
            boolean pasaEstado = filtroEstado == null ||
                    (p.getEstado() != null && filtroEstado.equals(p.getEstado().name()));
            boolean pasaBusqueda = query.isEmpty()
                    || (p.getTitulo() != null && p.getTitulo().toLowerCase().contains(query))
                    || (p.getAutor() != null && p.getAutor().toLowerCase().contains(query));
            if (pasaEstado && pasaBusqueda) filtrados.add(p);
        }

        contenedor.removeAllViews();
        if (filtrados.isEmpty()) {
            mostrarVacio();
        } else {
            for (Puzzle p : filtrados) {
                contenedor.addView(crearTarjetaPuzzle(p));
                espacio(contenedor, dp(10));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Tarjeta de puzzle
    // ══════════════════════════════════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    private View crearTarjetaPuzzle(Puzzle puzzle) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        cardBg.setCornerRadius(dp(14));
        cardBg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_borde_light));
        card.setBackground(cardBg);

        // Toda la tarjeta abre el diálogo, excepto el botón de estado
        card.setOnClickListener(v -> {
            PuzzleDialogFragment dialog = PuzzleDialogFragment.newInstance(puzzle);
            dialog.show(getChildFragmentManager(), "puzzle_detalle");
        });

        // ── Icono de puzzle ───────────────────────────────────────────────
        FrameLayout iconWrap = new FrameLayout(requireContext());
        int iconSize = dp(48);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconParams.setMargins(0, 0, dp(12), 0);
        iconWrap.setLayoutParams(iconParams);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft));
        iconBg.setCornerRadius(dp(10));
        iconWrap.setBackground(iconBg);

        TextView iconTv = new TextView(requireContext());
        iconTv.setText("🧩");
        iconTv.setTextSize(22);
        iconTv.setGravity(Gravity.CENTER);
        iconTv.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        iconWrap.addView(iconTv);
        card.addView(iconWrap);

        // ── Columna de info ───────────────────────────────────────────────
        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText(puzzle.getTitulo() != null ? puzzle.getTitulo() : "Sin título");
        tvTitulo.setTextSize(14);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto_dark));
        tvTitulo.setMaxLines(1);
        tvTitulo.setEllipsize(android.text.TextUtils.TruncateAt.END);
        info.addView(tvTitulo);

        // Fila 2: autor + dificultad + piezas
        LinearLayout fila2 = new LinearLayout(requireContext());
        fila2.setOrientation(LinearLayout.HORIZONTAL);
        fila2.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fila2Params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fila2Params.topMargin = dp(3);
        fila2.setLayoutParams(fila2Params);

        TextView tvAutor = new TextView(requireContext());
        tvAutor.setText("👤 " + (puzzle.getAutor() != null ? puzzle.getAutor() : "—"));
        tvAutor.setTextSize(12);
        tvAutor.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        tvAutor.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        fila2.addView(tvAutor);

        if (puzzle.getDificultad() != null) {
            fila2.addView(crearBadgeDificultad(puzzle.getDificultad()));
        }
        if (puzzle.getPiezas() != null) {
            fila2.addView(crearBadgePiezas(puzzle.getPiezas()));
        }
        info.addView(fila2);

        // Fila 3: badge de estado
        LinearLayout fila3 = new LinearLayout(requireContext());
        fila3.setOrientation(LinearLayout.HORIZONTAL);
        fila3.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fila3Params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        fila3Params.topMargin = dp(5);
        fila3.setLayoutParams(fila3Params);

        TextView tvEstadoBadge = crearBadgeEstado(
                puzzle.getEstado() != null ? puzzle.getEstado().name() : "—");
        fila3.addView(tvEstadoBadge);
        info.addView(fila3);
        card.addView(info);

        // ── Spinner de estado ──────────────────
        Spinner spinnerEstado = new Spinner(requireContext());
        Puzzle.Estados[] estados = Puzzle.Estados.values();
        ArrayAdapter<Puzzle.Estados> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        if (puzzle.getEstado() != null) {
            spinnerEstado.setSelection(puzzle.getEstado().ordinal());
        }
        LinearLayout.LayoutParams spinnerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spinnerParams.setMargins(dp(8), 0, 0, 0);
        spinnerEstado.setLayoutParams(spinnerParams);

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean inicializado = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!inicializado) { inicializado = true; return; }
                Puzzle.Estados seleccionado = estados[position];
                puzzle.setEstado(seleccionado);
                actualizarBadgeEstado(tvEstadoBadge, seleccionado.name());
                viewModel.cambiarEstado(puzzle.getUsuario().getId(), puzzle.getId(), seleccionado.name());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        card.addView(spinnerEstado);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Helpers de badges
    // ══════════════════════════════════════════════════════════════════════
    private TextView crearBadgeEstado(String estadoStr) {
        TextView badge = new TextView(requireContext());
        actualizarBadgeEstado(badge, estadoStr);
        return badge;
    }

    private void actualizarBadgeEstado(TextView badge, String estadoStr) {
        String icono;
        int colorTexto, colorFondo;

        switch (estadoStr) {
            case "Publico":
                icono = "✅ Público";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_green_success_text);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_green_success);
                break;
            case "Privado":
                icono = "🔒 Privado";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_naranja_text);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_naranja_soft);
                break;
            case "Bloqueado":
                icono = "🚫 Bloqueado";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_peligro_dark);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro_bg);
                break;
            default:
                icono = "❓ " + estadoStr;
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_subtexto);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_fondo_card);
                break;
        }

        badge.setText(icono);
        badge.setTextSize(11);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(colorTexto);
        badge.setPadding(dp(8), dp(3), dp(8), dp(3));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(colorFondo);
        bg.setCornerRadius(dp(12));
        badge.setBackground(bg);
    }

    private TextView crearBadgeDificultad(Puzzle.Dificultades dif) {
        TextView badge = new TextView(requireContext());
        int colorTexto, colorFondo;
        String label;

        switch (dif) {
            case Facil:
                label = "Fácil";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_green_success_text);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_green_success);
                break;
            case Media:
                label = "Media";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_naranja_text);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_naranja_soft);
                break;
            case Dificil:
                label = "Difícil";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_peligro_dark);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro_bg);
                break;
            case Extremo:
                label = "Extremo";
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_peligro_darker);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro_bg);
                break;
            default:
                label = dif.name();
                colorTexto = ContextCompat.getColor(requireContext(), R.color.app_subtexto);
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_fondo_card);
        }

        badge.setText(label);
        badge.setTextSize(10);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(colorTexto);
        badge.setPadding(dp(6), dp(2), dp(6), dp(2));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(colorFondo);
        bg.setCornerRadius(dp(10));
        badge.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(6), 0, 0, 0);
        badge.setLayoutParams(lp);
        return badge;
    }

    private TextView crearBadgePiezas(int piezas) {
        TextView badge = new TextView(requireContext());
        badge.setText("⬜ " + piezas);
        badge.setTextSize(10);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_piezas_text));
        badge.setPadding(dp(6), dp(2), dp(6), dp(2));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(requireContext(), R.color.app_piezas_bg));
        bg.setCornerRadius(dp(10));
        badge.setBackground(bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(6), 0, 0, 0);
        badge.setLayoutParams(lp);
        return badge;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Lógica de ciclo de estados
    // ══════════════════════════════════════════════════════════════════════
    private Puzzle.Estados siguienteEstado(Puzzle.Estados actual) {
        if (actual == null) return Puzzle.Estados.Publico;
        switch (actual) {
            case Publico:   return Puzzle.Estados.Privado;
            case Privado:   return Puzzle.Estados.Bloqueado;
            default:        return Puzzle.Estados.Publico;
        }
    }

    private String iconoEstado(Puzzle.Estados estado) {
        if (estado == null) return "❓";
        switch (estado) {
            case Publico:   return "✅";
            case Privado:   return "🔒";
            case Bloqueado: return "🚫";
            default:        return "❓";
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Estado vacío
    // ══════════════════════════════════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    private void mostrarVacio() {
        contenedor.removeAllViews();

        LinearLayout wrap = new LinearLayout(requireContext());
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setGravity(Gravity.CENTER);
        wrap.setPadding(dp(32), dp(48), dp(32), dp(48));
        wrap.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView emoji = new TextView(requireContext());
        emoji.setText("🧩");
        emoji.setTextSize(48);
        emoji.setGravity(Gravity.CENTER);
        wrap.addView(emoji);

        TextView msg = new TextView(requireContext());
        msg.setText("No hay puzzles que mostrar");
        msg.setTextSize(15);
        msg.setTypeface(null, Typeface.BOLD);
        msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, dp(12), 0, 0);
        wrap.addView(msg);

        TextView hint = new TextView(requireContext());
        hint.setText("Prueba a cambiar el filtro o la búsqueda");
        hint.setTextSize(13);
        hint.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(4), 0, 0);
        wrap.addView(hint);

        contenedor.addView(wrap);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Utilidades
    // ══════════════════════════════════════════════════════════════════════
    private void espacio(LinearLayout layout, int px) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px));
        layout.addView(v);
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }
}