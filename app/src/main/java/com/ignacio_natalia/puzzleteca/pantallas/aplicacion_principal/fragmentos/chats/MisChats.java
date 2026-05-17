package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.clases.Usuario;
import com.ignacio_natalia.puzzleteca.modelos.chat.CrearConversacionRequest;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.Arrays;
import java.util.List;

public class MisChats extends Fragment {

    // ── Paleta de la app ────────────────────────────────────────────────
    private static final String C_ROSA        = "#F06292";
    private static final String C_TEAL        = "#26A69A";
    private static final String C_TEXTO       = "#37474F";
    private static final String C_TEXTO_LEVE  = "#78909C";
    private static final String C_BORDE_CARD  = "#A5D6A7";

    private ChatViewModel viewModel;
    private LinearLayout contenedor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scroll = new ScrollView(getContext());

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(24));

        // ── Cabecera ────────────────────────────────────────────────────
        root.addView(crearCabecera());

        contenedor = new LinearLayout(getContext());
        contenedor.setOrientation(LinearLayout.VERTICAL);
        root.addView(contenedor);

        scroll.addView(root);

        viewModel.getUsuarios().observe(getViewLifecycleOwner(), this::renderUsuarios);
        viewModel.getError().observe(getViewLifecycleOwner(),
                error -> Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show());

        return scroll;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String token = GestorSesion.obtenerToken(requireContext());
        viewModel.cargarUsuarios(token);

        viewModel.getConversacionId().observe(getViewLifecycleOwner(), idConv -> {
            if (idConv == null) return;

            int idPropio = GestorSesion.obtenerId_usuario(requireContext());
            String nombreOtro = viewModel.getNombreSeleccionado();

            Bundle args = new Bundle();
            args.putInt("idConversacion", idConv);
            args.putInt("idPropio", idPropio);
            args.putString("nombreOtro", nombreOtro);

            Fragment fragment = new ConversacionChat();
            fragment.setArguments(args);

            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();

            // Limpiar el valor tras consumirlo.
            viewModel.limpiarConversacionId();
        });
    }

    // ── Cabecera con líneas decorativas ────────────

    private LinearLayout crearCabecera() {
        LinearLayout cont = new LinearLayout(getContext());
        cont.setOrientation(LinearLayout.VERTICAL);
        cont.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(0, 0, 0, dp(20));
        cont.setLayoutParams(p);

        // Fila: línea | 💬 Mis Chats | línea
        LinearLayout fila = new LinearLayout(getContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fp.setMargins(dp(4), 0, dp(4), 0);
        fila.setLayoutParams(fp);

        fila.addView(lineaDecorativa());

        LinearLayout tituloFila = new LinearLayout(getContext());
        tituloFila.setOrientation(LinearLayout.HORIZONTAL);
        tituloFila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams tfp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tfp.setMargins(dp(14), 0, dp(14), 0);
        tituloFila.setLayoutParams(tfp);

        TextView icono = new TextView(getContext());
        icono.setText("\uD83D\uDCAC");
        icono.setTextSize(24);
        icono.setPadding(0, 0, dp(8), 0);

        TextView titulo = new TextView(getContext());
        titulo.setText("Mis Chats");
        titulo.setTextSize(22);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor(C_TEAL));

        tituloFila.addView(icono);
        tituloFila.addView(titulo);
        fila.addView(tituloFila);
        fila.addView(lineaDecorativa());

        cont.addView(fila);

        // Subtítulo
        TextView sub = new TextView(getContext());
        sub.setText("Selecciona un usuario para chatear");
        sub.setTextSize(12);
        sub.setTextColor(Color.parseColor(C_TEXTO_LEVE));
        sub.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        sp.gravity = Gravity.CENTER_HORIZONTAL;
        sp.topMargin = dp(4);
        sub.setLayoutParams(sp);
        cont.addView(sub);

        return cont;
    }

    private View lineaDecorativa() {
        View v = new View(getContext());
        v.setBackgroundColor(Color.parseColor(C_TEAL));
        v.setAlpha(0.45f);
        v.setLayoutParams(new LinearLayout.LayoutParams(0, dp(1), 1f));
        return v;
    }

    // ── Render de la lista de usuarios ─────────────────────────────────

    private void renderUsuarios(List<Usuario> usuarios) {
        contenedor.removeAllViews();
        if (usuarios == null) return;

        String emailPropio = GestorSesion.obtenerEmail(requireContext());
        boolean hayUsuarios = false;

        for (Usuario u : usuarios) {
            if (u.getEmail() != null && u.getEmail().equals(emailPropio)) continue;
            contenedor.addView(crearItemUsuario(u));
            hayUsuarios = true;
        }

        if (!hayUsuarios) {
            TextView vacio = new TextView(getContext());
            vacio.setText("No hay otros usuarios disponibles");
            vacio.setTextColor(Color.parseColor(C_TEXTO_LEVE));
            vacio.setTextSize(15);
            vacio.setGravity(Gravity.CENTER);
            vacio.setPadding(0, dp(40), 0, 0);
            contenedor.addView(vacio);
        }
    }

    // ── Tarjeta de usuario ─────────────────────────────────────────────

    private View crearItemUsuario(Usuario u) {

        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams wrapParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        wrapParams.setMargins(0, 0, 0, dp(14));
        wrapper.setLayoutParams(wrapParams);

        LinearLayout card = new LinearLayout(getContext());
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        // Fondo blanco con borde verde-claro (igual que crearTarjeta() del resto de la app)
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(40));
        bg.setStroke(dp(2), Color.parseColor(C_BORDE_CARD));
        card.setBackground(bg);

        // ── Avatar circular con inicial ────────────────────────────────
        String inicial = (u.getNombre() != null && !u.getNombre().isEmpty())
                ? String.valueOf(u.getNombre().charAt(0)).toUpperCase() : "?";

        TextView avatar = new TextView(getContext());
        avatar.setText(inicial);
        avatar.setTextSize(20);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.WHITE);
        avatar.setGravity(Gravity.CENTER);

        int avatarSize = dp(52);
        LinearLayout.LayoutParams avatarParams = new LinearLayout.LayoutParams(avatarSize, avatarSize);
        avatarParams.setMargins(0, 0, dp(14), 0);
        avatar.setLayoutParams(avatarParams);

        // Colores del avatar alineados con la paleta rosa/teal de la app
        int[] colores = {
                Color.parseColor(C_ROSA),
                Color.parseColor(C_TEAL),
                Color.parseColor("#AB47BC"),  // violeta suave
                Color.parseColor("#FF7043"),  // naranja
                Color.parseColor("#42A5F5"),  // azul claro
                Color.parseColor("#66BB6A"),  // verde
                Color.parseColor("#EC407A"),  // rosa oscuro
                Color.parseColor("#26C6DA"),  // cyan
        };
        int colorIdx = Math.abs(u.getNombre() != null ? u.getNombre().hashCode() : 0) % colores.length;
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(colores[colorIdx]);
        avatar.setBackground(avatarBg);

        // ── Info: nombre + ID de usuario ───────────────────────────────
        LinearLayout info = new LinearLayout(getContext());
        info.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        info.setLayoutParams(infoParams);

        String nombreCompleto = (u.getNombre() != null ? u.getNombre() : "")
                + (u.getApellido() != null ? " " + u.getApellido() : "");

        TextView nombre = new TextView(getContext());
        nombre.setText(nombreCompleto.trim());
        nombre.setTextSize(16);
        nombre.setTypeface(null, Typeface.BOLD);
        nombre.setTextColor(Color.parseColor(C_TEXTO));

        // Badge con ID del usuario (en lugar del email)
        LinearLayout filaBadge = new LinearLayout(getContext());
        filaBadge.setOrientation(LinearLayout.HORIZONTAL);
        filaBadge.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams fbp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        fbp.setMargins(0, dp(4), 0, 0);
        filaBadge.setLayoutParams(fbp);

        TextView labelId = new TextView(getContext());
        labelId.setText("ID ");
        labelId.setTextSize(12);
        labelId.setTextColor(Color.parseColor(C_TEXTO_LEVE));

        TextView valorId = new TextView(getContext());
        valorId.setText(u.getId() != null ? String.valueOf(u.getId()) : "–");
        valorId.setTextSize(12);
        valorId.setTypeface(null, Typeface.BOLD);
        valorId.setTextColor(Color.parseColor(C_TEAL));
        valorId.setPadding(dp(6), dp(2), dp(6), dp(2));
        GradientDrawable badgeId = new GradientDrawable();
        badgeId.setColor(Color.parseColor("#E0F2F1"));   // fondo teal muy suave
        badgeId.setCornerRadius(dp(20));
        valorId.setBackground(badgeId);

        filaBadge.addView(labelId);
        filaBadge.addView(valorId);

        info.addView(nombre);
        info.addView(filaBadge);

        // ── Flecha rosa ────────────────────────────────────────────────
        TextView flecha = new TextView(getContext());
        flecha.setText("\u203A");
        flecha.setTextSize(28);
        flecha.setTextColor(Color.parseColor(C_ROSA));
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setPadding(dp(8), 0, 0, 0);

        card.addView(avatar);
        card.addView(info);
        card.addView(flecha);
        wrapper.addView(card);

        // ── Click ──────────────────────────────────────────────────────
        final String nombreFinal = nombreCompleto.trim();

        // Estado presionado con fondo teal suave
        GradientDrawable bgPressed = new GradientDrawable();
        bgPressed.setColor(Color.parseColor("#E0F2F1"));
        bgPressed.setCornerRadius(dp(40));
        bgPressed.setStroke(dp(2), Color.parseColor(C_TEAL));

        card.setOnClickListener(v -> {
            card.setBackground(bgPressed);
            card.postDelayed(() -> card.setBackground(bg), 150);

            int idPropio = GestorSesion.obtenerId_usuario(requireContext());
            viewModel.setNombreSeleccionado(nombreFinal);

            CrearConversacionRequest req = new CrearConversacionRequest(
                    Arrays.asList(idPropio, u.getId())
            );
            viewModel.crearConversacion(req);
        });

        return wrapper;
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}