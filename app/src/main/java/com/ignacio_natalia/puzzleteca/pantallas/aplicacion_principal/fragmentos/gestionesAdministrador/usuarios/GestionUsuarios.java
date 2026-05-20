package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.usuarios.UsuarioDialogFragment;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.ArrayList;
import java.util.List;

public class GestionUsuarios extends Fragment {

    private GestionUsuarioViewModel viewModel;
    private LinearLayout contenedor;
    private EditText buscador;

    private List<Usuario> listaTodos = new ArrayList<>();
    private String filtroRol = null; // null = todos

    private TextView chipTodos, chipUsuario, chipAdmin, chipBloqueado;

    // ══════════════════════════════════════════════════════════════════════
    //  onCreateView
    // ══════════════════════════════════════════════════════════════════════
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        LinearLayout root = new LinearLayout(requireContext());
        root.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft),
                        ContextCompat.getColor(requireContext(), R.color.jungle_green)
                }
        );

        root.setBackground(gradient);

        // Cabecera
        root.addView(crearCabecera());

        // Buscador
        root.addView(crearBuscador());

        // Chips de filtro
        LinearLayout filtrosRow = crearFiltros();
        LinearLayout.LayoutParams filtrosLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        filtrosLP.setMargins(dp(16), 0, dp(16), dp(8));
        filtrosRow.setLayoutParams(filtrosLP);
        root.addView(filtrosRow);

        // Lista con scroll
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f));
        scroll.setFillViewport(true);

        contenedor = new LinearLayout(requireContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setPadding(dp(16), dp(8), dp(16), dp(24));
        scroll.addView(contenedor);
        root.addView(scroll);

        // ViewModel
        viewModel = new ViewModelProvider(this).get(GestionUsuarioViewModel.class);

        viewModel.getUsuarios().observe(getViewLifecycleOwner(), lista -> {
            listaTodos = lista != null ? lista : new ArrayList<>();
            aplicarFiltros();
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());

        String token = GestorSesion.obtenerToken(requireContext());
        String email = GestorSesion.obtenerEmail(requireContext());
        viewModel.cargarUsuarios(token, email);

        return root;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Cabecera
    // ══════════════════════════════════════════════════════════════════════
    private View crearCabecera() {
        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(16), dp(20), dp(16), dp(12));

        TextView titulo = new TextView(requireContext());
        titulo.setText("👥  Gestión de Usuarios");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_teal_dark));
        titulo.setGravity(Gravity.START);
        header.addView(titulo);

        TextView subtitulo = new TextView(requireContext());
        subtitulo.setText("Administra los roles y accesos de los usuarios");
        subtitulo.setTextSize(12);
        subtitulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        subtitulo.setPadding(0, dp(2), 0, 0);
        header.addView(subtitulo);

        return header;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Buscador
    // ══════════════════════════════════════════════════════════════════════
    private View crearBuscador() {
        LinearLayout wrap = new LinearLayout(requireContext());
        wrap.setOrientation(LinearLayout.HORIZONTAL);
        wrap.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams wrapLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapLP.setMargins(dp(16), dp(8), dp(16), dp(8));
        wrap.setLayoutParams(wrapLP);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        bg.setCornerRadius(dp(24));
        bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_teal_soft));
        wrap.setBackground(bg);
        wrap.setPadding(dp(14), dp(10), dp(14), dp(10));

        TextView lupa = new TextView(requireContext());
        lupa.setText("🔍");
        lupa.setTextSize(16);
        lupa.setPadding(0, 0, dp(8), 0);
        wrap.addView(lupa);

        buscador = new EditText(requireContext());
        buscador.setHint("Buscar por nombre, apellido o email...");
        buscador.setHintTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        buscador.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        buscador.setTextSize(14);
        buscador.setBackground(null);
        buscador.setSingleLine(true);
        buscador.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        buscador.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { aplicarFiltros(); }
            @Override public void afterTextChanged(Editable s) {}
        });
        wrap.addView(buscador);

        return wrap;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Chips de filtro
    // ══════════════════════════════════════════════════════════════════════
    private LinearLayout crearFiltros() {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        chipTodos     = crearChip("Todos",      null,        true);
        chipUsuario   = crearChip("Usuario",    "Usuario",   false);
        chipAdmin     = crearChip("Admin",      "Admin",     false);
        chipBloqueado = crearChip("Bloqueado",  "Bloqueado", false);

        row.addView(chipTodos);
        row.addView(chipUsuario);
        row.addView(chipAdmin);
        row.addView(chipBloqueado);

        return row;
    }

    private TextView crearChip(String etiqueta, String rolFiltro, boolean activo) {
        TextView chip = new TextView(requireContext());
        chip.setText(etiqueta);
        chip.setTextSize(12);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setPadding(dp(14), dp(6), dp(14), dp(6));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, dp(8), 0);
        chip.setLayoutParams(lp);
        aplicarEstiloChip(chip, activo, rolFiltro);

        chip.setOnClickListener(v -> {
            filtroRol = rolFiltro;
            aplicarEstiloChip(chipTodos,     filtroRol == null,               null);
            aplicarEstiloChip(chipUsuario,   "Usuario".equals(filtroRol),     "Usuario");
            aplicarEstiloChip(chipAdmin,     "Admin".equals(filtroRol),       "Admin");
            aplicarEstiloChip(chipBloqueado, "Bloqueado".equals(filtroRol),   "Bloqueado");
            aplicarFiltros();
        });

        return chip;
    }

    private void aplicarEstiloChip(TextView chip, boolean activo, String rol) {
        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(dp(20));
        int colorTexto;

        if (activo) {
            int colorFondo;
            if ("Admin".equals(rol)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_admin_indigo);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else if ("Bloqueado".equals(rol)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else if ("Usuario".equals(rol)) {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_teal);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            } else {
                colorFondo = ContextCompat.getColor(requireContext(), R.color.app_teal_dark);
                colorTexto = ContextCompat.getColor(requireContext(), R.color.white);
            }
            bg.setColor(colorFondo);
        } else {
            bg.setColor(ContextCompat.getColor(requireContext(), R.color.app_fondo_pagina));
            bg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_borde_gris));
            colorTexto = ContextCompat.getColor(requireContext(), R.color.app_subtexto);
        }

        chip.setBackground(bg);
        chip.setTextColor(colorTexto);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Filtrado y renderizado
    // ══════════════════════════════════════════════════════════════════════
    private void aplicarFiltros() {
        String query = buscador != null ? buscador.getText().toString().trim().toLowerCase() : "";

        List<Usuario> filtrados = new ArrayList<>();
        for (Usuario u : listaTodos) {
            boolean pasaRol = filtroRol == null
                    || (u.getTipoUsuario() != null && filtroRol.equals(u.getTipoUsuario().name()));
            boolean pasaBusqueda = query.isEmpty()
                    || (u.getNombre()   != null && u.getNombre().toLowerCase().contains(query))
                    || (u.getApellido() != null && u.getApellido().toLowerCase().contains(query))
                    || (u.getEmail()    != null && u.getEmail().toLowerCase().contains(query));
            if (pasaRol && pasaBusqueda) filtrados.add(u);
        }

        contenedor.removeAllViews();
        if (filtrados.isEmpty()) {
            mostrarVacio();
        } else {
            for (Usuario u : filtrados) {
                contenedor.addView(crearTarjetaUsuario(u));
                espacio(contenedor, dp(10));
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Tarjeta de usuario — layout horizontal compacto
    // ══════════════════════════════════════════════════════════════════════
    @SuppressLint("SetTextI18n")
    private View crearTarjetaUsuario(Usuario usuario) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        cardBg.setCornerRadius(dp(14));
        cardBg.setStroke(dp(1), ContextCompat.getColor(requireContext(), R.color.app_borde_light));
        card.setBackground(cardBg);

        // ── Avatar circular con inicial ───────────────────────────────────
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "?";
        String inicial = nombre.substring(0, 1).toUpperCase();
        int[] colores = coloresPorRol(usuario.getTipoUsuario());

        TextView avatar = new TextView(requireContext());
        avatar.setText(inicial);
        avatar.setTextSize(18);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(ContextCompat.getColor(requireContext(), colores[0]));
        avatar.setGravity(Gravity.CENTER);
        int avSize = dp(48);
        LinearLayout.LayoutParams avLP = new LinearLayout.LayoutParams(avSize, avSize);
        avLP.setMargins(0, 0, dp(12), 0);
        avatar.setLayoutParams(avLP);
        GradientDrawable avBg = new GradientDrawable();
        avBg.setShape(GradientDrawable.OVAL);
        avBg.setColor(ContextCompat.getColor(requireContext(), colores[1]));
        avatar.setBackground(avBg);
        card.addView(avatar);

        // ── Columna de info ───────────────────────────────────────────────
        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Fila 1: nombre completo
        String nombreCompleto = nombre
                + (usuario.getApellido() != null ? " " + usuario.getApellido() : "");
        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText(nombreCompleto);
        tvNombre.setTextSize(14);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto_dark));
        tvNombre.setMaxLines(1);
        tvNombre.setEllipsize(android.text.TextUtils.TruncateAt.END);
        info.addView(tvNombre);

        // Fila 2: email
        if (usuario.getEmail() != null) {
            TextView tvEmail = new TextView(requireContext());
            tvEmail.setText("✉ " + usuario.getEmail());
            tvEmail.setTextSize(12);
            tvEmail.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
            tvEmail.setMaxLines(1);
            tvEmail.setEllipsize(android.text.TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams emailLP = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            emailLP.topMargin = dp(2);
            tvEmail.setLayoutParams(emailLP);
            info.addView(tvEmail);
        }

        // Fila 3: chip de rol
        TextView chipRol = crearBadgeRol(usuario.getTipoUsuario());
        LinearLayout.LayoutParams chipLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chipLP.topMargin = dp(5);
        chipRol.setLayoutParams(chipLP);
        info.addView(chipRol);

        card.addView(info);

        // ── Columna derecha: botón ciclo de rol + botón ver ───────────────
        LinearLayout derecha = new LinearLayout(requireContext());
        derecha.setOrientation(LinearLayout.VERTICAL);
        derecha.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams derechaLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        derechaLP.setMargins(dp(8), 0, 0, 0);
        derecha.setLayoutParams(derechaLP);

        // Botón ver detalle → abre UsuarioDialogFragment
        TextView btnVer = new TextView(requireContext());
        btnVer.setText("👁");
        btnVer.setTextSize(18);
        btnVer.setGravity(Gravity.CENTER);
        GradientDrawable btnVerBg = new GradientDrawable();
        btnVerBg.setColor(ContextCompat.getColor(requireContext(), R.color.app_teal_soft));
        btnVerBg.setCornerRadius(dp(8));
        btnVer.setBackground(btnVerBg);
        btnVer.setPadding(dp(10), dp(8), dp(10), dp(8));
        btnVer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        btnVer.setOnClickListener(v -> {
            UsuarioDialogFragment dialog = UsuarioDialogFragment.newInstance(usuario);
            dialog.setOnUsuarioModificadoListener(() ->
                    viewModel.cargarUsuarios(
                            GestorSesion.obtenerToken(requireContext()),
                            GestorSesion.obtenerEmail(requireContext())));
            dialog.show(getChildFragmentManager(), "usuario_dialog");
        });

        derecha.addView(btnVer);
        card.addView(derecha);

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Helpers de badge de rol
    // ══════════════════════════════════════════════════════════════════════
    private TextView crearBadgeRol(Usuario.TipoUsuario tipo) {
        TextView badge = new TextView(requireContext());
        actualizarBadgeRol(badge, tipo);
        return badge;
    }

    private void actualizarBadgeRol(TextView badge, Usuario.TipoUsuario tipo) {
        String texto;
        int colorTexto, colorFondo;

        if (tipo == null || tipo == Usuario.TipoUsuario.Usuario) {
            texto = "🧩 Usuario";
            colorTexto = ContextCompat.getColor(requireContext(), R.color.app_teal_dark);
            colorFondo = ContextCompat.getColor(requireContext(), R.color.app_teal_soft);
        } else if (tipo == Usuario.TipoUsuario.Admin) {
            texto = "🛡️ Admin";
            colorTexto = ContextCompat.getColor(requireContext(), R.color.app_admin_indigo);
            colorFondo = ContextCompat.getColor(requireContext(), R.color.app_admin_indigo_soft);
        } else {
            texto = "🚫 Bloqueado";
            colorTexto = ContextCompat.getColor(requireContext(), R.color.app_peligro_dark);
            colorFondo = ContextCompat.getColor(requireContext(), R.color.app_peligro_bg);
        }

        badge.setText(texto);
        badge.setTextSize(11);
        badge.setTypeface(null, Typeface.BOLD);
        badge.setTextColor(colorTexto);
        badge.setPadding(dp(8), dp(3), dp(8), dp(3));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(colorFondo);
        bg.setCornerRadius(dp(12));
        badge.setBackground(bg);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Lógica de ciclo de roles
    // ══════════════════════════════════════════════════════════════════════
    private Usuario.TipoUsuario siguienteRol(Usuario.TipoUsuario actual) {
        if (actual == null) return Usuario.TipoUsuario.Admin;
        switch (actual) {
            case Usuario:   return Usuario.TipoUsuario.Admin;
            case Admin:     return Usuario.TipoUsuario.Bloqueado;
            default:        return Usuario.TipoUsuario.Usuario;
        }
    }

    private String emojiRol(Usuario.TipoUsuario tipo) {
        if (tipo == null) return "🧩";
        switch (tipo) {
            case Admin:     return "🛡️";
            case Bloqueado: return "🚫";
            default:        return "🧩";
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Colores por rol [color texto, color fondo] como R.color IDs
    // ══════════════════════════════════════════════════════════════════════
    private int[] coloresPorRol(Usuario.TipoUsuario tipo) {
        if (tipo == null) return new int[]{R.color.app_teal_dark, R.color.app_teal_soft};
        switch (tipo) {
            case Admin:     return new int[]{R.color.app_admin_indigo, R.color.app_admin_indigo_soft};
            case Bloqueado: return new int[]{R.color.app_peligro_dark, R.color.app_peligro_bg};
            default:        return new int[]{R.color.app_teal_dark, R.color.app_teal_soft};
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Estado vacío
    // ══════════════════════════════════════════════════════════════════════
    private void mostrarVacio() {
        contenedor.removeAllViews();

        LinearLayout wrap = new LinearLayout(requireContext());
        wrap.setOrientation(LinearLayout.VERTICAL);
        wrap.setGravity(Gravity.CENTER);
        wrap.setPadding(dp(32), dp(48), dp(32), dp(48));
        wrap.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView emoji = new TextView(requireContext());
        emoji.setText("👤");
        emoji.setTextSize(48);
        emoji.setGravity(Gravity.CENTER);
        wrap.addView(emoji);

        TextView msg = new TextView(requireContext());
        msg.setText("No hay usuarios que mostrar");
        msg.setTextSize(15);
        msg.setTypeface(null, Typeface.BOLD);
        msg.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto));
        msg.setGravity(Gravity.CENTER);
        msg.setPadding(0, dp(12), 0, 0);
        wrap.addView(msg);

        TextView hint = new TextView(requireContext());
        hint.setText("Prueba a cambiar el filtro o la búsqueda");
        hint.setTextSize(13);
        hint.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        hint.setGravity(Gravity.CENTER);
        hint.setPadding(0, dp(4), 0, 0);
        wrap.addView(hint);

        contenedor.addView(wrap);
    }

    // ══════════════════════════════════════════════════════════════════════
    //  Utilidades
    // ══════════════════════════════════════════════════════════════════════
    private void espacio(LinearLayout layout, int px) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, px));
        layout.addView(v);
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}