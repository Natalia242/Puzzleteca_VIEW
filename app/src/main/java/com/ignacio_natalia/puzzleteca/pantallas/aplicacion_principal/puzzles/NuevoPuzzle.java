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
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NuevoPuzzle extends Fragment {

    private PuzzleViewModel viewModel;

    private EditText titulo, autor, tiempo, piezas, descripcion;
    private Slider dificultadSlider;
    private TextView tvDifValor;
    private boolean dificultadAutomatica = true;

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch colorSwitch, estadoSwitch;
    private ImageView imagenPreview;

    private File imagenSeleccionada = null;
    private ActivityResultLauncher<Intent> launcher;

    private static final int
            COLOR_CARD       = Color.WHITE,
            COLOR_ACENTO     = Color.parseColor("#E91E8C"),
            COLOR_ACENTO2    = Color.parseColor("#FF6BB5"),
            COLOR_BORDE      = Color.parseColor("#C8E6C9"),
            COLOR_TEXTO      = Color.parseColor("#1A2E1A"),
            COLOR_HINT       = Color.parseColor("#90A4AE"),
            COLOR_SUBTITULO  = Color.parseColor("#546E7A"),
            COLOR_HEADER_INI = Color.parseColor("#FCE4EC"),
            COLOR_HEADER_FIN = Color.parseColor("#E8F5E9"),
            COLOR_FACIL      = Color.parseColor("#66BB6A"),
            COLOR_MEDIA      = Color.parseColor("#FFA726"),
            COLOR_DIFICIL    = Color.parseColor("#EF5350"),
            COLOR_EXTREMO    = Color.parseColor("#B71C1C");

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

        // ── Fondo gradiente (dos instancias separadas) ──
        scroll.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_HEADER_INI, COLOR_HEADER_FIN}
        ));
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{COLOR_HEADER_INI, COLOR_HEADER_FIN}
        ));

        // ═══════════════════════════════════════════════════
        //  HEADER — mismo formato que EditarPuzzle
        // ═══════════════════════════════════════════════════
        FrameLayout header = new FrameLayout(getContext());
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        header.setPadding(dp(24), dp(56), dp(24), dp(32));

        LinearLayout headerContent = new LinearLayout(getContext());
        headerContent.setOrientation(LinearLayout.VERTICAL);
        headerContent.setGravity(Gravity.CENTER_HORIZONTAL);
        headerContent.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));

        // Chip/badge
        TextView tvBadge = new TextView(getContext());
        tvBadge.setText("➕  Nuevo");
        tvBadge.setTextSize(12);
        tvBadge.setTextColor(COLOR_ACENTO);
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setPadding(dp(14), dp(6), dp(14), dp(6));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.WHITE);
        badgeBg.setCornerRadius(dp(20));
        badgeBg.setStroke(dp(1), COLOR_ACENTO2);
        tvBadge.setBackground(badgeBg);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.setMargins(0, 0, 0, dp(14));
        tvBadge.setLayoutParams(badgeParams);

        // Título principal
        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText("Nuevo Puzzle");
        tvTitulo.setTextSize(30);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(COLOR_TEXTO);
        tvTitulo.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.setMargins(0, 0, 0, dp(8));
        tvTitulo.setLayoutParams(tituloParams);

        // Subtítulo
        TextView tvSubtitulo = new TextView(getContext());
        tvSubtitulo.setText("Completa los datos de tu puzzle");
        tvSubtitulo.setTextSize(13);
        tvSubtitulo.setTextColor(COLOR_SUBTITULO);
        tvSubtitulo.setGravity(Gravity.CENTER_HORIZONTAL);

        // Línea decorativa
        View lineaDecorada = new View(getContext());
        LinearLayout.LayoutParams lineaParams = new LinearLayout.LayoutParams(dp(48), dp(4));
        lineaParams.gravity = Gravity.CENTER_HORIZONTAL;
        lineaParams.setMargins(0, dp(16), 0, 0);
        lineaDecorada.setLayoutParams(lineaParams);
        GradientDrawable lineaBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{COLOR_ACENTO, COLOR_ACENTO2});
        lineaBg.setCornerRadius(dp(4));
        lineaDecorada.setBackground(lineaBg);

        headerContent.addView(tvBadge);
        headerContent.addView(tvTitulo);
        headerContent.addView(tvSubtitulo);
        headerContent.addView(lineaDecorada);
        header.addView(headerContent);
        root.addView(header);

        // ── Contenido (transparente, hereda el gradiente) ──
        LinearLayout contenido = new LinearLayout(getContext());
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(20), dp(4), dp(20), dp(40));

        // ── Sección: Información básica ──
        contenido.addView(tituloSeccion("📋  Información básica"));
        LinearLayout cardBasica = crearCard();
        titulo = crearCampo("Título del puzzle");
        autor  = crearCampo("Autor");
        cardBasica.addView(titulo);
        cardBasica.addView(crearSeparador());
        cardBasica.addView(autor);
        contenido.addView(cardBasica);
        contenido.addView(crearEspacio(dp(18)));

        // ── Sección: Detalles ──
        contenido.addView(tituloSeccion("🧩  Detalles"));
        LinearLayout cardDetalles = crearCard();
        tiempo      = crearCampoNumero("Tiempo (horas)");
        piezas      = crearCampoNumero("Número de piezas");
        descripcion = crearCampoMultilinea("Descripción");

        // Fila dificultad
        LinearLayout filaDificultad = new LinearLayout(getContext());
        filaDificultad.setOrientation(LinearLayout.HORIZONTAL);
        filaDificultad.setGravity(Gravity.CENTER_VERTICAL);
        filaDificultad.setPadding(dp(4), dp(12), dp(4), dp(4));
        filaDificultad.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout difTextos = new LinearLayout(getContext());
        difTextos.setOrientation(LinearLayout.VERTICAL);
        difTextos.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvDifLabel = new TextView(getContext());
        tvDifLabel.setText("Dificultad");
        tvDifLabel.setTextSize(15);
        tvDifLabel.setTextColor(COLOR_TEXTO);
        tvDifLabel.setTypeface(null, Typeface.BOLD);

        TextView tvDifAuto = new TextView(getContext());
        tvDifAuto.setText("Automática según piezas");
        tvDifAuto.setTextSize(11);
        tvDifAuto.setTextColor(COLOR_SUBTITULO);

        difTextos.addView(tvDifLabel);
        difTextos.addView(tvDifAuto);

        CheckBox autoCheck = new CheckBox(getContext());
        autoCheck.setChecked(true);

        dificultadSlider = new Slider(getContext());
        dificultadSlider.setValueFrom(0);
        dificultadSlider.setValueTo(3);
        dificultadSlider.setStepSize(1);
        dificultadSlider.setValue(0);
        dificultadSlider.setTrackInactiveTintList(
                ColorStateList.valueOf(Color.parseColor("#ECEFF1")));
        dificultadSlider.setLabelBehavior(LabelFormatter.LABEL_GONE);
        dificultadSlider.setEnabled(false);

        LinearLayout.LayoutParams sliderParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sliderParams.setMargins(0, dp(4), 0, dp(12));
        dificultadSlider.setLayoutParams(sliderParams);

        // Etiquetas del slider
        LinearLayout etiquetasDif = new LinearLayout(getContext());
        etiquetasDif.setOrientation(LinearLayout.HORIZONTAL);
        etiquetasDif.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        String[] niveles = {"Fácil", "Media", "Difícil", "Extremo"};
        int[] coloresNivel = {COLOR_FACIL, COLOR_MEDIA, COLOR_DIFICIL, COLOR_EXTREMO};
        for (int i = 0; i < niveles.length; i++) {
            TextView chip = new TextView(getContext());
            chip.setText(niveles[i]);
            chip.setTextSize(10);
            chip.setTextColor(coloresNivel[i]);
            chip.setGravity(Gravity.CENTER);
            chip.setLayoutParams(new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            etiquetasDif.addView(chip);
        }

        tvDifValor = new TextView(getContext());
        tvDifValor.setTextSize(13);
        tvDifValor.setTypeface(null, Typeface.BOLD);
        tvDifValor.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams difValorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        difValorParams.setMargins(0, dp(15), 0, dp(8));
        tvDifValor.setLayoutParams(difValorParams);
        actualizarTextoYColor(0); // estado inicial: Fácil

        autoCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dificultadAutomatica = isChecked;
            dificultadSlider.setEnabled(!isChecked);
            if (isChecked) recalcularDificultad();
        });

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
                if (!s.toString().isEmpty()) recalcularDificultad();
            }
        });

        filaDificultad.addView(difTextos);
        filaDificultad.addView(autoCheck);

        cardDetalles.addView(tiempo);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(piezas);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(descripcion);
        cardDetalles.addView(crearSeparador());
        cardDetalles.addView(filaDificultad);
        cardDetalles.addView(dificultadSlider);
        cardDetalles.addView(etiquetasDif);
        cardDetalles.addView(tvDifValor);
        contenido.addView(cardDetalles);
        contenido.addView(crearEspacio(dp(18)));

        // ── Sección: Opciones ──
        contenido.addView(tituloSeccion("⚙️  Opciones"));
        LinearLayout cardOpciones = crearCard();

        LinearLayout filaColor = crearFilaSwitch();
        LinearLayout textoColor = textoDoble("🎨  Color del puzzle",
                "El puzzle tiene más de un único color");
        colorSwitch = new Switch(getContext());
        colorSwitch.setChecked(true);
        colorSwitch.setOnCheckedChangeListener((btn, isChecked) -> recalcularDificultad());
        filaColor.addView(textoColor);
        filaColor.addView(colorSwitch);
        cardOpciones.addView(filaColor);
        cardOpciones.addView(crearSeparador());

        LinearLayout filaEstado = crearFilaSwitch();
        LinearLayout contenidoEstado = new LinearLayout(getContext());
        contenidoEstado.setOrientation(LinearLayout.HORIZONTAL);
        contenidoEstado.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        contenidoEstado.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout textoEstado = new LinearLayout(getContext());
        textoEstado.setOrientation(LinearLayout.VERTICAL);
        textoEstado.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvEstadoT = new TextView(getContext());
        tvEstadoT.setText("🔒  Visibilidad");
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
        contenido.addView(cardOpciones);
        contenido.addView(crearEspacio(dp(18)));

        // ── Sección: Imagen ──
        contenido.addView(tituloSeccion("🖼️  Imagen"));
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
        contenido.addView(cardImagen);
        contenido.addView(crearEspacio(dp(32)));

        // ── Botón crear ──
        Button btnCrear = crearBotonPrincipal("Crear Puzzle");
        btnCrear.setOnClickListener(v -> crearPuzzle());
        contenido.addView(btnCrear);

        root.addView(contenido);
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

    // ─────────────────────────────────────────────────────
    //  Lógica de dificultad
    // ─────────────────────────────────────────────────────

    private int calcularDificultad(int piezas, boolean color) {
        int dificultad;
        if (piezas <= 100)       dificultad = 0;
        else if (piezas <= 1000) dificultad = 1;
        else if (piezas < 10000) dificultad = 2;
        else                     dificultad = 3;
        if (!color && dificultad < 3) dificultad++;
        return dificultad;
    }

    private void recalcularDificultad() {
        if (!dificultadAutomatica) return;
        String txt = piezas.getText().toString();
        if (txt.isEmpty()) return;
        try {
            dificultadSlider.setValue(
                    calcularDificultad(Integer.parseInt(txt), colorSwitch.isChecked()));
        } catch (Exception ignored) {}
    }

    @SuppressLint("SetTextI18n")
    private void actualizarTextoYColor(int v) {
        int color;
        String texto;
        String emoji;
        switch (v) {
            case 1: texto = "Media";   color = COLOR_MEDIA;   emoji = "🟡"; break;
            case 2: texto = "Difícil"; color = COLOR_DIFICIL; emoji = "🟠"; break;
            case 3: texto = "Extremo"; color = COLOR_EXTREMO; emoji = "🔴"; break;
            default: texto = "Fácil";  color = COLOR_FACIL;   emoji = "🟢"; break;
        }
        tvDifValor.setText(emoji + "  " + texto);
        tvDifValor.setTextColor(color);
        dificultadSlider.setTrackActiveTintList(ColorStateList.valueOf(color));
        dificultadSlider.setThumbTintList(ColorStateList.valueOf(color));
    }

    // ─────────────────────────────────────────────────────
    //  Helpers de UI
    // ─────────────────────────────────────────────────────

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
            puzzle.setEstado(estadoSwitch.isChecked()
                    ? Puzzle.Estados.Publico : Puzzle.Estados.Privado);

            Puzzle.Dificultades dificultad;
            switch ((int) dificultadSlider.getValue()) {
                case 1:  dificultad = Puzzle.Dificultades.Media;   break;
                case 2:  dificultad = Puzzle.Dificultades.Dificil; break;
                case 3:  dificultad = Puzzle.Dificultades.Extremo; break;
                default: dificultad = Puzzle.Dificultades.Facil;   break;
            }
            puzzle.setDificultad(dificultad);
            puzzle.setIdUsuario(GestorSesion.obtenerId_usuario(getContext()));

            String token = GestorSesion.obtenerToken(getContext());
            viewModel.crearPuzzle(token, puzzle, imagenSeleccionada);

        } catch (NumberFormatException e) {
            Toast.makeText(getContext(),
                    "Revisa que tiempo y piezas sean números", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(),
                    "Error al rellenar los datos", Toast.LENGTH_SHORT).show();
        }
    }

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

    private LinearLayout crearCard() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(14), dp(18), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(COLOR_CARD);
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), COLOR_BORDE);
        card.setBackground(bg);
        card.setElevation(dp(2));
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return card;
    }

    private EditText crearCampo(String hint) {
        EditText et = new EditText(getContext());
        et.setHint(hint);
        et.setHintTextColor(COLOR_HINT);
        et.setTextColor(COLOR_TEXTO);
        et.setTextSize(15);
        et.setBackground(null);
        et.setPadding(dp(4), dp(12), dp(4), dp(12));
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

    private TextView tituloSeccion(String texto) {
        TextView tv = new TextView(getContext());
        tv.setText(texto);
        tv.setTextSize(13);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(COLOR_SUBTITULO);
        tv.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(dp(4), 0, 0, dp(8));
        tv.setLayoutParams(p);
        return tv;
    }

    private View crearSeparador() {
        View v = new View(getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        p.setMargins(0, dp(2), 0, dp(2));
        v.setLayoutParams(p);
        v.setBackgroundColor(Color.parseColor("#E8F5E9"));
        v.setAlpha(0.45f);
        return v;
    }

    private View crearEspacio(int height) {
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height));
        return v;
    }

    private LinearLayout crearFilaSwitch() {
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(4), dp(12), dp(4), dp(12));
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return fila;
    }

    private LinearLayout textoDoble(String titulo, String desc) {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvT = new TextView(getContext());
        tvT.setText(titulo);
        tvT.setTextSize(16);
        tvT.setTextColor(COLOR_TEXTO);
        tvT.setTypeface(null, Typeface.BOLD);
        TextView tvD = new TextView(getContext());
        tvD.setText(desc);
        tvD.setTextSize(12);
        tvD.setTextColor(COLOR_SUBTITULO);
        ll.addView(tvT);
        ll.addView(tvD);
        return ll;
    }

    private Button crearBotonPrincipal(String texto) {
        Button btn = new Button(getContext());
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(16);
        btn.setAllCaps(false);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setPadding(dp(20), dp(18), dp(20), dp(18));
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{COLOR_ACENTO, COLOR_ACENTO2});
        bg.setCornerRadius(dp(30));
        btn.setBackground(bg);
        btn.setElevation(dp(4));
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return btn;
    }

    private Button crearBotonSecundario(String texto) {
        Button btn = new Button(getContext());
        btn.setText(texto);
        btn.setTextColor(COLOR_SUBTITULO);
        btn.setTextSize(15);
        btn.setAllCaps(false);
        btn.setPadding(dp(20), dp(14), dp(20), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.TRANSPARENT);
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(1), COLOR_BORDE);
        btn.setBackground(bg);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return btn;
    }
}