package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.foro;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Comentario;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class Foro extends Fragment {

    private ForoViewModel viewModel;
    private LinearLayout contenedor;

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(ForoViewModel.class);

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        scroll.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity instanceof AppPrincipal) {

                boolean haciaAbajo = scrollY > oldScrollY;

                if (haciaAbajo) {
                    ((AppPrincipal) activity).ocultarBarra();
                } else {
                    ((AppPrincipal) activity).mostrarBarra();
                }
            }
        });

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(16), dp(16), dp(16), dp(16));

        scroll.addView(contenedor);

        viewModel.getPuzzles().observe(getViewLifecycleOwner(), this::mostrarPuzzles);
        viewModel.getError().observe(getViewLifecycleOwner(), this::mostrarError);

        String token = obtenerToken();
        viewModel.cargarPuzzles(token);

        return scroll;
    }

    private void mostrarPuzzles(List<Puzzle> puzzles) {
        contenedor.removeAllViews();
        for (Puzzle puzzle : puzzles) {
            contenedor.addView(crearTarjeta(puzzle));
        }
    }

    @SuppressLint("SetTextI18n")
    private View crearTarjeta(Puzzle puzzle) {

        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.WHITE);
        fondo.setCornerRadius(dp(20));
        tarjeta.setBackground(fondo);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, dp(16));
        tarjeta.setLayoutParams(params);

        ImageView imagen = new ImageView(requireContext());
        imagen.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(200)
        ));
        imagen.setScaleType(ImageView.ScaleType.CENTER_CROP);

        Bitmap bitmap = puzzle.getBitmap();
        if (bitmap != null) {
            imagen.setImageBitmap(bitmap);
        } else {
            imagen.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        LinearLayout cuerpo = new LinearLayout(requireContext());
        cuerpo.setOrientation(LinearLayout.VERTICAL);
        cuerpo.setPadding(dp(16), dp(12), dp(16), dp(12));

        TextView titulo = new TextView(requireContext());
        titulo.setText(puzzle.getTitulo());
        titulo.setTextSize(18);
        titulo.setTypeface(null, Typeface.BOLD);

        TextView autor = new TextView(requireContext());
        autor.setText("Autor: " + puzzle.getAutor());

        LinearLayout footer = new LinearLayout(requireContext());
        footer.setGravity(Gravity.END);

        footer.addView(crearBotonLikes(puzzle));

        cuerpo.addView(titulo);
        cuerpo.addView(autor);
        cuerpo.addView(footer);

        tarjeta.addView(imagen);
        tarjeta.addView(cuerpo);

        // ===================== COMENTARIOS =====================
        LinearLayout seccionComentarios = new LinearLayout(requireContext());
        seccionComentarios.setOrientation(LinearLayout.VERTICAL);
        seccionComentarios.setPadding(dp(16), dp(8), dp(16), dp(12));

        TextView tituloComentarios = new TextView(requireContext());
        tituloComentarios.setText("Comentarios");
        tituloComentarios.setTypeface(null, Typeface.BOLD);

        LinearLayout listaComentarios = new LinearLayout(requireContext());
        listaComentarios.setOrientation(LinearLayout.VERTICAL);


        EditText input = new EditText(requireContext());
        input.setHint("Escribe un comentario...");

        Button btn = new Button(requireContext());
        btn.setText("Enviar");

        String token = obtenerToken();

        // cargar comentarios de ESTE puzzle
        viewModel.cargarComentarios(token, puzzle.getId_puzzle());

        viewModel.getComentariosPorPuzzle(puzzle.getId_puzzle())
                .observe(getViewLifecycleOwner(), comentarios -> {

                    listaComentarios.removeAllViews();

                    if (comentarios != null) {
                        for (Comentario c : comentarios) {

                            TextView txt = new TextView(requireContext());
                            txt.setText(c.getContenido());
                            txt.setPadding(0, dp(4), 0, dp(4));

                            listaComentarios.addView(txt);
                        }
                    }
                });

        btn.setOnClickListener(v -> {

            String texto = input.getText().toString().trim();

            if (!texto.isEmpty()) {

                Comentario comentario = new Comentario();
                comentario.setContenido(texto);
                comentario.setId_puzzle(puzzle.getId_puzzle());

                viewModel.crearComentario(comentario, token);

                input.setText("");
            }
        });

        seccionComentarios.addView(tituloComentarios);
        seccionComentarios.addView(listaComentarios);
        seccionComentarios.addView(input);
        seccionComentarios.addView(btn);

        tarjeta.addView(seccionComentarios);

        return tarjeta;
    }

    private LinearLayout crearBotonLikes(Puzzle puzzle) {

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView icon = new ImageView(requireContext());
        TextView txt = new TextView(requireContext());

        int likes = puzzle.getValoracion() != null ? puzzle.getValoracion() : 0;

        final boolean[] liked = {false};
        final int[] count = {likes};

        actualizarUI(icon, txt, liked[0], count[0]);

        layout.setOnClickListener(v -> {
            liked[0] = !liked[0];
            count[0] += liked[0] ? 1 : -1;
            actualizarUI(icon, txt, liked[0], count[0]);
        });

        layout.addView(icon);
        layout.addView(txt);

        return layout;
    }

    private void actualizarUI(ImageView icon, TextView txt, boolean liked, int count) {
        icon.setImageResource(liked ?
                R.drawable.ic_like_filled :
                R.drawable.ic_like_outline);

        txt.setText(String.valueOf(count));
    }

    private void mostrarError(String msg) {
        contenedor.removeAllViews();

        TextView error = new TextView(requireContext());
        error.setText(msg);
        error.setTextColor(Color.RED);
        error.setGravity(Gravity.CENTER);

        contenedor.addView(error);
    }

    private int dp(int v) {
        return (int) (v * requireContext().getResources().getDisplayMetrics().density);
    }

    private String obtenerToken() {
        return GestorSesion.obtenerToken(requireContext());
    }
}