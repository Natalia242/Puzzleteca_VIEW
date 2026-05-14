package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ignacio_natalia.puzzleteca.modelos.ActualizarUsuarioRequest;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfil extends Fragment {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final String C_TEAL       = "#2E7D6E";
    private static final String C_TEAL_SOFT  = "#E0F2F1";
    private static final String C_TEXTO      = "#37474F";
    private static final String C_SUBTEXTO   = "#78909C";
    private static final String C_BORDE      = "#ECEFF1";
    private static final String C_PELIGRO    = "#E53935";
    private static final String C_PELIGRO_BG = "#FFEBEE";
    private static final String C_PELIGRO_BD = "#EF9A9A";
    private static final String C_INPUT_BG   = "#F8FAFB";

    private UsuarioRepositorio repositorio;

    // ── Referencias a campos (necesarias para validar entre secciones) ────────
    private EditText campoNombre, campoApellido;
    private EditText campoContrasenaActual, campoContrasenaNueva, campoContrasenaConfirm;
    private EditText campoCodigo;
    private LinearLayout seccionCodigo;
    private Button btnGuardarNombre, btnSolicitarCodigo, btnCambiarContrasena;

    // ══════════════════════════════════════════════════════════════════════════
    //  Ciclo de vida
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle savedInstanceState) {

        repositorio = new UsuarioRepositorio();

        String nombre = GestorSesion.obtenerNombre(requireContext());
        String email  = GestorSesion.obtenerEmail(requireContext());
        String rol    = GestorSesion.obtenerRol(requireContext());

        // ── Raíz ─────────────────────────────────────────────────────────────
        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(0), dp(16), dp(32));
        scroll.addView(layout);

        // ── Cabecera con avatar ───────────────────────────────────────────────
        layout.addView(crearCabecera(nombre, email, rol));

        // ── Tarjeta info de sesión (solo lectura) ─────────────────────────────
        layout.addView(crearSeccionLabel("📋 Información de la cuenta"));
        layout.addView(crearTarjetaInfo(email, rol));
        espaciado(layout, 8);

        // ── Sección: Nombre y apellido ────────────────────────────────────────
        layout.addView(crearSeccionLabel("👤 Datos personales"));
        layout.addView(crearSeccionNombre(email));
        espaciado(layout, 8);

        // ── Sección: Cambiar contraseña ───────────────────────────────────────
        layout.addView(crearSeccionLabel("🔑 Cambiar contraseña"));
        layout.addView(crearSeccionContrasena(email));
        espaciado(layout, 8);

        // ── Zona de peligro ───────────────────────────────────────────────────
        layout.addView(crearSeccionLabel("⚠️ Zona de peligro"));
        layout.addView(crearZonaPeligro());

        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bloque: Cabecera con avatar
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearCabecera(String nombre, String email, String rol) {
        // Fondo teal degradado para el header
        LinearLayout header = new LinearLayout(requireContext());
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(dp(24), dp(32), dp(24), dp(28));
        LinearLayout.LayoutParams headerLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headerLP.setMargins(-dp(16), 0, -dp(16), dp(20));
        header.setLayoutParams(headerLP);

        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{Color.parseColor("#2E7D6E"), Color.parseColor("#26A69A")});
        header.setBackground(headerBg);

        // Círculo avatar con inicial
        TextView avatar = new TextView(requireContext());
        String inicial = (nombre != null && !nombre.isEmpty())
                ? nombre.substring(0, 1).toUpperCase() : "U";
        avatar.setText(inicial);
        avatar.setTextSize(36);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.parseColor(C_TEAL));
        avatar.setGravity(Gravity.CENTER);
        int avatarSize = dp(86);
        LinearLayout.LayoutParams avLP = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avLP.bottomMargin = dp(14);
        avatar.setLayoutParams(avLP);
        GradientDrawable avBg = new GradientDrawable();
        avBg.setShape(GradientDrawable.OVAL);
        avBg.setColor(Color.WHITE);
        avBg.setStroke(dp(3), Color.parseColor("#B2DFDB"));
        avatar.setBackground(avBg);

        // Nombre
        TextView tvNombre = new TextView(requireContext());
        tvNombre.setText(nombre != null ? nombre : "Usuario");
        tvNombre.setTextSize(20);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(Color.WHITE);
        tvNombre.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nomLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLP.bottomMargin = dp(4);
        tvNombre.setLayoutParams(nomLP);

        // Email
        TextView tvEmail = new TextView(requireContext());
        tvEmail.setText(email != null ? email : "");
        tvEmail.setTextSize(13);
        tvEmail.setTextColor(Color.parseColor("#B2DFDB"));
        tvEmail.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams emailLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        emailLP.bottomMargin = dp(12);
        tvEmail.setLayoutParams(emailLP);

        // Chip de rol
        TextView chipRol = new TextView(requireContext());
        chipRol.setText(rolEmoji(rol) + "  " + (rol != null ? rol : "Usuario"));
        chipRol.setTextSize(12);
        chipRol.setTypeface(null, Typeface.BOLD);
        chipRol.setTextColor(Color.parseColor(C_TEAL));
        chipRol.setPadding(dp(14), dp(5), dp(14), dp(5));
        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(Color.WHITE);
        chipBg.setCornerRadius(dp(20));
        chipRol.setBackground(chipBg);

        header.addView(avatar);
        header.addView(tvNombre);
        header.addView(tvEmail);
        header.addView(chipRol);
        return header;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bloque: Tarjeta de info de sesión (solo lectura)
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjetaInfo(String email, String rol) {
        LinearLayout card = crearTarjeta();
        card.addView(crearFilaInfo("📧", "Email", email != null ? email : "—"));
        card.addView(crearDivisorSutil());
        card.addView(crearFilaInfo("🎭", "Rol", rol != null ? rol : "Usuario"));
        card.addView(crearDivisorSutil());
        card.addView(crearFilaInfo("🆔", "ID de usuario",
                String.valueOf(GestorSesion.obtenerId_usuario(requireContext()))));
        return card;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearFilaInfo(String emoji, String etiqueta, String valor) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(16), dp(13), dp(16), dp(13));

        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(16);
        LinearLayout.LayoutParams emojiLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        emojiLP.setMargins(0, 0, dp(12), 0);
        tvEmoji.setLayoutParams(emojiLP);

        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText(etiqueta);
        tvLabel.setTextSize(11);
        tvLabel.setTextColor(Color.parseColor(C_SUBTEXTO));
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setLetterSpacing(0.05f);

        TextView tvValor = new TextView(requireContext());
        tvValor.setText(valor);
        tvValor.setTextSize(14);
        tvValor.setTextColor(Color.parseColor(C_TEXTO));
        tvValor.setTypeface(null, Typeface.BOLD);

        col.addView(tvLabel);
        col.addView(tvValor);
        fila.addView(tvEmoji);
        fila.addView(col);
        return fila;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bloque: Nombre y apellido
    // ══════════════════════════════════════════════════════════════════════════

    private LinearLayout crearSeccionNombre(String email) {
        LinearLayout card = crearTarjeta();
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView desc = new TextView(requireContext());
        desc.setText("Actualiza cómo te llamas en la app.");
        desc.setTextSize(13);
        desc.setTextColor(Color.parseColor(C_SUBTEXTO));
        LinearLayout.LayoutParams descLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descLP.bottomMargin = dp(14);
        desc.setLayoutParams(descLP);
        card.addView(desc);

        campoNombre   = crearCampo("Nuevo nombre", "👤", false);
        campoApellido = crearCampo("Nuevo apellido", "👤", false);
        card.addView(crearLabelCampo("Nombre"));
        card.addView(campoNombre);
        espaciado(card, 10);
        card.addView(crearLabelCampo("Apellido"));
        card.addView(campoApellido);
        espaciado(card, 16);

        btnGuardarNombre = crearBotonAccion("💾  Guardar cambios", C_TEAL, C_TEAL_SOFT, C_TEAL);
        btnGuardarNombre.setOnClickListener(v -> guardarNombreApellido(email));
        card.addView(btnGuardarNombre);
        return card;
    }

    private void guardarNombreApellido(String email) {
        String nuevoNombre   = campoNombre.getText().toString().trim();
        String nuevoApellido = campoApellido.getText().toString().trim();

        if (nuevoNombre.isEmpty() && nuevoApellido.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Rellena al menos el nombre o el apellido.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (email == null) {
            Toast.makeText(requireContext(), "No se pudo obtener tu sesión.", Toast.LENGTH_SHORT).show();
            return;
        }

        setBotonCargando(btnGuardarNombre, true, "Guardando…");
        boolean nombreRelleno   = !nuevoNombre.isEmpty();
        boolean apellidoRelleno = !nuevoApellido.isEmpty();

        if (nombreRelleno) {
            actualizarAtributo(email, "nombre", nuevoNombre, apellidoRelleno ? () ->
                            actualizarAtributo(email, "apellido", nuevoApellido,
                                    () -> onGuardadoExito("¡Datos actualizados!", btnGuardarNombre, "💾  Guardar cambios"),
                                    btnGuardarNombre, "💾  Guardar cambios")
                            : () -> onGuardadoExito("¡Datos actualizados!", btnGuardarNombre, "💾  Guardar cambios"),
                    btnGuardarNombre, "💾  Guardar cambios");
        } else {
            actualizarAtributo(email, "apellido", nuevoApellido,
                    () -> onGuardadoExito("¡Apellido actualizado!", btnGuardarNombre, "💾  Guardar cambios"),
                    btnGuardarNombre, "💾  Guardar cambios");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bloque: Cambiar contraseña
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearSeccionContrasena(String email) {
        LinearLayout card = crearTarjeta();
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        TextView desc = new TextView(requireContext());
        desc.setText("Te enviaremos un código a tu email para verificar el cambio.");
        desc.setTextSize(13);
        desc.setTextColor(Color.parseColor(C_SUBTEXTO));
        LinearLayout.LayoutParams descLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        descLP.bottomMargin = dp(14);
        desc.setLayoutParams(descLP);
        card.addView(desc);

        // Paso 1: solicitar código
        btnSolicitarCodigo = crearBotonAccion(
                "📨  Solicitar código de verificación", "#5C6BC0", "#E8EAF6", "#5C6BC0");
        btnSolicitarCodigo.setOnClickListener(v -> solicitarCodigoCambioContrasena(email));
        card.addView(btnSolicitarCodigo);
        espaciado(card, 14);

        // Paso 2 (oculto hasta que llegue el código)
        seccionCodigo = new LinearLayout(requireContext());
        seccionCodigo.setOrientation(LinearLayout.VERTICAL);
        seccionCodigo.setVisibility(View.GONE);

        View separador = new View(requireContext());
        LinearLayout.LayoutParams sepLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        sepLP.setMargins(0, 0, 0, dp(14));
        separador.setLayoutParams(sepLP);
        separador.setBackgroundColor(Color.parseColor(C_BORDE));
        seccionCodigo.addView(separador);

        TextView tvPaso2 = new TextView(requireContext());
        tvPaso2.setText("✉️ Introduce el código recibido y tu nueva contraseña:");
        tvPaso2.setTextSize(13);
        tvPaso2.setTypeface(null, Typeface.BOLD);
        tvPaso2.setTextColor(Color.parseColor(C_TEXTO));
        LinearLayout.LayoutParams paso2LP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paso2LP.bottomMargin = dp(12);
        tvPaso2.setLayoutParams(paso2LP);
        seccionCodigo.addView(tvPaso2);

        campoCodigo            = crearCampo("Código de verificación", "🔢", false);
        campoCodigo.setInputType(InputType.TYPE_CLASS_NUMBER);
        campoContrasenaNueva   = crearCampo("Nueva contraseña", "🔒", true);
        campoContrasenaConfirm = crearCampo("Confirmar contraseña", "🔒", true);

        seccionCodigo.addView(crearLabelCampo("Código"));
        seccionCodigo.addView(campoCodigo);
        espaciado(seccionCodigo, 10);
        seccionCodigo.addView(crearLabelCampo("Nueva contraseña"));
        seccionCodigo.addView(campoContrasenaNueva);
        espaciado(seccionCodigo, 10);
        seccionCodigo.addView(crearLabelCampo("Confirmar contraseña"));
        seccionCodigo.addView(campoContrasenaConfirm);
        espaciado(seccionCodigo, 14);

        btnCambiarContrasena = crearBotonAccion("🔑  Cambiar contraseña", C_TEAL, C_TEAL_SOFT, C_TEAL);
        btnCambiarContrasena.setOnClickListener(v -> cambiarContrasena(email));
        seccionCodigo.addView(btnCambiarContrasena);

        card.addView(seccionCodigo);
        return card;
    }

    private void solicitarCodigoCambioContrasena(String email) {
        if (email == null) return;
        setBotonCargando(btnSolicitarCodigo, true, "Enviando…");
        repositorio.solicitarCodigo(email, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setBotonCargando(btnSolicitarCodigo, false, "📨  Solicitar código de verificación");
                    if (r.isSuccessful()) {
                        seccionCodigo.setVisibility(View.VISIBLE);
                        Toast.makeText(requireContext(),
                                "Código enviado a " + email, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error al enviar el código (" + r.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setBotonCargando(btnSolicitarCodigo, false, "📨  Solicitar código de verificación");
                    Toast.makeText(requireContext(), "Error de red: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void cambiarContrasena(String email) {
        String codigo    = campoCodigo.getText().toString().trim();
        String nueva     = campoContrasenaNueva.getText().toString();
        String confirmar = campoContrasenaConfirm.getText().toString();

        if (codigo.isEmpty() || nueva.isEmpty() || confirmar.isEmpty()) {
            Toast.makeText(requireContext(), "Rellena todos los campos.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!nueva.equals(confirmar)) {
            Toast.makeText(requireContext(), "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
            campoContrasenaConfirm.setError("No coincide");
            return;
        }
        if (nueva.length() < 6) {
            Toast.makeText(requireContext(), "La contraseña debe tener al menos 6 caracteres.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        setBotonCargando(btnCambiarContrasena, true, "Cambiando…");
        repositorio.confirmarCambioContrasena(email, codigo, nueva, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setBotonCargando(btnCambiarContrasena, false, "🔑  Cambiar contraseña");
                    if (r.isSuccessful()) {
                        seccionCodigo.setVisibility(View.GONE);
                        campoCodigo.setText("");
                        campoContrasenaNueva.setText("");
                        campoContrasenaConfirm.setText("");
                        UtilidadesSesion.mostrarDialogoPersonalizado(
                                requireContext(), "✅", "Contraseña cambiada",
                                "Tu contraseña se ha actualizado correctamente.\nVuelve a iniciar sesión.",
                                "Cerrar sesión", C_TEAL,
                                () -> UtilidadesSesion.cerrarSesion(requireContext()));
                    } else {
                        String msg = r.code() == 400
                                ? "Código incorrecto o expirado."
                                : "Error al cambiar la contraseña (" + r.code() + ").";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setBotonCargando(btnCambiarContrasena, false, "🔑  Cambiar contraseña");
                    Toast.makeText(requireContext(), "Error de red: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Bloque: Zona de peligro
    // ══════════════════════════════════════════════════════════════════════════

    private LinearLayout crearZonaPeligro() {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));
        LinearLayout.LayoutParams cardLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLP.bottomMargin = dp(8);
        card.setLayoutParams(cardLP);
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(Color.parseColor(C_PELIGRO_BG));
        cardBg.setCornerRadius(dp(14));
        cardBg.setStroke(dp(1), Color.parseColor(C_PELIGRO_BD));
        card.setBackground(cardBg);

        TextView tvAviso = new TextView(requireContext());
        tvAviso.setText("⚠️  Las acciones de esta sección son permanentes y no se pueden deshacer.");
        tvAviso.setTextSize(13);
        tvAviso.setTextColor(Color.parseColor("#B71C1C"));
        LinearLayout.LayoutParams avisoLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        avisoLP.bottomMargin = dp(14);
        tvAviso.setLayoutParams(avisoLP);
        card.addView(tvAviso);

        Button btnEliminar = new Button(requireContext());
        btnEliminar.setText("🗑️  Eliminar mi cuenta");
        btnEliminar.setTextColor(Color.WHITE);
        btnEliminar.setTextSize(14);
        btnEliminar.setTypeface(null, Typeface.BOLD);
        btnEliminar.setAllCaps(false);
        btnEliminar.setPadding(0, dp(13), 0, dp(13));
        btnEliminar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable elimBg = new GradientDrawable();
        elimBg.setColor(Color.parseColor(C_PELIGRO));
        elimBg.setCornerRadius(dp(12));
        btnEliminar.setBackground(elimBg);

        btnEliminar.setOnClickListener(v ->
                UtilidadesSesion.mostrarDialogoEliminarCuenta(requireContext(),
                        () -> UtilidadesSesion.eliminarCuenta(requireContext(), null)));
        card.addView(btnEliminar);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica de red compartida
    // ══════════════════════════════════════════════════════════════════════════

    private void actualizarAtributo(String email, String atributo, String valor,
                                    Runnable onExito, Button boton, String textoBoton) {
        ActualizarUsuarioRequest req = new ActualizarUsuarioRequest(email, atributo, valor);
        repositorio.actualizarPerfil(req, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (r.isSuccessful()) {
                        if ("nombre".equals(atributo)) GestorSesion.guardarNombre(requireContext(), valor);
                        onExito.run();
                    } else {
                        setBotonCargando(boton, false, textoBoton);
                        String msg = r.code() == 409
                                ? "El " + atributo + " ya tiene ese valor."
                                : "Error al actualizar el " + atributo + " (" + r.code() + ").";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    setBotonCargando(boton, false, textoBoton);
                    Toast.makeText(requireContext(), "Error de red: " + t.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void onGuardadoExito(String mensaje, Button boton, String textoBoton) {
        if (!isAdded()) return;
        setBotonCargando(boton, false, textoBoton);
        campoNombre.setText("");
        campoApellido.setText("");
        UtilidadesSesion.mostrarDialogoPersonalizado(
                requireContext(), "✅", "¡Listo!",
                mensaje,
                "Aceptar", C_TEAL,
                () -> requireActivity().getSupportFragmentManager().popBackStack());
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers de UI
    // ══════════════════════════════════════════════════════════════════════════

    private LinearLayout crearTarjeta() {
        LinearLayout t = new LinearLayout(requireContext());
        t.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(8);
        t.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), Color.parseColor(C_BORDE));
        t.setBackground(bg);
        t.setElevation(dp(1));
        return t;
    }

    private TextView crearSeccionLabel(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(12);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor(C_SUBTEXTO));
        tv.setLetterSpacing(0.08f);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(dp(4), dp(4), 0, dp(6));
        tv.setLayoutParams(lp);
        return tv;
    }

    private TextView crearLabelCampo(String texto) {
        TextView tv = new TextView(requireContext());
        tv.setText(texto);
        tv.setTextSize(12);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(Color.parseColor(C_TEXTO));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(4);
        tv.setLayoutParams(lp);
        return tv;
    }

    private EditText crearCampo(String hint, String emoji, boolean esContrasena) {
        EditText campo = new EditText(requireContext());
        campo.setHint(hint);
        campo.setTextSize(14);
        campo.setTextColor(Color.parseColor(C_TEXTO));
        campo.setHintTextColor(Color.parseColor(C_SUBTEXTO));
        campo.setPadding(dp(14), dp(13), dp(14), dp(13));
        campo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        if (esContrasena) {
            campo.setInputType(
                    InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(C_INPUT_BG));
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), Color.parseColor("#CFD8DC"));
        campo.setBackground(bg);
        return campo;
    }

    @SuppressLint("SetTextI18n")
    private Button crearBotonAccion(String texto, String colorTexto,
                                    String colorFondo, String colorBorde) {
        Button btn = new Button(requireContext());
        btn.setText(texto);
        btn.setTextColor(Color.parseColor(colorTexto));
        btn.setTextSize(14);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setAllCaps(false);
        btn.setPadding(0, dp(13), 0, dp(13));
        btn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(colorFondo));
        bg.setCornerRadius(dp(10));
        bg.setStroke(dp(1), Color.parseColor(colorBorde));
        btn.setBackground(bg);
        return btn;
    }

    private View crearDivisorSutil() {
        View d = new View(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lp.setMargins(dp(16), 0, dp(16), 0);
        d.setLayoutParams(lp);
        d.setBackgroundColor(Color.parseColor(C_BORDE));
        return d;
    }

    private void setBotonCargando(Button btn, boolean cargando, String textoOriginal) {
        if (btn == null || !isAdded()) return;
        btn.setEnabled(!cargando);
        btn.setText(cargando ? "…" : textoOriginal);
        btn.setAlpha(cargando ? 0.6f : 1f);
    }

    private String rolEmoji(String rol) {
        if (rol == null) return "👤";
        switch (rol) {
            case "Admin":   return "🛡️";
            case "Invitado":return "👁️";
            default:        return "🧩";
        }
    }

    private void espaciado(LinearLayout layout, int dpVal) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(dpVal)));
        layout.addView(v);
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }
}