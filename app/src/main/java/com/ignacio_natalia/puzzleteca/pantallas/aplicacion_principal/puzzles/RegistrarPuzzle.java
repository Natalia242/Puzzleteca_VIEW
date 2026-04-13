package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class RegistrarPuzzle extends Fragment {

    private PuzzleViewModel viewModel;

    private EditText titulo, autor, tiempo, piezas, descripcion;
    private Spinner dificultadSpinner, estadoSpinner;
    private Switch colorSwitch;
    private ImageView imagenPreview;
    private String imagenBase64;

    private ActivityResultLauncher<Intent> launcher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {

                        Uri uri = result.getData().getData();
                        imagenPreview.setImageURI(uri);

                        imagenBase64 = viewModel.convertirImagenBase64(
                                uri,
                                requireContext().getContentResolver()
                        );
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        titulo = new EditText(getContext());
        titulo.setHint("Título");

        autor = new EditText(getContext());
        autor.setHint("Autor");

        tiempo = new EditText(getContext());
        tiempo.setHint("Tiempo");

        piezas = new EditText(getContext());
        piezas.setHint("Piezas");

        descripcion = new EditText(getContext());
        descripcion.setHint("Descripción");

        dificultadSpinner = new Spinner(getContext());
        dificultadSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Facil", "Media", "Dificil", "Extremo"}));

        estadoSpinner = new Spinner(getContext());
        estadoSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"Publico", "Privado", "Bloqueado"}));

        colorSwitch = new Switch(getContext());
        colorSwitch.setText("Color");

        imagenPreview = new ImageView(getContext());

        Button btnImagen = new Button(getContext());
        btnImagen.setText("Seleccionar Imagen");

        btnImagen.setOnClickListener(v -> abrirGaleria());

        Button btnCrear = new Button(getContext());
        btnCrear.setText("Crear Puzzle");

        layout.addView(titulo);
        layout.addView(autor);
        layout.addView(tiempo);
        layout.addView(piezas);
        layout.addView(descripcion);
        layout.addView(dificultadSpinner);
        layout.addView(estadoSpinner);
        layout.addView(colorSwitch);
        layout.addView(imagenPreview);
        layout.addView(btnImagen);
        layout.addView(btnCrear);

        btnCrear.setOnClickListener(v -> crearPuzzle());

        // OBSERVERS
        viewModel.getPuzzleCreado().observe(getViewLifecycleOwner(), creado -> {
            if (creado != null && creado) {
                Toast.makeText(getContext(), "Puzzle creado", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
        });

        return layout;
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void crearPuzzle() {
        try {
            Puzzle puzzle = new Puzzle();

            puzzle.setTitulo(titulo.getText().toString());
            puzzle.setAutor(autor.getText().toString());
            puzzle.setTiempo(Integer.parseInt(tiempo.getText().toString()));
            puzzle.setPiezas(Integer.parseInt(piezas.getText().toString()));
            puzzle.setDescripcion(descripcion.getText().toString());
            puzzle.setColor(colorSwitch.isChecked());

            puzzle.setDificultad(Puzzle.Dificultades.valueOf(
                    dificultadSpinner.getSelectedItem().toString()));

            puzzle.setEstado(Puzzle.Estados.valueOf(
                    estadoSpinner.getSelectedItem().toString()));

            puzzle.setIdUsuario(GestorSesion.obtenerId_usuario(getContext()));

            // 🔥 AQUÍ METES LA IMAGEN
            puzzle.setImagenBase64(imagenBase64);

            viewModel.crearPuzzle(puzzle);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error en datos", Toast.LENGTH_SHORT).show();
        }
    }
}