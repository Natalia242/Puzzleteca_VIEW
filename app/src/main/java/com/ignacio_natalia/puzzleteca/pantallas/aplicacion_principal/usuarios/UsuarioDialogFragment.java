package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.usuarios;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UsuarioDialogFragment extends DialogFragment {

    private static final String ARG_USUARIO = "usuario";

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final String C_TEAL       = "#2E7D6E";
    private static final String C_TEAL_SOFT  = "#E0F2F1";
    private static final String C_ADMIN      = "#5C6BC0";
    private static final String C_ADMIN_SOFT = "#E8EAF6";
    private static final String C_BLOQ       = "#E53935";
    private static final String C_BLOQ_SOFT  = "#FFEBEE";
    private static final String C_USUARIO    = "#2E7D6E";
    private static final String C_USU_SOFT   = "#E0F2F1";
    private static final String C_TEXTO      = "#37474F";
    private static final String C_SUBTEXTO   = "#78909C";
    private static final String C_BORDE      = "#ECEFF1";
    private static final String C_FONDO      = "#F5F7F8";
    private static final String C_PELIGRO    = "#E53935";
    private static final String C_PELIGRO_BG = "#FFEBEE";
    private static final String C_PELIGRO_BD = "#EF9A9A";

    private Usuario usuario;
    private UsuarioRepositorio repositorio;

    // Listener para notificar cambios a la lista padre
    public interface OnUsuarioModificadoListener {
        void onUsuarioModificado();
    }
    private OnUsuarioModificadoListener listener;

    public void setOnUsuarioModificadoListener(OnUsuarioModificadoListener l) {
        this.listener = l;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Construcción
    // ══════════════════════════════════════════════════════════════════════════

    public static UsuarioDialogFragment newInstance(Usuario usuario) {
        UsuarioDialogFragment f = new UsuarioDialogFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_USUARIO, usuario);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuario = (Usuario) getArguments().getSerializable(ARG_USUARIO);
        }
        repositorio = new UsuarioRepositorio();
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Vista
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        boolean esAdmin = GestorSesion.esAdmin(requireContext());

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(20), dp(20), dp(20), dp(24));
        scroll.addView(layout);

        // ── Cabecera: avatar + nombre + chip de rol ───────────────────────────
        layout.addView(crearCabecera());

        // ── Sección pública ───────────────────────────────────────────────────
        layout.addView(crearTarjetaPublica());

        // ── Sección restringida (solo Admin) ──────────────────────────────────
        if (esAdmin) {
            espaciado(layout, 8);
            layout.addView(crearTarjetaPrivada());

            espaciado(layout, 8);
            layout.addView(crearTarjetaGestion());

            espaciado(layout, 8);
            layout.addView(crearZonaPeligro());
        }

        // ── Botón cerrar ──────────────────────────────────────────────────────
        espaciado(layout, 12);
        TextView btnCerrar = new TextView(requireContext());
        btnCerrar.setText("Cerrar");
        btnCerrar.setTextColor(Color.parseColor(C_SUBTEXTO));
        btnCerrar.setTextSize(14);
        btnCerrar.setTypeface(null, Typeface.BOLD);
        btnCerrar.setGravity(Gravity.CENTER);
        btnCerrar.setPadding(0, dp(13), 0, dp(13));
        btnCerrar.setClickable(true);
        btnCerrar.setFocusable(true);
        LinearLayout.LayoutParams cerrarLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        btnCerrar.setLayoutParams(cerrarLP);
        GradientDrawable cerrarBg = new GradientDrawable();
        cerrarBg.setColor(Color.parseColor("#F0F0F0"));
        cerrarBg.setCornerRadius(dp(12));
        btnCerrar.setBackground(cerrarBg);
        btnCerrar.setOnClickListener(v -> dismiss());

        layout.addView(btnCerrar);
        return scroll;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Cabecera
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearCabecera() {
        LinearLayout cab = new LinearLayout(requireContext());
        cab.setOrientation(LinearLayout.VERTICAL);
        cab.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams cabLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cabLP.bottomMargin = dp(16);
        cab.setLayoutParams(cabLP);

        // Avatar circular grande con inicial
        String nombre = usuario.getNombre() != null ? usuario.getNombre() : "?";
        String inicial = nombre.substring(0, 1).toUpperCase();
        String[] colores = colorPorTipo(usuario.getTipoUsuario());

        TextView avatar = new TextView(requireContext());
        avatar.setText(inicial);
        avatar.setTextSize(32);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.parseColor(colores[0]));
        avatar.setGravity(Gravity.CENTER);
        int size = dp(80);
        LinearLayout.LayoutParams avLP = new LinearLayout.LayoutParams(size, size);
        avLP.gravity = Gravity.CENTER_HORIZONTAL;
        avLP.bottomMargin = dp(12);
        avatar.setLayoutParams(avLP);
        GradientDrawable avBg = new GradientDrawable();
        avBg.setShape(GradientDrawable.OVAL);
        avBg.setColor(Color.parseColor(colores[1]));
        avBg.setStroke(dp(2), Color.parseColor(colores[0]));
        avatar.setBackground(avBg);

        // Nombre completo
        TextView tvNombre = new TextView(requireContext());
        String nombreCompleto = (usuario.getNombre() != null ? usuario.getNombre() : "")
                + " " + (usuario.getApellido() != null ? usuario.getApellido() : "");
        tvNombre.setText(nombreCompleto.trim());
        tvNombre.setTextSize(20);
        tvNombre.setTypeface(null, Typeface.BOLD);
        tvNombre.setTextColor(Color.parseColor(C_TEXTO));
        tvNombre.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams nomLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        nomLP.bottomMargin = dp(8);
        tvNombre.setLayoutParams(nomLP);

        // Chip de rol
        TextView chipRol = crearChipRol(usuario.getTipoUsuario());
        LinearLayout.LayoutParams chipLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        chipLP.gravity = Gravity.CENTER_HORIZONTAL;
        chipRol.setLayoutParams(chipLP);

        cab.addView(avatar);
        cab.addView(tvNombre);
        cab.addView(chipRol);
        return cab;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Tarjeta pública (visible para todos)
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjetaPublica() {
        LinearLayout card = crearTarjeta(false);

        String nombre = (usuario.getNombre() != null ? usuario.getNombre() : "—")
                + " " + (usuario.getApellido() != null ? usuario.getApellido() : "");
        card.addView(crearFila("👤", "Nombre", nombre.trim()));
        card.addView(crearDivisor());
        card.addView(crearFila("🎭", "Tipo de cuenta", labelTipo(usuario.getTipoUsuario())));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Tarjeta privada (solo Admin)
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjetaPrivada() {
        LinearLayout card = crearTarjeta(true);

        // Cabecera de "acceso restringido"
        LinearLayout bannerRow = new LinearLayout(requireContext());
        bannerRow.setOrientation(LinearLayout.HORIZONTAL);
        bannerRow.setGravity(Gravity.CENTER_VERTICAL);
        bannerRow.setPadding(dp(14), dp(10), dp(14), dp(10));
        LinearLayout.LayoutParams bannerLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bannerLP.bottomMargin = dp(4);
        bannerRow.setLayoutParams(bannerLP);
        GradientDrawable bannerBg = new GradientDrawable();
        bannerBg.setColor(Color.parseColor(C_ADMIN_SOFT));
        bannerBg.setCornerRadii(new float[]{dp(12), dp(12), dp(12), dp(12), 0, 0, 0, 0});
        bannerRow.setBackground(bannerBg);

        TextView tvLock = new TextView(requireContext());
        tvLock.setText("🔒");
        tvLock.setTextSize(14);
        LinearLayout.LayoutParams lockLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lockLP.setMargins(0, 0, dp(8), 0);
        tvLock.setLayoutParams(lockLP);

        TextView tvBanner = new TextView(requireContext());
        tvBanner.setText("Datos confidenciales — acceso admin");
        tvBanner.setTextSize(12);
        tvBanner.setTypeface(null, Typeface.BOLD);
        tvBanner.setTextColor(Color.parseColor(C_ADMIN));

        bannerRow.addView(tvLock);
        bannerRow.addView(tvBanner);
        card.addView(bannerRow);
        card.addView(crearDivisor());

        card.addView(crearFila("🆔", "ID de usuario",
                usuario.getId() != null ? String.valueOf(usuario.getId()) : "—"));
        card.addView(crearDivisor());
        card.addView(crearFila("📧", "Email",
                usuario.getEmail() != null ? usuario.getEmail() : "—"));

        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Tarjeta de gestión (Spinner de tipo — solo Admin)
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearTarjetaGestion() {
        LinearLayout card = crearTarjeta(false);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText("Cambiar tipo de usuario");
        tvLabel.setTextSize(13);
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setTextColor(Color.parseColor(C_TEXTO));
        LinearLayout.LayoutParams labelLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        labelLP.bottomMargin = dp(4);
        tvLabel.setLayoutParams(labelLP);

        TextView tvSub = new TextView(requireContext());
        tvSub.setText("El cambio se aplica de forma inmediata.");
        tvSub.setTextSize(12);
        tvSub.setTextColor(Color.parseColor(C_SUBTEXTO));
        LinearLayout.LayoutParams subLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLP.bottomMargin = dp(12);
        tvSub.setLayoutParams(subLP);

        // Spinner de tipo
        Usuario.TipoUsuario[] tipos = Usuario.TipoUsuario.values();
        Spinner spinner = new Spinner(requireContext());
        ArrayAdapter<Usuario.TipoUsuario> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (usuario.getTipoUsuario() != null) {
            spinner.setSelection(usuario.getTipoUsuario().ordinal());
        }
        GradientDrawable spBg = new GradientDrawable();
        spBg.setColor(Color.WHITE);
        spBg.setCornerRadius(dp(10));
        spBg.setStroke(dp(1), Color.parseColor(C_BORDE));
        spinner.setBackground(spBg);
        spinner.setPadding(dp(10), dp(8), dp(10), dp(8));
        LinearLayout.LayoutParams spLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        spLP.bottomMargin = dp(14);
        spinner.setLayoutParams(spLP);

        // Estado de confirmación
        TextView tvEstado = new TextView(requireContext());
        tvEstado.setTextSize(12);
        tvEstado.setTextColor(Color.parseColor(C_SUBTEXTO));
        tvEstado.setText("Selecciona un tipo para cambiar.");
        tvEstado.setGravity(Gravity.CENTER);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean init = false;
            @Override
            public void onItemSelected(AdapterView<?> p, View v, int pos, long id) {
                if (!init) { init = true; return; }
                Usuario.TipoUsuario nuevo = tipos[pos];
                if (nuevo == usuario.getTipoUsuario()) return;

                tvEstado.setText("Actualizando…");
                tvEstado.setTextColor(Color.parseColor(C_SUBTEXTO));

                String token = GestorSesion.obtenerToken(requireContext());
                repositorio.actualizarEstado(usuario.getEmail(), nuevo.name(),
                        new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> c, @NonNull Response<Void> r) {
                                if (!isAdded()) return;
                                requireActivity().runOnUiThread(() -> {
                                    if (r.isSuccessful()) {
                                        usuario.setTipoUsuario(nuevo);
                                        tvEstado.setText("✅ Tipo actualizado a " + nuevo.name());
                                        tvEstado.setTextColor(Color.parseColor("#2E7D32"));
                                        if (listener != null) listener.onUsuarioModificado();
                                    } else {
                                        tvEstado.setText("❌ Error al actualizar (" + r.code() + ")");
                                        tvEstado.setTextColor(Color.parseColor(C_BLOQ));
                                        spinner.setSelection(usuario.getTipoUsuario().ordinal());
                                    }
                                });
                            }
                            @Override
                            public void onFailure(@NonNull Call<Void> c, @NonNull Throwable t) {
                                if (!isAdded()) return;
                                requireActivity().runOnUiThread(() -> {
                                    tvEstado.setText("❌ Error de red");
                                    tvEstado.setTextColor(Color.parseColor(C_BLOQ));
                                    spinner.setSelection(usuario.getTipoUsuario().ordinal());
                                });
                            }
                        });
            }
            @Override public void onNothingSelected(AdapterView<?> p) {}
        });

        card.addView(tvLabel);
        card.addView(tvSub);
        card.addView(spinner);
        card.addView(tvEstado);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Zona de peligro (solo Admin)
    // ══════════════════════════════════════════════════════════════════════════

    @SuppressLint("SetTextI18n")
    private LinearLayout crearZonaPeligro() {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        LinearLayout.LayoutParams cardLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLP.bottomMargin = dp(4);
        card.setLayoutParams(cardLP);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(C_PELIGRO_BG));
        bg.setCornerRadius(dp(14));
        bg.setStroke(dp(1), Color.parseColor(C_PELIGRO_BD));
        card.setBackground(bg);

        TextView tvAviso = new TextView(requireContext());
        tvAviso.setText("⚠️  Eliminar este usuario borrará todos sus datos permanentemente.");
        tvAviso.setTextSize(13);
        tvAviso.setTextColor(Color.parseColor("#B71C1C"));
        LinearLayout.LayoutParams avisoLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        avisoLP.bottomMargin = dp(12);
        tvAviso.setLayoutParams(avisoLP);
        card.addView(tvAviso);

        TextView btnEliminar = new TextView(requireContext());
        btnEliminar.setText("🗑️  Eliminar usuario");
        btnEliminar.setTextColor(Color.WHITE);
        btnEliminar.setTextSize(14);
        btnEliminar.setTypeface(null, Typeface.BOLD);
        btnEliminar.setGravity(Gravity.CENTER);
        btnEliminar.setPadding(0, dp(13), 0, dp(13));
        btnEliminar.setClickable(true);
        btnEliminar.setFocusable(true);
        btnEliminar.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable elimBg = new GradientDrawable();
        elimBg.setColor(Color.parseColor(C_PELIGRO));
        elimBg.setCornerRadius(dp(12));
        btnEliminar.setBackground(elimBg);

        btnEliminar.setOnClickListener(v -> {
            String nombreUsuario = usuario.getNombre() != null ? usuario.getNombre() : "este usuario";
            UtilidadesSesion.mostrarDialogoPersonalizado(
                    requireContext(),
                    "🗑️",
                    "Eliminar usuario",
                    "¿Seguro que quieres eliminar a " + nombreUsuario + "?\nEsta acción no se puede deshacer.",
                    "Eliminar",
                    C_PELIGRO,
                    this::eliminarUsuario
            );
        });

        card.addView(btnEliminar);
        return card;
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Lógica de red
    // ══════════════════════════════════════════════════════════════════════════

    private void eliminarUsuario() {
        if (usuario.getEmail() == null) return;
        repositorio.borrarCuenta(usuario.getEmail(), new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> r) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (r.isSuccessful()) {
                        Toast.makeText(requireContext(),
                                "Usuario eliminado", Toast.LENGTH_SHORT).show();
                        if (listener != null) listener.onUsuarioModificado();
                        dismiss();
                    } else {
                        Toast.makeText(requireContext(),
                                "Error al eliminar (" + r.code() + ")", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(),
                                "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Helpers de UI
    // ══════════════════════════════════════════════════════════════════════════

    private LinearLayout crearTarjeta(boolean borde) {
        LinearLayout t = new LinearLayout(requireContext());
        t.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(4);
        t.setLayoutParams(lp);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(14));
        if (borde) bg.setStroke(dp(1), Color.parseColor(C_ADMIN));
        else       bg.setStroke(dp(1), Color.parseColor(C_BORDE));
        t.setBackground(bg);
        t.setElevation(dp(1));
        return t;
    }

    @SuppressLint("SetTextI18n")
    private LinearLayout crearFila(String emoji, String etiqueta, String valor) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(dp(16), dp(13), dp(16), dp(13));

        TextView tvEmoji = new TextView(requireContext());
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(16);
        LinearLayout.LayoutParams eLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        eLP.setMargins(0, 0, dp(12), 0);
        tvEmoji.setLayoutParams(eLP);

        LinearLayout col = new LinearLayout(requireContext());
        col.setOrientation(LinearLayout.VERTICAL);
        col.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView tvLabel = new TextView(requireContext());
        tvLabel.setText(etiqueta);
        tvLabel.setTextSize(11);
        tvLabel.setTypeface(null, Typeface.BOLD);
        tvLabel.setTextColor(Color.parseColor(C_SUBTEXTO));
        tvLabel.setLetterSpacing(0.04f);

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

    private View crearDivisor() {
        View d = new View(requireContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        lp.setMargins(dp(16), 0, dp(16), 0);
        d.setLayoutParams(lp);
        d.setBackgroundColor(Color.parseColor(C_BORDE));
        return d;
    }

    @SuppressLint("SetTextI18n")
    private TextView crearChipRol(Usuario.TipoUsuario tipo) {
        String[] c = colorPorTipo(tipo);
        TextView chip = new TextView(requireContext());
        chip.setText(emojiTipo(tipo) + "  " + labelTipo(tipo));
        chip.setTextSize(12);
        chip.setTypeface(null, Typeface.BOLD);
        chip.setTextColor(Color.parseColor(c[0]));
        chip.setPadding(dp(14), dp(6), dp(14), dp(6));
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(c[1]));
        bg.setCornerRadius(dp(20));
        bg.setStroke(dp(1), Color.parseColor(c[0]));
        chip.setBackground(bg);
        return chip;
    }

    /** [color texto, color fondo] según tipo */
    private String[] colorPorTipo(Usuario.TipoUsuario tipo) {
        if (tipo == null) return new String[]{C_USUARIO, C_USU_SOFT};
        switch (tipo) {
            case Admin:    return new String[]{C_ADMIN,   C_ADMIN_SOFT};
            case Bloqueado:return new String[]{C_BLOQ,    C_BLOQ_SOFT};
            default:       return new String[]{C_USUARIO, C_USU_SOFT};
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
            case Admin:    return "Administrador";
            case Bloqueado:return "Bloqueado";
            default:       return "Usuario";
        }
    }

    private void espaciado(LinearLayout l, int dpVal) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(dpVal)));
        l.addView(v);
    }

    private int dp(int v) {
        return Math.round(v * requireContext().getResources().getDisplayMetrics().density);
    }

    // ══════════════════════════════════════════════════════════════════════════
    //  Ventana del dialog
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            int screenW = requireContext().getResources().getDisplayMetrics().widthPixels;
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().setLayout(
                    (int)(screenW * 0.92f),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}