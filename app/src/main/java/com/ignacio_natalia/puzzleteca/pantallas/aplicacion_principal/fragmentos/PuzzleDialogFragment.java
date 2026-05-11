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

import com.bumptech.glide.Glide;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.repositorios.RankingRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PuzzleDialogFragment extends DialogFragment {

    private static final String ARG_PUZZLE = "puzzle";

    private Puzzle puzzle;
    private RankingRepositorio rankingRepositorio;

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

        rankingRepositorio = new RankingRepositorio();

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(20));

        // ── Imagen ────────────────────────────────────────────────────────
        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dp(200)));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

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

        // ── Textos ────────────────────────────────────────────────────────
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

        // ── RatingBar ─────────────────────────────────────────────────────
        LinearLayout.LayoutParams rbParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rbParams.topMargin = dp(12);

        RatingBar ratingBar = new RatingBar(requireContext());
        ratingBar.setLayoutParams(rbParams);
        ratingBar.setNumStars(5);
        ratingBar.setStepSize(1f);
        ratingBar.setRating(0);

        // Etiqueta de estado de la valoración
        TextView tvEstadoValoracion = new TextView(requireContext());
        tvEstadoValoracion.setText("Toca las estrellas para valorar");
        tvEstadoValoracion.setTextSize(12);
        tvEstadoValoracion.setPadding(0, dp(4), 0, 0);

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if (!fromUser) return;

            int stars      = (int) rating;
            String token   = GestorSesion.obtenerToken(requireContext());
            int    idUsuario = GestorSesion.obtenerId_usuario(requireContext());

            // Evitar valorar el propio puzzle
            if (puzzle.getIdUsuario() != null && puzzle.getIdUsuario().equals(idUsuario)) {
                Toast.makeText(requireContext(),
                        "No puedes valorar tu propio puzzle",
                        Toast.LENGTH_SHORT).show();
                ratingBar.setRating(0);
                return;
            }

            tvEstadoValoracion.setText("Enviando valoración…");
            ratingBar.setEnabled(false);

            rankingRepositorio.valorarPuzzle(
                    token,
                    puzzle.getId(),
                    idUsuario,
                    stars,
                    new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call,
                                               @NonNull Response<Void> response) {
                            if (!isAdded()) return;
                            if (response.isSuccessful()) {
                                // 201 → OK
                                puzzle.setValoracion(stars);
                                tvEstadoValoracion.setText("✅ Valorado con " + stars + " ★");
                                Toast.makeText(requireContext(),
                                        "Has valorado con " + stars + " estrellas",
                                        Toast.LENGTH_SHORT).show();
                            } else if (response.code() == 409) {
                                // Ya valorado
                                tvEstadoValoracion.setText("Ya valoraste este puzzle");
                                ratingBar.setEnabled(false);
                                Toast.makeText(requireContext(),
                                        "Ya has valorado este puzzle",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                tvEstadoValoracion.setText("Error al valorar. Inténtalo de nuevo");
                                ratingBar.setEnabled(true);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call,
                                              @NonNull Throwable t) {
                            if (!isAdded()) return;
                            tvEstadoValoracion.setText("Sin conexión. Inténtalo de nuevo");
                            ratingBar.setEnabled(true);
                            Toast.makeText(requireContext(),
                                    "Error de red",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        // ── Botón cerrar ──────────────────────────────────────────────────
        Button cerrar = new Button(requireContext());
        cerrar.setText("Cerrar");
        cerrar.setOnClickListener(v -> dismiss());

        // ── Montaje ───────────────────────────────────────────────────────
        layout.addView(imagen);
        layout.addView(titulo);
        layout.addView(autor);
        layout.addView(descripcion);
        layout.addView(dificultad);
        layout.addView(piezas);
        layout.addView(tiempo);
        layout.addView(ratingBar);
        layout.addView(tvEstadoValoracion);
        layout.addView(cerrar);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }
}