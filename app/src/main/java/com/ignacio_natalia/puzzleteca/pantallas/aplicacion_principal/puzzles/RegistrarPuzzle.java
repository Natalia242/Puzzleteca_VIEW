package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Pantalla de registro de puzzles.
 * <p>
 * CAMBIO: ya no convierte la imagen a base64. En su lugar guarda el archivo
 * en caché y lo envía como multipart al backend, que lo procesa con
 * ImagenService (redimensiona + comprime) y guarda en disco.
 */
public class RegistrarPuzzle extends Fragment {

    private PuzzleViewModel viewModel;

    private EditText titulo, autor, tiempo, piezas, descripcion;
    private Slider dificultadSlider;
    private TextView tvDifValor;
    private boolean dificultadAutomatica = true;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch colorSwitch, estadoSwitch;
    private ImageView imagenPreview;

    /**
     * Archivo temporal de la imagen seleccionada (null si no hay imagen).
     */
    private File imagenSeleccionada = null;
    private ActivityResultLauncher<Intent> launcher;

    private static final int
            COLOR_CARD = Color.WHITE,
            COLOR_ACENTO = Color.parseColor("#F06292"),
            COLOR_BORDE = Color.parseColor("#A5D6A7"),
            COLOR_TEXTO = Color.parseColor("#263238"),
            COLOR_TEXTO_HINT = Color.parseColor("#90A4AE"),
            COLOR_SUBTITULO = Color.parseColor("#546E7A"),

    COLOR_FACIL = Color.parseColor("#7BCF9E"),
            COLOR_MEDIA = Color.parseColor("#F2C66D"),
            COLOR_DIFICIL = Color.parseColor("#F6A06A"),
            COLOR_EXTREMO = Color.parseColor("#E57373");

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(PuzzleViewModel.class);

        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK
                            && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri == null) return;

                        // Copiar al caché para enviarlo como File en multipart
                        File f = uriAFile(uri);
                        if (f != null) {
                            imagenSeleccionada = f;
                            imagenPreview.setImageURI(uri);
                            imagenPreview.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(requireContext(),
                                    "No se pudo cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(40));

        // Titulo pantalla
        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText("Nuevo Puzzle");
        tvTitulo.setTextSize(26);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(COLOR_TEXTO);
        tvTitulo.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTitulo.setPadding(0, 0, 0, dp(4));

        TextView tvSubtitulo = new TextView(getContext());
        tvSubtitulo.setText("Completa los datos de tu puzzle");
        tvSubtitulo.setTextSize(14);
        tvSubtitulo.setTextColor(COLOR_SUBTITULO);
        tvSubtitulo.setGravity(Gravity.CENTER_HORIZONTAL);
        tvSubtitulo.setPadding(0, 0, 0, dp(20));

        root.addView(tvTitulo);
        root.addView(tvSubtitulo);

        // Sección: Información básica
        root.addView(crearTituloSeccion("Informacion basica"));
        LinearLayout cardBasica = crearCard();
        titulo = crearCampo("Titulo del puzzle");
        autor  = crearCampo("Autor");
        cardBasica.addView(titulo);
        cardBasica.addView(crearSeparador());
        cardBasica.addView(autor);
        root.addView(cardBasica);
        root.addView(crearEspacio(dp(14)));

        // Sección: Detalles
        root.addView(crearTituloSeccion("Detalles"));
        LinearLayout cardDetalles = crearCard();
        tiempo      = crearCampoNumero("Tiempo (Horas)");
        piezas      = crearCampoNumero("Numero de piezas");
        descripcion = crearCampoMultilinea("Descripcion");

        LinearLayout filaDificultad = new LinearLayout(getContext());
        filaDificultad.setOrientation(LinearLayout.HORIZONTAL);
        filaDificultad.setGravity(Gravity.CENTER_VERTICAL);
        filaDificultad.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        TextView tvDifLabel = crearLabel("Dificultad");
        tvDifLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
        ));

        CheckBox autoCheck = new CheckBox(getContext());
        autoCheck.setChecked(true); // activado por defecto

        autoCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dificultadAutomatica = isChecked;
            dificultadSlider.setEnabled(!isChecked);

            if (isChecked) {
                recalcularDificultad();
            }
        });

        filaDificultad.addView(tvDifLabel);
        filaDificultad.addView(autoCheck);

        dificultadSlider = new Slider(getContext());
        dificultadSlider.setValueFrom(0);
        dificultadSlider.setValueTo(3);
        dificultadSlider.setStepSize(1);
        dificultadSlider.setValue(0);

        dificultadAutomatica = true;
        dificultadSlider.setEnabled(false);

        dificultadSlider.setTrackInactiveTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        dificultadSlider.setTrackActiveTintList(ColorStateList.valueOf(COLOR_FACIL));
        dificultadSlider.setThumbTintList(ColorStateList.valueOf(COLOR_FACIL));
        dificultadSlider.setLabelBehavior(LabelFormatter.LABEL_GONE);

        tvDifValor = new TextView(getContext());
        tvDifValor.setText("Fácil");
        tvDifValor.setTextColor(COLOR_SUBTITULO);

        dificultadSlider.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                dificultadAutomatica = false;
                autoCheck.setChecked(false);
                dificultadSlider.setEnabled(true);
            }
            actualizarTextoYColor((int) value);
        });

        piezas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) return;
                recalcularDificultad();
            }
        });

        cardDetalles.addView(tiempo);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(piezas);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(descripcion);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(filaDificultad);
        cardDetalles.addView(dificultadSlider);
        cardDetalles.addView(tvDifValor);

        root.addView(cardDetalles);
        root.addView(crearEspacio(dp(14)));

        // Sección: Opciones
        root.addView(crearTituloSeccion("Opciones"));
        LinearLayout cardOpciones = crearCard();

        // Switch COLOR
        LinearLayout filaColor = crearFilaSwitch();
        LinearLayout textoColor = new LinearLayout(getContext());
        textoColor.setOrientation(LinearLayout.VERTICAL);
        textoColor.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvColorT = new TextView(getContext());
        tvColorT.setText("Color del puzzle");
        tvColorT.setTextSize(16);
        tvColorT.setTextColor(COLOR_TEXTO);
        tvColorT.setTypeface(null, Typeface.BOLD);
        TextView tvColorD = new TextView(getContext());
        tvColorD.setText("El puzzle tiene más de un único color");
        tvColorD.setTextSize(12);
        tvColorD.setTextColor(COLOR_SUBTITULO);
        textoColor.addView(tvColorT);
        textoColor.addView(tvColorD);
        colorSwitch = new Switch(getContext());
        colorSwitch.setChecked(true);

        colorSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            recalcularDificultad();
        });

        filaColor.addView(textoColor);
        filaColor.addView(colorSwitch);
        cardOpciones.addView(filaColor);
        cardOpciones.addView(crearSeparador());

        // Switch ESTADO
        LinearLayout filaEstado = crearFilaSwitch();
        LinearLayout contenidoEstado = new LinearLayout(getContext());
        contenidoEstado.setOrientation(LinearLayout.HORIZONTAL);
        contenidoEstado.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        contenidoEstado.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout textoEstado = new LinearLayout(getContext());
        textoEstado.setOrientation(LinearLayout.VERTICAL);
        textoEstado.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        TextView tvEstadoT = new TextView(getContext());
        tvEstadoT.setText("Visibilidad");
        tvEstadoT.setTextSize(16);
        tvEstadoT.setTextColor(COLOR_TEXTO);
        tvEstadoT.setTypeface(null, Typeface.BOLD);
        TextView tvEstadoD = new TextView(getContext());
        tvEstadoD.setText("Público");
        tvEstadoD.setTextSize(12);
        tvEstadoD.setTextColor(COLOR_SUBTITULO);
        ImageView iconoEstado = new ImageView(getContext());
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(24), dp(24));
        iconParams.setMargins(dp(8), 0, 0, 0);
        iconoEstado.setLayoutParams(iconParams);
        iconoEstado.setImageResource(R.drawable.open_lock);
        textoEstado.addView(tvEstadoT);
        textoEstado.addView(tvEstadoD);
        contenidoEstado.addView(textoEstado);
        contenidoEstado.addView(iconoEstado);
        estadoSwitch = new Switch(getContext());
        estadoSwitch.setChecked(true);
        estadoSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            tvEstadoD.setText(isChecked ? "Público" : "Privado");
            iconoEstado.setImageResource(isChecked ? R.drawable.open_lock : R.drawable.lock);
        });
        filaEstado.addView(contenidoEstado);
        filaEstado.addView(estadoSwitch);
        cardOpciones.addView(filaEstado);
        root.addView(cardOpciones);
        root.addView(crearEspacio(dp(14)));

        // Sección: Imagen
        root.addView(crearTituloSeccion("Imagen"));
        LinearLayout cardImagen = crearCard();
        cardImagen.setGravity(Gravity.CENTER_HORIZONTAL);

        imagenPreview = new ImageView(getContext());
        imagenPreview.setVisibility(View.GONE);
        LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(200));
        imgParams.setMargins(0, 0, 0, dp(12));
        imagenPreview.setLayoutParams(imgParams);
        imagenPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
        GradientDrawable imgBg = new GradientDrawable();
        imgBg.setCornerRadius(dp(12));
        imgBg.setStroke(dp(1), COLOR_BORDE);
        imgBg.setColor(Color.parseColor("#F5F5F5"));
        imagenPreview.setBackground(imgBg);

        Button btnImagen = crearBotonSecundario("Seleccionar imagen");
        btnImagen.setOnClickListener(v -> abrirGaleria());

        cardImagen.addView(imagenPreview);
        cardImagen.addView(btnImagen);
        root.addView(cardImagen);
        root.addView(crearEspacio(dp(28)));

        // Boton crear
        Button btnCrear = crearBotonPrincipal("Crear Puzzle");
        btnCrear.setOnClickListener(v -> crearPuzzle());
        root.addView(btnCrear);

        scroll.addView(root);

        viewModel.getPuzzleCreado().observe(getViewLifecycleOwner(), creado -> {
            if (creado != null && creado) {
                Toast.makeText(getContext(), "Puzzle creado con éxito!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), e ->
                Toast.makeText(getContext(), e, Toast.LENGTH_SHORT).show());

        return scroll;
    }

    @Override
    public void onResume() {
        super.onResume();

        AppPrincipal activity = (AppPrincipal) requireActivity();
        activity.ocultarTitulo();
    }

    @Override
    public void onPause() {
        super.onPause();

        AppPrincipal activity = (AppPrincipal) requireActivity();
        activity.mostrarTitulo();
    }

    private int calcularDificultad(int piezas, boolean color) {

        int dificultad;

        if (piezas <= 100) dificultad = 0;
        else if (piezas <= 1000) dificultad = 1;
        else if (piezas < 10000) dificultad = 2;
        else dificultad = 3;

        if (!color && dificultad < 3) dificultad++;

        return dificultad;

    }

    private void recalcularDificultad() {
        if (dificultadAutomatica && !piezas.getText().toString().isEmpty()) {
            try {
                dificultadSlider.setValue(
                        calcularDificultad(Integer.parseInt(piezas.getText().toString()), colorSwitch.isChecked())
                );
            } catch (Exception ignored) {}
        }
    }

    @SuppressLint("SetTextI18n")
    private void actualizarTextoYColor(int dificultad) {

        int color;

        switch (dificultad) {
            case 1:
                tvDifValor.setText("Media");
                color = COLOR_MEDIA;
                break;
            case 2:
                tvDifValor.setText("Dificil");
                color = COLOR_DIFICIL;
                break;
            case 3:
                tvDifValor.setText("Extremo");
                color = COLOR_EXTREMO;
                break;
            default:
                tvDifValor.setText("Facil");
                color = COLOR_FACIL;
                break;
        }

        // 🔥 track activo
        dificultadSlider.setTrackActiveTintList(ColorStateList.valueOf(color));

        // 🎯 thumb (punto)
        dificultadSlider.setThumbTintList(ColorStateList.valueOf(color));

    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }


    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        launcher.launch(intent);
    }

    private void crearPuzzle() {
        try {
            Puzzle puzzle = new Puzzle();
            puzzle.setTitulo(titulo.getText().toString().trim());
            puzzle.setAutor(autor.getText().toString().trim());
            puzzle.setTiempo(Integer.parseInt(tiempo.getText().toString().trim()));
            puzzle.setPiezas(Integer.parseInt(piezas.getText().toString().trim()));
            puzzle.setDescripcion(descripcion.getText().toString().trim());
            puzzle.setColor(colorSwitch.isChecked());
            puzzle.setEstado(estadoSwitch.isChecked() ? Puzzle.Estados.Publico : Puzzle.Estados.Privado);

            Puzzle.Dificultades dificultad;

            switch ((int) dificultadSlider.getValue()) {
                case 1:
                    dificultad = Puzzle.Dificultades.Media;
                    break;
                case 2:
                    dificultad = Puzzle.Dificultades.Dificil;
                    break;
                case 3:
                    dificultad = Puzzle.Dificultades.Extremo;
                    break;
                default:
                    dificultad = Puzzle.Dificultades.Facil;
                    break;
            }

            puzzle.setDificultad(dificultad);

            puzzle.setIdUsuario(GestorSesion.obtenerId_usuario(getContext()));

            String token = GestorSesion.obtenerToken(getContext());
            // Pasamos el File (puede ser null si no se seleccionó imagen)
            viewModel.crearPuzzle(token, puzzle, imagenSeleccionada);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Revisa que tiempo y piezas sean números",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al rellenar los datos",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /** Copia un Uri de contenido a un File temporal en caché */
    private File uriAFile(Uri uri) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            if (is == null) return null;
            File temp = File.createTempFile("puzzle_img_", ".jpg",
                    requireContext().getCacheDir());
            FileOutputStream fos = new FileOutputStream(temp);
            byte[] buf = new byte[4096];
            int n;
            while ((n = is.read(buf)) != -1) fos.write(buf, 0, n);
            is.close();
            fos.close();
            return temp;
        } catch (Exception e) {
            return null;
        }
    }

    // --- Helpers de UI ---

    private LinearLayout crearCard() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(12), dp(16), dp(12));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_CARD);
        bg.setCornerRadius(dp(16));
        bg.setStroke(dp(1), COLOR_BORDE);
        card.setBackground(bg);
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private EditText crearCampo(String hint) {
        EditText et = new EditText(getContext());
        et.setHint(hint);
        et.setHintTextColor(COLOR_TEXTO_HINT);
        et.setTextColor(COLOR_TEXTO);
        et.setTextSize(15);
        et.setBackground(null);
        et.setPadding(dp(4), dp(10), dp(4), dp(10));
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return et;
    }

    private EditText crearCampoNumero(String hint) {
        EditText et = crearCampo(hint);
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        return et;
    }

    private EditText crearCampoMultilinea(String hint) {
        EditText et = crearCampo(hint);
        et.setMinLines(2);
        et.setMaxLines(4);
        et.setGravity(Gravity.TOP | Gravity.START);
        return et;
    }

    private TextView crearLabel(String texto) {
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setTextSize(13);
        tv.setTextColor(COLOR_SUBTITULO);
        tv.setPadding(dp(4), dp(10), dp(4), 0);
        return tv;
    }

    private TextView crearTituloSeccion(String texto) {
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setTextSize(15);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(COLOR_SUBTITULO);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(dp(4), 0, 0, dp(6));
        tv.setLayoutParams(p);
        return tv;
    }

    private View crearSeparador() {
        View v = new View(getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        p.setMargins(dp(4), dp(4), dp(4), dp(4));
        v.setLayoutParams(p);
        v.setBackgroundColor(Color.parseColor("#E8F5E9"));
        return v;
    }

    private View crearEspacio(int height) {
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        return v;
    }

    private LinearLayout crearFilaSwitch() {
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(4), dp(10), dp(4), dp(10));
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return fila;
    }

    private Button crearBotonPrincipal(String texto) {
        Button btn = new Button(getContext());
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(17);
        btn.setAllCaps(false);
        btn.setPadding(dp(20), dp(16), dp(20), dp(16));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_ACENTO);
        bg.setCornerRadius(dp(30));
        btn.setBackground(bg);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return btn;
    }

    private Button crearBotonSecundario(String texto) {
        Button btn = new Button(getContext());
        btn.setText(texto);
        btn.setTextColor(COLOR_ACENTO);
        btn.setTextSize(15);
        btn.setAllCaps(false);
        btn.setPadding(dp(20), dp(12), dp(20), dp(12));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(2), COLOR_ACENTO);
        btn.setBackground(bg);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return btn;
    }

}