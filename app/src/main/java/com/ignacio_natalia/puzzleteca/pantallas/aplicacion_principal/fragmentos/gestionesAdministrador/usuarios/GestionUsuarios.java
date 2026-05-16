package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.usuarios.UsuarioDialogFragment;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class GestionUsuarios extends Fragment {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final String C_TEAL      = "#2E7D6E";
    private static final String C_TEAL_SOFT = "#E0F2F1";
    private static final String C_ADMIN     = "#5C6BC0";
    private static final String C_ADMIN_SF  = "#E8EAF6";
    private static final String C_BLOQ      = "#E53935";
    private static final String C_BLOQ_SF   = "#FFEBEE";
    private static final String C_TEXTO     = "#37474F";
    private static final String C_SUBTEXTO  = "#78909C";
    private static final String C_BORDE     = "#ECEFF1";

    private GestionUsuarioViewModel viewModel;
    private LinearLayout contenedorUsuarios;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        FrameLayout root = new FrameLayout(requireContext());

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(16), dp(16), dp(24));

        // ── Cabecera ──────────────────────────────────────────────────────────
        LinearLayout cab = new LinearLayout(requireContext());
        cab.setOrientation(LinearLayout.HORIZONTAL);
        cab.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams cabLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cabLP.bottomMargin = dp(16);
        cab.setLayoutParams(cabLP);

        LinearLayout cabTextos = new LinearLayout(requireContext());
        cabTextos.setOrientation(LinearLayout.VERTICAL);
        cabTextos.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView titulo = new TextView(requireContext());
        titulo.setText("Gestión de usuarios");
        titulo.setTextSize(22);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor(C_TEXTO));

        TextView tvContador = new TextView(requireContext());
        tvContador.setTextSize(13);
        tvContador.setTextColor(Color.parseColor(C_SUBTEXTO));

        cabTextos.addView(titulo);
        cabTextos.addView(tvContador);
        cab.addView(cabTextos);

        TextView emoji = new TextView(requireContext());
        emoji.setText("👥");
        emoji.setTextSize(26);
        cab.addView(emoji);

        layout.addView(cab);

        // ── Contenedor de tarjetas ────────────────────────────────────────────
        contenedorUsuarios = new LinearLayout(requireContext());
        contenedorUsuarios.setOrientation(LinearLayout.VERTICAL);
        layout.addView(contenedorUsuarios);

        // ── ViewModel ─────────────────────────────────────────────────────────
        viewModel = new ViewModelProvider(this).get(GestionUsuarioViewModel.class);

        viewModel.getUsuarios().observe(getViewLifecycleOwner(), usuarios -> {
            renderizarUsuarios(usuarios);
            if (usuarios != null) {
                tvContador.setText(usuarios.size() + (usuarios.size() == 1 ? " usuario" : " usuarios"));
            }
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());

        String token = GestorSesion.obtenerToken(requireContext());
        String email = GestorSesion.obtenerEmail(requireContext());
        viewModel.cargarUsuarios(token, email);

        scroll.addView(layout);
        root.addView(scroll);
        return root;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Renderizado
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private void renderizarUsuarios(List<Usuario> usuarios) {
        contenedorUsuarios.removeAllViews();

        if (usuarios == null || usuarios.isEmpty()) {
            LinearLayout vacio = new LinearLayout(requireContext());
            vacio.setOrientation(LinearLayout.VERTICAL);
            vacio.setGravity(Gravity.CENTER_HORIZONTAL);
            vacio.setPadding(dp(32), dp(60), dp(32), dp(40));
            vacio.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView tvEmoji = new TextView(requireContext());
            tvEmoji.setText("👤");
            tvEmoji.setTextSize(40);
            tvEmoji.setGravity(Gravity.CENTER);
            tvEmoji.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            TextView tvMsg = new TextView(requireContext());
            tvMsg.setText("No hay usuarios registrados");
            tvMsg.setTextSize(16);
            tvMsg.setTextColor(Color.parseColor(C_SUBTEXTO));
            tvMsg.setGravity(Gravity.CENTER);
            tvMsg.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

            vacio.addView(tvEmoji);
            vacio.addView(tvMsg);
            contenedorUsuarios.addView(vacio);
            return;
        }

        for (Usuario u : usuarios) {
            contenedorUsuarios.addView(crearTarjetaUsuario(u));
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Tarjeta de usuario
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjetaUsuario(Usuario usuario) {
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setGravity(Gravity.CENTER_VERTICAL);
        tarjeta.setPadding(dp(14), dp(14), dp(12), dp(14));
        tarjeta.setElevation(dp(2));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(10);
        tarjeta.setLayoutParams(lp);

        int r = dp(14);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(r);
        bg.setStroke(dp(1), Color.parseColor(C_BORDE));
        tarjeta.setBackground(bg);
        tarjeta.setClipToOutline(true);
        tarjeta.setOutlineProvider(new android.view.ViewOutlineProvider() {
            @Override
            public void getOutline(View v, android.graphics.Outline o) {
                o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), r);
            }
        });

        // ── Avatar circular ───────────────────────────────────────────────────
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "?";
        String inicial = nombre.substring(0, 1).toUpperCase();
        String[] colores = colorPorTipo(usuario.getTipoUsuario());

        TextView avatar = new TextView(requireContext());
        avatar.setText(inicial);
        avatar.setTextSize(18);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.parseColor(colores[0]));
        avatar.setGravity(Gravity.CENTER);
        int avSize = dp(44);
        LinearLayout.LayoutParams avLP = new LinearLayout.LayoutParams(avSize, avSize);
        avLP.setMargins(0, 0, dp(12), 0);
        avatar.setLayoutParams(avLP);
        GradientDrawable avBg = new GradientDrawable();
        avBg.setShape(GradientDrawable.OVAL);
        avBg.setColor(Color.parseColor(colores[1]));
        avatar.setBackground(avBg);

        // ── Columna central: nombre + chip de rol ─────────────────────────────
        LinearLayout colInfo = new LinearLayout(requireContext());
        colInfo.setOrientation(LinearLayout.VERTICAL);
        colInfo.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        String nombreCompleto = nombre + (usuario.getApellido() != null
                ? " " + usuario.getApellido() : "");
        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText(nombreCompleto);
        tvNombre.setTextSize(15);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(Color.parseColor(C_TEXTO));
        tvNombre.setMaxLines(1);
        tvNombre.setEllipsize(android.text.TextUtils.TruncateAt.END);

        // Mini chip de rol debajo del nombre
        TextView chipRol = new TextView(requireContext());
        chipRol.setText(emojiTipo(usuario.getTipoUsuario())
                + " " + labelTipo(usuario.getTipoUsuario()));
        chipRol.setTextSize(11);
        chipRol.setTypeface(null, Typeface.BOLD);
        chipRol.setTextColor(Color.parseColor(colores[0]));
        chipRol.setPadding(dp(8), dp(3), dp(8), dp(3));
        LinearLayout.LayoutParams chipLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chipLP.topMargin = dp(4);
        chipRol.setLayoutParams(chipLP);
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(Color.parseColor(colores[1]));
        chipBg.setCornerRadius(dp(20));
        chipRol.setBackground(chipBg);

        colInfo.addView(tvNombre);
        colInfo.addView(chipRol);

        // ── Flecha derecha ────────────────────────────────────────────────────
        TextView flecha = new TextView(requireContext());
        flecha.setText("›");
        flecha.setTextSize(28);
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setTextColor(Color.parseColor("#A5D6A7"));
        flecha.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams flechaLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        flechaLP.gravity = Gravity.CENTER_VERTICAL;
        flechaLP.setMargins(dp(6), 0, dp(2), 0);
        flecha.setLayoutParams(flechaLP);

        tarjeta.addView(avatar);
        tarjeta.addView(colInfo);
        tarjeta.addView(flecha);

        // Click → abrir UsuarioDialogFragment
        tarjeta.setOnClickListener(v -> {
            UsuarioDialogFragment dialog =
                    UsuarioDialogFragment.newInstance(usuario);
            dialog.setOnUsuarioModificadoListener(() ->
                    viewModel.cargarUsuarios(
                            GestorSesion.obtenerToken(requireContext()),
                            GestorSesion.obtenerEmail(requireContext())));
            dialog.show(getChildFragmentManager(), "usuario_dialog");
        });

        return tarjeta;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers
    // ══════════════════════════════════════════════════════════════════════════

    private String[] colorPorTipo(Usuario.TipoUsuario tipo) {
        if (tipo == null) return new String[]{C_TEAL, C_TEAL_SOFT};
        switch (tipo) {
            case Admin:    return new String[]{C_ADMIN, C_ADMIN_SF};
            case Bloqueado:return new String[]{C_BLOQ,  C_BLOQ_SF};
            default:       return new String[]{C_TEAL,  C_TEAL_SOFT};
        }
    }

    private String emojiTipo(Usuario.TipoUsuario tipo) {
        if (tipo == null) return "👤";
        switch (tipo) {
            case Admin:    return "🛡️";
            case Bloqueado:return "🚫";
            default:       return "🧩";
        }
    }

    private String labelTipo(Usuario.TipoUsuario tipo) {
        if (tipo == null) return "Usuario";
        switch (tipo) {
            case Admin:    return "Admin";
            case Bloqueado:return "Bloqueado";
            default:       return "Usuario";
        }
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}