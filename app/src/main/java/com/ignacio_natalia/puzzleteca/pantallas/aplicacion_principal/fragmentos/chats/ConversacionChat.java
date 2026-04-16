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

    private ChatViewModel viewModel;
    private LinearLayout contenedorMensajes;
    private ScrollView scroll;

    private int idConversacion;
    private Integer id_usuario;
    private String emailPropio;
    private String nombreOtro;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        if (getArguments() != null) {
            idConversacion = getArguments().getInt("idConversacion", 0);
            emailPropio    = getArguments().getString("emailPropio", "");
            nombreOtro     = getArguments().getString("nombreOtro", "Chat");
        }

        if ((emailPropio == null || emailPropio.isEmpty()) && getContext() != null) {
            emailPropio = GestorSesion.obtenerEmail(requireContext());
        }

        if (id_usuario == null && getContext() != null) {
            id_usuario = GestorSesion.obtenerId_usuario(this.requireContext());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F4F6FB"));

        // ================= TOP BAR =================
        LinearLayout topBar = new LinearLayout(getContext());
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(12), dp(14), dp(16), dp(14));
        topBar.setBackgroundColor(Color.WHITE);

        TextView btnAtras = new TextView(getContext());
        btnAtras.setText("\u2190");
        btnAtras.setTextSize(22);
        btnAtras.setTextColor(Color.parseColor("#4361EE"));
        btnAtras.setOnClickListener(v -> requireActivity().onBackPressed());

        TextView nombreBar = new TextView(getContext());
        nombreBar.setText(nombreOtro != null ? nombreOtro : "Chat");
        nombreBar.setTextSize(17);
        nombreBar.setTypeface(null, Typeface.BOLD);

        topBar.addView(btnAtras);
        topBar.addView(nombreBar);
        root.addView(topBar);

        // ================= MENSAJES =================
        scroll = new ScrollView(getContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));

        contenedorMensajes = new LinearLayout(getContext());
        contenedorMensajes.setOrientation(LinearLayout.VERTICAL);
        contenedorMensajes.setPadding(dp(14), dp(14), dp(14), dp(10));

        scroll.addView(contenedorMensajes);
        root.addView(scroll);

        // ================= INPUT =================
        LinearLayout inputBar = new LinearLayout(getContext());
        inputBar.setOrientation(LinearLayout.HORIZONTAL);
        inputBar.setPadding(dp(12), dp(10), dp(12), dp(10));

        EditText input = new EditText(getContext());
        input.setHint("Escribe un mensaje...");
        input.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView btnEnviar = new TextView(getContext());
        btnEnviar.setText("➤");

        inputBar.addView(input);
        inputBar.addView(btnEnviar);
        root.addView(inputBar);

        // ================= OBSERVER (SOLO UNO) =================
        viewModel.getMensajes().observe(getViewLifecycleOwner(), lista -> {
            renderMensajes(lista);
        });

        // ================= CARGA INICIAL =================
        viewModel.cargarMensajes(idConversacion);
        viewModel.conectarWebSocket(idConversacion);

        // ================= ENVIAR =================
        Runnable enviar = () -> {
            String texto = input.getText().toString().trim();

            if (!texto.isEmpty()) {
                viewModel.enviarMensaje(emailPropio, idConversacion, texto);
                input.setText("");
            }
        };

        btnEnviar.setOnClickListener(v -> enviar.run());

        input.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviar.run();
                return true;
            }
            return false;
        });

        return root;
    }

    // ================= RENDER =================
    private void renderMensajes(List<MensajeChat> mensajes) {

        contenedorMensajes.removeAllViews();

        if (mensajes == null || mensajes.isEmpty()) {
            TextView vacio = new TextView(getContext());
            vacio.setText("No hay mensajes");
            contenedorMensajes.addView(vacio);
            return;
        }

        for (MensajeChat m : mensajes) {
            contenedorMensajes.addView(crearMensaje(m));
        }

        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
    }

    private View crearMensaje(MensajeChat m) {

        boolean esPropio = id_usuario.equals(m.getIdUsuario());

        LinearLayout fila = new LinearLayout(getContext());
        fila.setGravity(esPropio ? Gravity.END : Gravity.START);

        TextView burbuja = new TextView(getContext());
        burbuja.setText(m.getContenido());

        fila.addView(burbuja);
        return fila;
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.desconectar();
    }
}