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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.clases.Puzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class EditarPuzzle extends Fragment {

    private static final String ARG_PUZZLE = "puzzle";

    private PuzzleViewModel viewModel;
    private Puzzle puzzle;

    private EditText campoTitulo, campoAutor, campoPiezas, campoTiempo, campoDescripcion;
    private Slider   sliderDificultad;
    private TextView tvDifValor;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch   switchColor, switchEstado;

    private boolean dificultadAutomatica = true;

    public static EditarPuzzle newInstance(Puzzle puzzle) {
        EditarPuzzle f = new EditarPuzzle();
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

        // ── Fondo gradiente: instancias SEPARADAS para scroll y root ──
        scroll.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ContextCompat.getColor(requireContext(), R.color.app_rosa_soft), ContextCompat.getColor(requireContext(), R.color.app_green_success)}
        ));
        root.setBackground(new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{ContextCompat.getColor(requireContext(), R.color.app_rosa_soft), ContextCompat.getColor(requireContext(), R.color.app_green_success)}
        ));

        // ═══════════════════════════════════════════════════
        //  HEADER con gradiente y título decorado
        // ═══════════════════════════════════════════════════
        FrameLayout header = new FrameLayout(getContext());
        header.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        header.setPadding(dp(24), dp(56), dp(24), dp(32));
        // Sin fondo propio — hereda el gradiente del root

        LinearLayout headerContent = new LinearLayout(getContext());
        headerContent.setOrientation(LinearLayout.VERTICAL);
        headerContent.setGravity(Gravity.CENTER_HORIZONTAL);
        FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        headerContent.setLayoutParams(flp);

        // Insignia/chip sobre el título
        TextView tvBadge = new TextView(getContext());
        tvBadge.setText("✏️  Edición");
        tvBadge.setTextSize(12);
        tvBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_rosa_acento));
        tvBadge.setTypeface(null, Typeface.BOLD);
        tvBadge.setPadding(dp(14), dp(6), dp(14), dp(6));
        GradientDrawable badgeBg = new GradientDrawable();
        badgeBg.setColor(Color.WHITE);
        badgeBg.setCornerRadius(dp(20));
        badgeBg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_rosa_acento_light));
        tvBadge.setBackground(badgeBg);
        LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        badgeParams.gravity = Gravity.CENTER_HORIZONTAL;
        badgeParams.setMargins(0, 0, 0, dp(14));
        tvBadge.setLayoutParams(badgeParams);

        // Título principal grande
        TextView tvTitulo = new TextView(getContext());
        tvTitulo.setText("Editar Puzzle");
        tvTitulo.setTextSize(30);
        tvTitulo.setTypeface(null, Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_texto_oscuro));
        tvTitulo.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams tituloParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.setMargins(0, 0, 0, dp(8));
        tvTitulo.setLayoutParams(tituloParams);

        // Nombre del puzzle como subtítulo
        String nombrePuzzle = puzzle.getTitulo() != null ? "\"" + puzzle.getTitulo() + "\"" : "";
        TextView tvNombrePuzzle = new TextView(getContext());
        tvNombrePuzzle.setText(nombrePuzzle);
        tvNombrePuzzle.setTextSize(15);
        tvNombrePuzzle.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_rosa_acento));
        tvNombrePuzzle.setGravity(Gravity.CENTER_HORIZONTAL);
        tvNombrePuzzle.setTypeface(null, Typeface.ITALIC);
        LinearLayout.LayoutParams nombreParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nombreParams.setMargins(0, 0, 0, dp(6));
        tvNombrePuzzle.setLayoutParams(nombreParams);

        // Subtítulo informativo
        TextView tvSubtitulo = new TextView(getContext());
        tvSubtitulo.setText("Modifica los campos que desees");
        tvSubtitulo.setTextSize(13);
        tvSubtitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));
        tvSubtitulo.setGravity(Gravity.CENTER_HORIZONTAL);

        // Línea decorativa bajo el header
        View lineaDecorada = new View(getContext());
        LinearLayout.LayoutParams lineaParams = new LinearLayout.LayoutParams(dp(48), dp(4));
        lineaParams.gravity = Gravity.CENTER_HORIZONTAL;
        lineaParams.setMargins(0, dp(16), 0, 0);
        lineaDecorada.setLayoutParams(lineaParams);
        GradientDrawable lineaBg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{ContextCompat.getColor(requireContext(), R.color.app_rosa_acento), ContextCompat.getColor(requireContext(), R.color.app_rosa_acento_light)});
        lineaBg.setCornerRadius(dp(4));
        lineaDecorada.setBackground(lineaBg);

        headerContent.addView(tvBadge);
        headerContent.addView(tvTitulo);
        if (!nombrePuzzle.isEmpty()) headerContent.addView(tvNombrePuzzle);
        headerContent.addView(tvSubtitulo);
        headerContent.addView(lineaDecorada);

        header.addView(headerContent);
        root.addView(header);

        // ── Contenido con padding ──
        LinearLayout contenido = new LinearLayout(getContext());
        contenido.setOrientation(LinearLayout.VERTICAL);
        contenido.setPadding(dp(20), dp(24), dp(20), dp(40));
        // Sin fondo propio — transparente para mostrar el gradiente del root

        // ── Sección: Información básica ──
        contenido.addView(tituloSeccion("📋  Información básica"));
        LinearLayout cardBasica = crearCard();
        campoTitulo = crearCampo("Título del puzzle", puzzle.getTitulo());
        campoAutor  = crearCampo("Autor", puzzle.getAutor());
        cardBasica.addView(campoTitulo);
        cardBasica.addView(separador());
        cardBasica.addView(campoAutor);
        contenido.addView(cardBasica);
        contenido.addView(espacio(dp(18)));

        // ── Sección: Detalles ──
        contenido.addView(tituloSeccion("🧩  Detalles"));
        LinearLayout cardDetalles = crearCard();

        campoTiempo      = crearCampoNumero("Tiempo (horas)",
                puzzle.getTiempo() != null ? String.valueOf(puzzle.getTiempo()) : "");
        campoPiezas      = crearCampoNumero("Número de piezas",
                puzzle.getPiezas() != null ? String.valueOf(puzzle.getPiezas()) : "");
        campoDescripcion = crearCampoMultilinea("Descripción", puzzle.getDescripcion());

        // ── Fila dificultad ──
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
        tvDifLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_texto_oscuro));
        tvDifLabel.setTypeface(null, Typeface.BOLD);

        TextView tvDifAuto = new TextView(getContext());
        tvDifAuto.setText("Automática según piezas");
        tvDifAuto.setTextSize(11);
        tvDifAuto.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));

        difTextos.addView(tvDifLabel);
        difTextos.addView(tvDifAuto);

        CheckBox autoCheck = new CheckBox(getContext());
        autoCheck.setText("Auto");
        autoCheck.setTextSize(12);
        autoCheck.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));
        autoCheck.setChecked(true);

        sliderDificultad = new Slider(getContext());
        sliderDificultad.setValueFrom(0);
        sliderDificultad.setValueTo(3);
        sliderDificultad.setStepSize(1);
        sliderDificultad.setValue(dificultadAInt(puzzle.getDificultad()));
        sliderDificultad.setTrackInactiveTintList(ColorStateList.valueOf(Color.parseColor("#ECEFF1")));
        sliderDificultad.setLabelBehavior(LabelFormatter.LABEL_GONE);
        sliderDificultad.setEnabled(false);

        LinearLayout.LayoutParams sliderParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sliderParams.setMargins(0, dp(4), 0, dp(4));
        sliderDificultad.setLayoutParams(sliderParams);

        // Etiquetas del slider
        LinearLayout etiquetasDif = new LinearLayout(getContext());
        etiquetasDif.setOrientation(LinearLayout.HORIZONTAL);
        etiquetasDif.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        String[] niveles = {"Fácil", "Media", "Difícil", "Extremo"};
        int[] coloresNivel = {ContextCompat.getColor(requireContext(), R.color.app_dificultad_facil), ContextCompat.getColor(requireContext(), R.color.app_naranja_amber_light), ContextCompat.getColor(requireContext(), R.color.app_peligro_medium), ContextCompat.getColor(requireContext(), R.color.app_peligro_darker)};
        for (int i = 0; i < niveles.length; i++) {
            TextView chip = new TextView(getContext());
            chip.setText(niveles[i]);
            chip.setTextSize(10);
            chip.setTextColor(coloresNivel[i]);
            chip.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            chip.setLayoutParams(chipParams);
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
        actualizarColorDificultad((int) sliderDificultad.getValue());

        autoCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            dificultadAutomatica = isChecked;
            sliderDificultad.setEnabled(!isChecked);
            if (isChecked) recalcularDificultad();
        });

        sliderDificultad.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                dificultadAutomatica = false;
                autoCheck.setChecked(false);
                sliderDificultad.setEnabled(true);
            }
            actualizarColorDificultad((int) value);
        });

        campoPiezas.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) recalcularDificultad();
            }
        });

        filaDificultad.addView(difTextos);
        filaDificultad.addView(autoCheck);

        cardDetalles.addView(campoTiempo);
        cardDetalles.addView(separador());
        cardDetalles.addView(campoPiezas);
        cardDetalles.addView(separador());
        cardDetalles.addView(campoDescripcion);
        cardDetalles.addView(separador());
        cardDetalles.addView(filaDificultad);
        cardDetalles.addView(sliderDificultad);
        cardDetalles.addView(etiquetasDif);
        cardDetalles.addView(tvDifValor);
        contenido.addView(cardDetalles);
        contenido.addView(espacio(dp(18)));

        // ── Sección: Opciones ──
        contenido.addView(tituloSeccion("⚙️  Opciones"));
        LinearLayout cardOpciones = crearCard();

        LinearLayout filaColor = crearFilaSwitch();
        LinearLayout textoColor = textoDoble("🎨  Color del puzzle", "El puzzle tiene más de un color");
        switchColor = new Switch(getContext());
        switchColor.setChecked(puzzle.isColor() != null && puzzle.isColor());
        switchColor.setOnCheckedChangeListener((btn, isChecked) -> recalcularDificultad());
        filaColor.addView(textoColor);
        filaColor.addView(switchColor);
        cardOpciones.addView(filaColor);
        cardOpciones.addView(separador());

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
        tvEstadoTitulo.setText("🔒  Visibilidad");
        tvEstadoTitulo.setTextSize(16);
        tvEstadoTitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_texto_oscuro));
        tvEstadoTitulo.setTypeface(null, Typeface.BOLD);

        TextView tvEstadoDesc = new TextView(getContext());
        boolean esPublico = puzzle.getEstado() == Puzzle.Estados.Publico;
        tvEstadoDesc.setText(esPublico ? "Público" : "Privado");
        tvEstadoDesc.setTextSize(12);
        tvEstadoDesc.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));

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
        contenido.addView(cardOpciones);
        contenido.addView(espacio(dp(32)));

        // ── Botón guardar ──
        Button btnGuardar = crearBotonPrincipal("Guardar cambios");
        btnGuardar.setOnClickListener(v -> guardarCambios());
        contenido.addView(btnGuardar);
        contenido.addView(espacio(dp(12)));

        // ── Botón cancelar ──
        Button btnCancelar = crearBotonSecundario("Cancelar");
        btnCancelar.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
        contenido.addView(btnCancelar);

        root.addView(contenido);
        scroll.addView(root);

        // ── Observers ──
        viewModel.getPuzzleActualizado().observe(getViewLifecycleOwner(), ok -> {
            if (ok != null && ok) {
                Toast.makeText(getContext(), "✔ Puzzle actualizado", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        viewModel.getError().observe(getViewLifecycleOwner(), msg ->
                Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show());

        return scroll;
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
        String txt = campoPiezas.getText().toString();
        if (txt.isEmpty()) return;
        try {
            sliderDificultad.setValue(
                    calcularDificultad(Integer.parseInt(txt), switchColor.isChecked()));
        } catch (Exception ignored) {}
    }

    // ─────────────────────────────────────────────────────
    //  Guardar cambios
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
    //  Helpers de dificultad
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
        String emoji;
        switch (v) {
            case 1: texto = "Media";   color = ContextCompat.getColor(requireContext(), R.color.app_naranja_amber_light);   emoji = "🟡"; break;
            case 2: texto = "Difícil"; color = ContextCompat.getColor(requireContext(), R.color.app_peligro_medium); emoji = "🟠"; break;
            case 3: texto = "Extremo"; color = ContextCompat.getColor(requireContext(), R.color.app_peligro_darker); emoji = "🔴"; break;
            default: texto = "Fácil";  color = ContextCompat.getColor(requireContext(), R.color.app_dificultad_facil);   emoji = "🟢"; break;
        }
        tvDifValor.setText(emoji + "  " + texto);
        tvDifValor.setTextColor(color);
        sliderDificultad.setTrackActiveTintList(ColorStateList.valueOf(color));
        sliderDificultad.setThumbTintList(ColorStateList.valueOf(color));
    }

    // ─────────────────────────────────────────────────────
    //  Helpers UI
    // ─────────────────────────────────────────────────────

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private LinearLayout crearCard() {
        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(18), dp(14), dp(18), dp(14));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_green_borde_alt));
        card.setBackground(bg);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        card.setLayoutParams(params);
        return card;
    }

    private EditText crearCampo(String hint, String valor) {
        EditText et = new EditText(getContext());
        et.setHint(hint);
        et.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        et.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_texto_oscuro));
        et.setTextSize(15);
        et.setBackground(null);
        et.setPadding(dp(4), dp(12), dp(4), dp(12));
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
        tv.setTextSize(13);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));
        tv.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(dp(4), 0, 0, dp(8));
        tv.setLayoutParams(p);
        return tv;
    }

    private View separador() {
        View v = new View(getContext());
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        p.setMargins(0, dp(2), 0, dp(2));
        v.setLayoutParams(p);
        v.setBackgroundColor(Color.parseColor("#E8F5E9"));
        v.setAlpha(0.45f);
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
        fila.setPadding(dp(4), dp(12), dp(4), dp(12));
        fila.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return fila;
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

    private LinearLayout textoDoble(String titulo, String desc) {
        LinearLayout ll = new LinearLayout(getContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        TextView tvT = new TextView(getContext());
        tvT.setText(titulo);
        tvT.setTextSize(16);
        tvT.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_texto_oscuro));
        tvT.setTypeface(null, Typeface.BOLD);
        TextView tvD = new TextView(getContext());
        tvD.setText(desc);
        tvD.setTextSize(12);
        tvD.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));
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
                new int[]{ContextCompat.getColor(requireContext(), R.color.app_rosa_acento), ContextCompat.getColor(requireContext(), R.color.app_rosa_acento_light)});
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
        btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_dark));
        btn.setTextSize(15);
        btn.setAllCaps(false);
        btn.setPadding(dp(20), dp(14), dp(20), dp(14));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.TRANSPARENT);
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_green_borde_alt));
        btn.setBackground(bg);
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        return btn;
    }
}