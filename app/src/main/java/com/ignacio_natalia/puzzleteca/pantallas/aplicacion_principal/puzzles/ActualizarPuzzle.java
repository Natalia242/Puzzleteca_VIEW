package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class ActualizarPuzzle extends Fragment {

    private static final String ARG_PUZZLE = "puzzle";

    private PuzzleViewModel viewModel;
    private Puzzle puzzle;

    private EditText campoTitulo, campoAutor, campoPiezas, campoTiempo, campoDescripcion;
    private Slider   sliderDificultad;
    private TextView tvDifValor;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch   switchColor, switchEstado;

    private boolean dificultadAutomatica = false;

    private static final int
            COLOR_CARD      = Color.WHITE,
            COLOR_ACENTO    = Color.parseColor("#F06292"),
            COLOR_BORDE     = Color.parseColor("#A5D6A7"),
            COLOR_TEXTO     = Color.parseColor("#263238"),
            COLOR_HINT      = Color.parseColor("#90A4AE"),
            COLOR_SUBTITULO = Color.parseColor("#546E7A"),
            COLOR_FACIL     = Color.parseColor("#7BCF9E"),
            COLOR_MEDIA     = Color.parseColor("#F2C66D"),
            COLOR_DIFICIL   = Color.parseColor("#F6A06A"),
            COLOR_EXTREMO   = Color.parseColor("#E57373");

    public static ActualizarPuzzle newInstance(Puzzle puzzle) {
        ActualizarPuzzle f = new ActualizarPuzzle();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PUZZLE, puzzle);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(PuzzleViewModel.class);
        if (getArguments() != null) {
            puzzle = (Puzzle) getArguments().getSerializable(ARG_PUZZLE);
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        if (puzzle == null) {
            Toast.makeText(getContext(), "Error: puzzle no recibido", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
            return new View(getContext());
        }

        ScrollView scroll = new ScrollView(getContext());
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(40));

        // ── Título pantalla ──
        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText("Editar Puzzle");
        tvTitulo.setTextSize(26);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(COLOR_TEXTO);
        tvTitulo.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tvSubtitulo = new TextView(getContext());
        tvSubtitulo.setText("Modifica los campos que desees");
        tvSubtitulo.setTextSize(14);
        tvSubtitulo.setTextColor(COLOR_SUBTITULO);
        tvSubtitulo.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams paramsSub = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsSub.setMargins(0, dp(4), 0, dp(20));
        tvSubtitulo.setLayoutParams(paramsSub);

        root.addView(tvTitulo);
        root.addView(tvSubtitulo);

        // ── Sección: Información básica ──
        root.addView(tituloSeccion("Información básica"));
        LinearLayout cardBasica = crearCard();
        campoTitulo = crearCampo("Título del puzzle", puzzle.getTitulo());
        campoAutor  = crearCampo("Autor", puzzle.getAutor());
        cardBasica.addView(campoTitulo);
        cardBasica.addView(separador());
        cardBasica.addView(campoAutor);
        root.addView(cardBasica);
        root.addView(espacio(dp(14)));

        // ── Sección: Detalles ──
        root.addView(tituloSeccion("Detalles"));
        LinearLayout cardDetalles = crearCard();

        campoTiempo      = crearCampoNumero("Tiempo (Horas)",
                puzzle.getTiempo() != null ? String.valueOf(puzzle.getTiempo()) : "");
        campoPiezas      = crearCampoNumero("Número de piezas",
                puzzle.getPiezas() != null ? String.valueOf(puzzle.getPiezas()) : "");
        campoDescripcion = crearCampoMultilinea("Descripción", puzzle.getDescripcion());

        // ── Fila dificultad: label + checkbox auto ──
        LinearLayout filaDificultad = new LinearLayout(getContext());
        filaDificultad.setOrientation(LinearLayout.HORIZONTAL);
        filaDificultad.setGravity(Gravity.CENTER_VERTICAL);
        filaDificultad.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView tvDifLabel = new TextView(getContext());
        tvDifLabel.setText("Dificultad");
        tvDifLabel.setTextSize(13);
        tvDifLabel.setTextColor(COLOR_SUBTITULO);
        tvDifLabel.setPadding(dp(4), dp(10), dp(4), 0);
        tvDifLabel.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        CheckBox autoCheck = new CheckBox(getContext());
        autoCheck.setText("Auto");
        autoCheck.setChecked(false); // al editar, por defecto se respeta la dificultad guardada

        sliderDificultad = new Slider(getContext());
        sliderDificultad.setValueFrom(0);
        sliderDificultad.setValueTo(3);
        sliderDificultad.setStepSize(1);
        sliderDificultad.setValue(dificultadAInt(puzzle.getDificultad()));
        sliderDificultad.setTrackInactiveTintList(ColorStateList.valueOf(Color.TRANSPARENT));
        sliderDificultad.setLabelBehavior(LabelFormatter.LABEL_GONE);
        sliderDificultad.setEnabled(true); // empieza habilitado (modo manual)

        tvDifValor = new TextView(getContext());
        tvDifValor.setTextColor(COLOR_SUBTITULO);
        actualizarColorDificultad((int) sliderDificultad.getValue());

        // Checkbox: activar/desactivar modo automático
        autoCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dificultadAutomatica = isChecked;
            sliderDificultad.setEnabled(!isChecked);
            if (isChecked) recalcularDificultad();
        });

        // Slider: si el usuario lo mueve manualmente, desactivar auto
        sliderDificultad.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                dificultadAutomatica = false;
                autoCheck.setChecked(false);
                sliderDificultad.setEnabled(true);
            }
            actualizarColorDificultad((int) value);
        });

        // Piezas: recalcular si auto está activo
        campoPiezas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) recalcularDificultad();
            }
        });

        filaDificultad.addView(tvDifLabel);
        filaDificultad.addView(autoCheck);

        cardDetalles.addView(campoTiempo);
        cardDetalles.addView(separador());
        cardDetalles.addView(campoPiezas);
        cardDetalles.addView(separador());
        cardDetalles.addView(campoDescripcion);
        cardDetalles.addView(separador());
        cardDetalles.addView(filaDificultad);
        cardDetalles.addView(sliderDificultad);
        cardDetalles.addView(tvDifValor);
        root.addView(cardDetalles);
        root.addView(espacio(dp(14)));

        // ── Sección: Opciones ──
        root.addView(tituloSeccion("Opciones"));
        LinearLayout cardOpciones = crearCard();

        // Switch color
        LinearLayout filaColor = crearFilaSwitch();
        LinearLayout textoColor = textoDoble("Color del puzzle", "El puzzle tiene más de un color");
        switchColor = new Switch(getContext());
        switchColor.setChecked(puzzle.isColor() != null && puzzle.isColor());
        // Al cambiar color, recalcular si auto está activo
        switchColor.setOnCheckedChangeListener((btn, isChecked) -> recalcularDificultad());
        filaColor.addView(textoColor);
        filaColor.addView(switchColor);
        cardOpciones.addView(filaColor);
        cardOpciones.addView(separador());

        // Switch estado
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

        TextView tvEstadoTitulo = new TextView(getContext());
        tvEstadoTitulo.setText("Visibilidad");
        tvEstadoTitulo.setTextSize(16);
        tvEstadoTitulo.setTextColor(COLOR_TEXTO);
        tvEstadoTitulo.setTypeface(null, Typeface.BOLD);

        TextView tvEstadoDesc = new TextView(getContext());
        boolean esPublico = puzzle.getEstado() == Puzzle.Estados.Publico;
        tvEstadoDesc.setText(esPublico ? "Público" : "Privado");
        tvEstadoDesc.setTextSize(12);
        tvEstadoDesc.setTextColor(COLOR_SUBTITULO);

        textoEstado.addView(tvEstadoTitulo);
        textoEstado.addView(tvEstadoDesc);
        contenidoEstado.addView(textoEstado);

        switchEstado = new Switch(getContext());
        switchEstado.setChecked(esPublico);
        switchEstado.setOnCheckedChangeListener((btn, isChecked) ->
                tvEstadoDesc.setText(isChecked ? "Público" : "Privado"));

        filaEstado.addView(contenidoEstado);
        filaEstado.addView(switchEstado);
        cardOpciones.addView(filaEstado);
        root.addView(cardOpciones);
        root.addView(espacio(dp(28)));

        // ── Botón guardar ──
        Button btnGuardar = crearBotonPrincipal("Guardar cambios");
        btnGuardar.setOnClickListener(v -> guardarCambios());
        root.addView(btnGuardar);
        root.addView(espacio(dp(12)));

        // ── Botón cancelar ──
        Button btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        root.addView(btnCancelar);

        scroll.addView(root);

        // ── Observers ──
        viewModel.getPuzzleActualizado().observe(getViewLifecycleOwner(), ok -> {
            if (ok != null && ok) {
                Toast.makeText(getContext(), "Puzzle actualizado ✔", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());

        return scroll;
    }

    // ─────────────────────────────────────────────────────
    // Lógica de dificultad
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
        String txt = campoPiezas.getText().toString();
        if (txt.isEmpty()) return;
        try {
            sliderDificultad.setValue(
                    calcularDificultad(Integer.parseInt(txt), switchColor.isChecked()));
        } catch (Exception ignored) {}
    }

    // ─────────────────────────────────────────────────────
    // Guardar cambios
    // ─────────────────────────────────────────────────────

    private void guardarCambios() {
        String token     = GestorSesion.obtenerToken(getContext());
        int    idUsuario = GestorSesion.obtenerId_usuario(getContext());
        int    idPuzzle  = puzzle.getId();

        String nuevoTitulo = campoTitulo.getText().toString().trim();
        String viejoTitulo = puzzle.getTitulo() != null ? puzzle.getTitulo().trim() : "";
        if (!nuevoTitulo.isEmpty() && !nuevoTitulo.equals(viejoTitulo))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "titulo", nuevoTitulo);

        String nuevoAutor = campoAutor.getText().toString().trim();
        String viejoAutor = puzzle.getAutor() != null ? puzzle.getAutor().trim() : "";
        if (!nuevoAutor.isEmpty() && !nuevoAutor.equals(viejoAutor))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "autor", nuevoAutor);

        String nuevoDesc = campoDescripcion.getText().toString().trim();
        String viejoDesc = puzzle.getDescripcion() != null ? puzzle.getDescripcion().trim() : "";
        if (!nuevoDesc.equals(viejoDesc))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "descripcion", nuevoDesc);

        String nuevoPiezas = campoPiezas.getText().toString().trim();
        String viejoPiezas = puzzle.getPiezas() != null ? String.valueOf(puzzle.getPiezas()) : "";
        if (!nuevoPiezas.isEmpty() && !nuevoPiezas.equals(viejoPiezas))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "piezas", nuevoPiezas);

        String nuevoTiempo = campoTiempo.getText().toString().trim();
        String viejoTiempo = puzzle.getTiempo() != null ? String.valueOf(puzzle.getTiempo()) : "";
        if (!nuevoTiempo.isEmpty() && !nuevoTiempo.equals(viejoTiempo))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "tiempo", nuevoTiempo);

        String nuevaDif = intADificultad((int) sliderDificultad.getValue());
        String viejaDif = puzzle.getDificultad() != null ? puzzle.getDificultad().name() : "Facil";
        if (!nuevaDif.equals(viejaDif))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "dificultad", nuevaDif);

        String nuevoColor = String.valueOf(switchColor.isChecked());
        String viejoColor = puzzle.isColor() != null ? String.valueOf(puzzle.isColor()) : "true";
        if (!nuevoColor.equals(viejoColor))
            viewModel.actualizarPuzzle(token, idUsuario, idPuzzle, "color", nuevoColor);

        String nuevoEstado = switchEstado.isChecked() ? "Publico" : "Privado";
        String viejoEstado = puzzle.getEstado() != null ? puzzle.getEstado().name() : "Publico";
        if (!nuevoEstado.equals(viejoEstado))
            viewModel.cambiarEstadoPuzzle(idUsuario, idPuzzle, nuevoEstado);

        viewModel.notificarActualizacion();
    }

    // ─────────────────────────────────────────────────────
    // Helpers de dificultad
    // ─────────────────────────────────────────────────────

    private int dificultadAInt(Puzzle.Dificultades d) {
        if (d == null) return 0;
        switch (d) {
            case Media:   return 1;
            case Dificil: return 2;
            case Extremo: return 3;
            default:      return 0;
        }
    }

    private String intADificultad(int v) {
        switch (v) {
            case 1: return "Media";
            case 2: return "Dificil";
            case 3: return "Extremo";
            default: return "Facil";
        }
    }

    @SuppressLint("SetTextI18n")
    private void actualizarColorDificultad(int v) {
        int color;
        String texto;
        switch (v) {
            case 1: texto = "Media";   color = COLOR_MEDIA;   break;
            case 2: texto = "Difícil"; color = COLOR_DIFICIL; break;
            case 3: texto = "Extremo"; color = COLOR_EXTREMO; break;
            default: texto = "Fácil";  color = COLOR_FACIL;   break;
        }
        tvDifValor.setText(texto);
        sliderDificultad.setTrackActiveTintList(ColorStateList.valueOf(color));
        sliderDificultad.setThumbTintList(ColorStateList.valueOf(color));
    }

    // ─────────────────────────────────────────────────────
    // Helpers UI
    // ─────────────────────────────────────────────────────

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

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

    private EditText crearCampo(String hint, String valor) {
        EditText et = new EditText(getContext());
        et.setHint(hint);
        et.setHintTextColor(COLOR_HINT);
        et.setTextColor(COLOR_TEXTO);
        et.setTextSize(15);
        et.setBackground(null);
        et.setPadding(dp(4), dp(10), dp(4), dp(10));
        et.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (valor != null) et.setText(valor);
        return et;
    }

    private EditText crearCampoNumero(String hint, String valor) {
        EditText et = crearCampo(hint, valor);
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        return et;
    }

    private EditText crearCampoMultilinea(String hint, String valor) {
        EditText et = crearCampo(hint, valor);
        et.setMinLines(2);
        et.setMaxLines(4);
        et.setGravity(Gravity.TOP | Gravity.START);
        return et;
    }

    private TextView tituloSeccion(String texto) {
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

    private View separador() {
        View v = new View(getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        p.setMargins(dp(4), dp(4), dp(4), dp(4));
        v.setLayoutParams(p);
        v.setBackgroundColor(Color.parseColor("#E8F5E9"));
        return v;
    }

    private View espacio(int height) {
        View v = new View(getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, height));
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