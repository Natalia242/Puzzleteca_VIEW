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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;

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

        TextView titulo = new TextView(requireContext());
        titulo.setText("🧩 Gestión de Puzzles");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        titulo.setGravity(Gravity.CENTER);
        layout.addView(titulo);

        espacio(layout, 30);

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        layout.addView(contenedor);

        viewModel = new ViewModelProvider(this).get(GestionPuzzlesViewModel.class);
        viewModel.getPuzzles().observe(getViewLifecycleOwner(), this::renderizarPuzzles);
        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());
        viewModel.getVacio().observe(getViewLifecycleOwner(), esVacio -> {
            if (Boolean.TRUE.equals(esVacio)) mostrarVacio();
        });
        viewModel.cargarPuzzles("TOKEN_AQUI");

        scroll.addView(layout);
        return scroll;
    }

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

    private View crearTarjetaPuzzle(Puzzle puzzle) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(40, 30, 40, 30);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        fondo.setCornerRadius(40);
        fondo.setStroke(2, ContextCompat.getColor(requireContext(), R.color.app_green_border));
        card.setBackground(fondo);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvTitulo = new TextView(requireContext());
        tvTitulo.setText("🧩 " + puzzle.getTitulo() + puzzle.getId());
        tvTitulo.setTextSize(15);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));

        TextView autor = new TextView(requireContext());
        autor.setText("👤 " + puzzle.getAutor());
        autor.setTextSize(13);
        autor.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));

        TextView estado = new TextView(requireContext());
        estado.setTextSize(13);

        info.addView(tvTitulo);
        info.addView(autor);
        info.addView(estado);

        Spinner spinnerEstado = new Spinner(requireContext());
        Puzzle.Estados[] estados = Puzzle.Estados.values();
        ArrayAdapter<Puzzle.Estados> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, estados);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEstado.setAdapter(adapter);
        spinnerEstado.setSelection(puzzle.getEstado().ordinal());
        actualizarEstadoPuzzleUI(estado, puzzle.getEstado().name());

        spinnerEstado.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean inicializado = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!inicializado) { inicializado = true; return; }
                Puzzle.Estados seleccionado = estados[position];
                puzzle.setEstado(seleccionado);
                actualizarEstadoPuzzleUI(estado, seleccionado.name());
                viewModel.cambiarEstado(puzzle.getIdUsuario(), puzzle.getId(), seleccionado.name());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        card.addView(info);
        card.addView(spinnerEstado);
        return card;
    }

    private void actualizarEstadoPuzzleUI(TextView estado, String estadoStr) {
        estado.setText(estadoStr);
        int color;
        switch (estadoStr) {
            case "Publico":
                color = ContextCompat.getColor(requireContext(), R.color.app_green_success_text);
                break;
            case "Privado":
                color = ContextCompat.getColor(requireContext(), R.color.app_estado_pendiente);
                break;
            default:
                color = ContextCompat.getColor(requireContext(), R.color.app_peligro);
                break;
        }
        estado.setTextColor(color);
    }

    private void mostrarVacio() {
        contenedor.removeAllViews();
        TextView msg = new TextView(requireContext());
        msg.setText("No hay puzzles disponibles");
        msg.setTextSize(14);
        msg.setTextColor(Color.GRAY);
        msg.setGravity(Gravity.CENTER);
        contenedor.addView(msg);
    }

    private void espacio(LinearLayout layout, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(v);
    }
}