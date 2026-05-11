package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.chat.MensajeChat;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class ConversacionChat extends Fragment {

    // ─────────────────────────────────────────────────────────────
    // COLORES
    // ─────────────────────────────────────────────────────────────

    private static final int COLOR_ROSA = Color.parseColor("#F06292");
    private static final int COLOR_ROSA_OSCURO = Color.parseColor("#7BD8EF");

    private static final int COLOR_TEAL = Color.parseColor("#26A69A");
    private static final int COLOR_TEAL_OSCURO = Color.parseColor("#00897B");

    private static final int COLOR_TEXTO = Color.parseColor("#37474F");
    private static final int COLOR_TEXTO_SUAVE = Color.parseColor("#78909C");

    private static final int COLOR_BORDE = Color.parseColor("#A5D6A7");

    // ─────────────────────────────────────────────────────────────
    // ESTADO
    // ─────────────────────────────────────────────────────────────

    private ChatViewModel viewModel;

    private LinearLayout contenedorMensajes;
    private ScrollView scroll;

    private int idConversacion;
    private int idUsuarioPropio;

    private String nombreOtro = "Chat";
    private String inicialOtro = "?";

    // ─────────────────────────────────────────────────────────────
    // CICLO DE VIDA
    // ─────────────────────────────────────────────────────────────

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        if (getArguments() != null) {

            // ← IMPORTANTE:
            // usamos EXACTAMENTE las claves del código que funciona
            idConversacion = getArguments().getInt("idConversacion", 0);

            idUsuarioPropio = getArguments().getInt("idUsuario", -1);

            nombreOtro = getArguments().getString("nombreOtro", "Chat");
        }

        if (idUsuarioPropio == -1 && getContext() != null) {
            idUsuarioPropio = GestorSesion.obtenerId_usuario(requireContext());
        }

        if (nombreOtro != null && !nombreOtro.isEmpty()) {
            inicialOtro = String.valueOf(nombreOtro.charAt(0)).toUpperCase();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // ───────────────── ROOT ─────────────────

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        Color.parseColor("#FBC7DF"),
                        Color.parseColor("#F0FAFA")
                });

        root.setBackground(fondo);

        // ───────────────── TOP BAR ─────────────────

        root.addView(crearTopBar());

        // Separador

        View sep = new View(getContext());

        sep.setBackgroundColor(COLOR_BORDE);
        sep.setAlpha(0.7f);

        sep.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)));

        root.addView(sep);

        // ───────────────── MENSAJES ─────────────────

        scroll = new ScrollView(getContext());

        scroll.setFillViewport(true);

        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f));

        contenedorMensajes = new LinearLayout(getContext());

        contenedorMensajes.setOrientation(LinearLayout.VERTICAL);

        contenedorMensajes.setPadding(
                dp(14),
                dp(16),
                dp(14),
                dp(10));

        scroll.addView(contenedorMensajes);

        root.addView(scroll);

        // Separador

        View sep2 = new View(getContext());

        sep2.setBackgroundColor(COLOR_BORDE);
        sep2.setAlpha(0.7f);

        sep2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(1)));

        root.addView(sep2);

        // ───────────────── INPUT BAR ─────────────────

        LinearLayout[] inputBarRef = new LinearLayout[1];
        EditText[] inputRef = new EditText[1];

        inputBarRef[0] = crearInputBar(inputRef);

        root.addView(inputBarRef[0]);

        EditText input = inputRef[0];

        // ───────────────── OBSERVER ─────────────────

        viewModel.getMensajes().observe(
                getViewLifecycleOwner(),
                this::renderMensajes
        );

        // ───────────────── CARGA ─────────────────

        viewModel.cargarMensajes(idConversacion);

        viewModel.conectarWebSocket(idConversacion);

        // ───────────────── ENVÍO ─────────────────

        Runnable enviar = () -> {

            String texto = input.getText().toString().trim();

            if (!texto.isEmpty()) {

                viewModel.enviarMensaje(
                        idUsuarioPropio,
                        idConversacion,
                        texto
                );

                input.setText("");
            }
        };

        // ← usamos el mismo TAG que funciona

        View btnEnviar = inputBarRef[0].findViewWithTag("btn_enviar");

        if (btnEnviar != null) {
            btnEnviar.setOnClickListener(v -> enviar.run());
        }

        input.setOnEditorActionListener((v, actionId, event) -> {

            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviar.run();
                return true;
            }

            return false;
        });

        return root;
    }

    // ─────────────────────────────────────────────────────────────
    // TOP BAR
    // ─────────────────────────────────────────────────────────────

    private View crearTopBar() {

        LinearLayout topBar = new LinearLayout(getContext());

        topBar.setOrientation(LinearLayout.HORIZONTAL);

        topBar.setGravity(Gravity.CENTER_VERTICAL);

        topBar.setPadding(dp(12), dp(14), dp(16), dp(14));

        topBar.setBackgroundColor(Color.WHITE);

        // ATRÁS

        TextView btnAtras = new TextView(getContext());

        btnAtras.setText("←");

        btnAtras.setTextSize(22);

        btnAtras.setTypeface(null, Typeface.BOLD);

        btnAtras.setTextColor(COLOR_TEAL);

        LinearLayout.LayoutParams atrasParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        atrasParams.setMargins(0, 0, dp(12), 0);

        btnAtras.setLayoutParams(atrasParams);

        btnAtras.setOnClickListener(v ->
                requireActivity().onBackPressed());

        // AVATAR

        TextView avatar = new TextView(getContext());

        avatar.setText(inicialOtro);

        avatar.setTextColor(Color.WHITE);

        avatar.setTypeface(null, Typeface.BOLD);

        avatar.setTextSize(17);

        avatar.setGravity(Gravity.CENTER);

        int avatarSize = dp(42);

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(
                        avatarSize,
                        avatarSize);

        avatarParams.setMargins(0, 0, dp(12), 0);

        avatar.setLayoutParams(avatarParams);

        GradientDrawable avatarBg = new GradientDrawable();

        avatarBg.setShape(GradientDrawable.OVAL);

        avatarBg.setColor(COLOR_ROSA);

        avatar.setBackground(avatarBg);

        // NOMBRE

        TextView nombreTv = new TextView(getContext());

        nombreTv.setText(nombreOtro);

        nombreTv.setTextSize(16);

        nombreTv.setTypeface(null, Typeface.BOLD);

        nombreTv.setTextColor(COLOR_TEXTO);

        nombreTv.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f));

        // ICONO

        TextView icono = new TextView(getContext());

        icono.setText("💬");

        icono.setTextSize(20);

        topBar.addView(btnAtras);
        topBar.addView(avatar);
        topBar.addView(nombreTv);
        topBar.addView(icono);

        return topBar;
    }

    // ─────────────────────────────────────────────────────────────
    // INPUT BAR
    // ─────────────────────────────────────────────────────────────

    private LinearLayout crearInputBar(EditText[] inputRef) {

        LinearLayout inputBar = new LinearLayout(getContext());

        inputBar.setOrientation(LinearLayout.HORIZONTAL);

        inputBar.setGravity(Gravity.CENTER_VERTICAL);

        inputBar.setPadding(dp(12), dp(10), dp(12), dp(10));

        inputBar.setBackgroundColor(Color.WHITE);

        // INPUT

        EditText input = new EditText(getContext());

        input.setHint("Escribe un mensaje…");

        input.setHintTextColor(COLOR_TEXTO_SUAVE);

        input.setTextColor(COLOR_TEXTO);

        input.setTextSize(15);

        input.setImeOptions(EditorInfo.IME_ACTION_SEND);

        input.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        input.setMaxLines(4);

        input.setPadding(dp(14), dp(10), dp(14), dp(10));

        GradientDrawable bgInput = new GradientDrawable();

        bgInput.setColor(Color.parseColor("#F5F7FA"));

        bgInput.setCornerRadius(dp(30));

        bgInput.setStroke(dp(1), COLOR_BORDE);

        input.setBackground(bgInput);

        LinearLayout.LayoutParams inputParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f);

        inputParams.setMargins(0, 0, dp(10), 0);

        input.setLayoutParams(inputParams);

        inputRef[0] = input;

        // BOTÓN ENVIAR

        TextView btnEnviar = new TextView(getContext());

        // ← MISMO TAG DEL QUE FUNCIONA
        btnEnviar.setTag("btn_enviar");

        btnEnviar.setText("➤");

        btnEnviar.setTextColor(Color.WHITE);

        btnEnviar.setTextSize(18);

        btnEnviar.setGravity(Gravity.CENTER);

        int btnSize = dp(44);

        btnEnviar.setLayoutParams(
                new LinearLayout.LayoutParams(btnSize, btnSize));

        GradientDrawable bgBtn = new GradientDrawable();

        bgBtn.setShape(GradientDrawable.OVAL);

        bgBtn.setColor(COLOR_ROSA);

        btnEnviar.setBackground(bgBtn);

        inputBar.addView(input);

        inputBar.addView(btnEnviar);

        return inputBar;
    }

    // ─────────────────────────────────────────────────────────────
    // RENDER MENSAJES
    // ─────────────────────────────────────────────────────────────

    private void renderMensajes(List<MensajeChat> mensajes) {

        contenedorMensajes.removeAllViews();

        if (mensajes == null || mensajes.isEmpty()) {

            TextView vacio = new TextView(getContext());

            vacio.setText("No hay mensajes aún.\n¡Sé el primero en escribir!");

            vacio.setTextSize(14);

            vacio.setTextColor(COLOR_TEXTO_SUAVE);

            vacio.setGravity(Gravity.CENTER);

            vacio.setPadding(0, dp(40), 0, 0);

            contenedorMensajes.addView(vacio);

            return;
        }

        for (MensajeChat m : mensajes) {
            contenedorMensajes.addView(crearBurbuja(m));
        }

        scroll.post(() ->
                scroll.fullScroll(View.FOCUS_DOWN));
    }

    private View crearBurbuja(MensajeChat m) {

        // ← PROTECCIÓN NULL
        boolean esPropio =
                m.getIdUsuario() != null
                        && m.getIdUsuario() == idUsuarioPropio;

        LinearLayout fila = new LinearLayout(getContext());

        fila.setOrientation(LinearLayout.VERTICAL);

        fila.setGravity(esPropio ? Gravity.END : Gravity.START);

        LinearLayout.LayoutParams filaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        filaParams.setMargins(0, 0, 0, dp(10));

        fila.setLayoutParams(filaParams);

        // Nombre remitente

        if (!esPropio
                && m.getNombre() != null
                && !m.getNombre().isEmpty()) {

            TextView nombreTv = new TextView(getContext());

            nombreTv.setText(m.getNombre());

            nombreTv.setTextSize(11);

            nombreTv.setTypeface(null, Typeface.BOLD);

            nombreTv.setTextColor(COLOR_TEAL_OSCURO);

            LinearLayout.LayoutParams np =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            np.setMargins(dp(4), 0, 0, dp(2));

            nombreTv.setLayoutParams(np);

            fila.addView(nombreTv);
        }

        // BURBUJA

        TextView burbuja = new TextView(getContext());

        burbuja.setText(m.getContenido());

        burbuja.setTextSize(15);

        burbuja.setPadding(dp(14), dp(10), dp(14), dp(10));

        burbuja.setTextColor(
                esPropio ? Color.WHITE : COLOR_TEXTO);

        LinearLayout.LayoutParams burbujaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        burbujaParams.setMargins(
                esPropio ? dp(60) : 0,
                0,
                esPropio ? 0 : dp(60),
                0);

        burbuja.setLayoutParams(burbujaParams);

        GradientDrawable bg;

        if (esPropio) {

            bg = new GradientDrawable(
                    GradientDrawable.Orientation.TL_BR,
                    new int[]{
                            COLOR_ROSA,
                            COLOR_ROSA_OSCURO
                    });

            bg.setCornerRadii(new float[]{
                    dp(18), dp(18),
                    dp(4), dp(4),
                    dp(18), dp(18),
                    dp(18), dp(18)
            });

        } else {

            bg = new GradientDrawable();

            bg.setColor(Color.WHITE);

            bg.setStroke(dp(1), COLOR_BORDE);

            bg.setCornerRadii(new float[]{
                    dp(4), dp(4),
                    dp(18), dp(18),
                    dp(18), dp(18),
                    dp(18), dp(18)
            });
        }

        burbuja.setBackground(bg);

        fila.addView(burbuja);

        // HORA

        if (m.getCreadoEn() != null
                && !m.getCreadoEn().isEmpty()) {

            TextView horaTv = new TextView(getContext());

            horaTv.setText(formatearHora(m.getCreadoEn()));

            horaTv.setTextSize(10);

            horaTv.setTextColor(COLOR_TEXTO_SUAVE);

            LinearLayout.LayoutParams hp =
                    new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);

            hp.setMargins(
                    esPropio ? 0 : dp(4),
                    dp(2),
                    esPropio ? dp(4) : 0,
                    0);

            horaTv.setLayoutParams(hp);

            fila.addView(horaTv);
        }

        return fila;
    }

    // ─────────────────────────────────────────────────────────────
    // UTILIDADES
    // ─────────────────────────────────────────────────────────────

    private String formatearHora(String creadoEn) {

        try {

            int idxT = creadoEn.indexOf('T');

            if (idxT >= 0 && creadoEn.length() > idxT + 5) {
                return creadoEn.substring(idxT + 1, idxT + 6);
            }

        } catch (Exception ignored) {}

        return creadoEn;
    }

    private int dp(int dp) {
        return Math.round(
                dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.desconectar();
    }
}