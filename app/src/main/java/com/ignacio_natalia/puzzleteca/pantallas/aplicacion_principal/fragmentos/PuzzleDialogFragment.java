package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;

public class PuzzleDialogFragment extends DialogFragment {

    private static final String ARG_PUZZLE = "puzzle";

    private Puzzle puzzle;

    public static PuzzleDialogFragment newInstance(Puzzle puzzle) {
        PuzzleDialogFragment fragment = new PuzzleDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_PUZZLE, puzzle);
        fragment.setArguments(args);

        return fragment;
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (getArguments() != null) {
            puzzle = (Puzzle) getArguments().getSerializable(ARG_PUZZLE);
        }

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(20));

        // ---------------- IMAGEN ----------------
        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(200)
        ));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

        if (puzzle.getBitmap() != null) {
            imagen.setImageBitmap(puzzle.getBitmap());
        } else {
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        // ---------------- TEXTO ----------------
        TextView titulo = new TextView(requireContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);

        TextView autor = new TextView(requireContext());
        autor.setText("Autor: " + puzzle.getAutor());

        TextView descripcion = new TextView(requireContext());
        descripcion.setText("Descripción: " + puzzle.getDescripcion());

        TextView dificultad = new TextView(requireContext());
        dificultad.setText("Dificultad: " + puzzle.getDificultad());

        TextView piezas = new TextView(requireContext());
        piezas.setText("Piezas: " + puzzle.getPiezas());

        TextView tiempo = new TextView(requireContext());
        tiempo.setText("Tiempo: " + puzzle.getTiempo() + " min");

        // ---------------- RATING ----------------
        RatingBar ratingBar = new RatingBar(requireContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.topMargin = dp(12);

        ratingBar.setLayoutParams(params);

        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        ratingBar.setScaleX(1f);
        ratingBar.setScaleY(1f);

        // Para hacerlo más grande
//        ratingBar.getLayoutParams().height = dp(48);

        ratingBar.setRating(0);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (fromUser) {
                puzzle.setValoracion((int) rating);

                Toast.makeText(requireContext(),
                        "Has valorado con " + (int) rating + " estrellas",
                        Toast.LENGTH_SHORT).show();

                // Conectar aquí con el ViewModel/API
                // viewModel.valorarPuzzle(puzzle.getId(), (int) rating, token);
            }
        });

        // ---------------- BOTÓN CERRAR ----------------
        Button cerrar = new Button(requireContext());
        cerrar.setText("Cerrar");
        cerrar.setOnClickListener(v -> dismiss());

        // ---------------- MONTAJE ----------------
        layout.addView(imagen);
        layout.addView(titulo);
        layout.addView(autor);
        layout.addView(descripcion);
        layout.addView(dificultad);
        layout.addView(piezas);
        layout.addView(tiempo);
        layout.addView(ratingBar);
        layout.addView(cerrar);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }

}