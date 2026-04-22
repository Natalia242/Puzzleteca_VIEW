package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;

import java.util.List;

public class GestionPuzzles extends Fragment {

    private GestionPuzzlesViewModel viewModel;
    private LinearLayout contenedor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // ── TÍTULO ──
        TextView titulo = new TextView(requireContext());
        titulo.setText("🧩 Gestión de Puzzles");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#37474F"));
        titulo.setGravity(Gravity.CENTER);
        layout.addView(titulo);

        espacio(layout, 30);

        // ── CONTENEDOR ──
        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        layout.addView(contenedor);

        // ── VIEWMODEL ──
        viewModel = new ViewModelProvider(this).get(GestionPuzzlesViewModel.class);

        viewModel.getPuzzles().observe(getViewLifecycleOwner(), this::renderizarPuzzles);

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
        );

        viewModel.getVacio().observe(getViewLifecycleOwner(), esVacio -> {
            if (Boolean.TRUE.equals(esVacio)) {
                mostrarVacio();
            }
        });

        viewModel.cargarPuzzles("TOKEN_AQUI");

        scroll.addView(layout);
        return scroll;
    }

    // ─────────────────────────────────────────────
    // RENDER LISTA
    // ─────────────────────────────────────────────
    private void renderizarPuzzles(List<Puzzle> lista) {

        contenedor.removeAllViews();

        if (lista == null || lista.isEmpty()) {
            mostrarVacio();
            return;
        }

        for (Puzzle p : lista) {
            contenedor.addView(crearTarjetaPuzzle(p));
            espacio(contenedor, 16);
        }
    }

    // ─────────────────────────────────────────────
    // TARJETA PUZZLE
    // ─────────────────────────────────────────────
    private View crearTarjetaPuzzle(Puzzle puzzle) {

        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(40, 30, 40, 30);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(40);
        fondo.setStroke(2, Color.parseColor("#A5D6A7"));
        card.setBackground(fondo);

        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // ── INFO ──
        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView titulo = new TextView(requireContext());
        titulo.setText("🧩 " + puzzle.getTitulo() + puzzle.getId());
        titulo.setTextSize(15);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#37474F"));

        TextView autor = new TextView(requireContext());
        autor.setText("👤 " + puzzle.getAutor());
        autor.setTextSize(13);
        autor.setTextColor(Color.parseColor("#78909C"));

        TextView estado = new TextView(requireContext());
        estado.setTextSize(13);

        info.addView(titulo);
        info.addView(autor);
        info.addView(estado);

        // ── SPINNER ──
        Spinner spinnerEstado = new Spinner(requireContext());

        Puzzle.Estados[] estados = Puzzle.Estados.values();

        ArrayAdapter<Puzzle.Estados> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                estados
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);

        // 🔥 IMPORTANTE: cargar valor desde BD
        spinnerEstado.setSelection(puzzle.getEstado().ordinal());

        actualizarEstadoPuzzleUI(estado, puzzle.getEstado().name());

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            boolean inicializado = false;

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!inicializado) {
                    inicializado = true;
                    return;
                }

                Puzzle.Estados seleccionado = estados[position];

                puzzle.setEstado(seleccionado);

                actualizarEstadoPuzzleUI(estado, seleccionado.name());

                viewModel.cambiarEstado(
                        puzzle.getIdUsuario(),
                        puzzle.getId(),
                        seleccionado.name()
                );
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        card.addView(info);
        card.addView(spinnerEstado);

        return card;
    }
    private void actualizarEstadoPuzzleUI(TextView estado, String estadoStr) {

        estado.setText(estadoStr);

        switch (estadoStr) {
            case "Publico":
                estado.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "Privado":
                estado.setTextColor(Color.parseColor("#F9A825"));
                break;
            case "Bloqueado":
                estado.setTextColor(Color.RED);
                break;
        }
    }

    // ─────────────────────────────────────────────
    // EMPTY STATE
    // ─────────────────────────────────────────────
    private void mostrarVacio() {

        contenedor.removeAllViews();

        TextView msg = new TextView(requireContext());
        msg.setText("No hay puzzles disponibles");
        msg.setTextSize(14);
        msg.setTextColor(Color.GRAY);
        msg.setGravity(Gravity.CENTER);

        contenedor.addView(msg);
    }

    // ─────────────────────────────────────────────
    private void espacio(LinearLayout layout, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(v);
    }
}