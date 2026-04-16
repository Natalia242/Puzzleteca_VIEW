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

import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.modelos.chat.CrearConversacionRequest;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.Arrays;
import java.util.List;

public class MisChats extends Fragment {

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
        scroll.setBackgroundColor(Color.parseColor("#F4F6FB"));

        LinearLayout root = new LinearLayout(getContext());
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(24), dp(20), dp(24));
        root.setBackgroundColor(Color.parseColor("#F4F6FB"));

        // Encabezado
        LinearLayout header = new LinearLayout(getContext());
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(0, 0, 0, dp(20));

        TextView icono = new TextView(getContext());
        icono.setText("\uD83D\uDCAC");
        icono.setTextSize(28);
        icono.setPadding(0, 0, dp(10), 0);

        TextView titulo = new TextView(getContext());
        titulo.setText("Mis Chats");
        titulo.setTextSize(26);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#1A1A2E"));

        header.addView(icono);
        header.addView(titulo);
        root.addView(header);

        // Separador
        View sep = new View(getContext());
        sep.setBackgroundColor(Color.parseColor("#E0E4F0"));
        LinearLayout.LayoutParams sepParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1));
        sepParams.setMargins(0, 0, 0, dp(20));
        sep.setLayoutParams(sepParams);
        root.addView(sep);

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
    }

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
            vacio.setTextColor(Color.parseColor("#9A9DB5"));
            vacio.setTextSize(15);
            vacio.setGravity(Gravity.CENTER);
            vacio.setPadding(0, dp(40), 0, 0);
            contenedor.addView(vacio);
        }
    }

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

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dp(18));
        card.setBackground(bg);

        // Avatar circular con inicial
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

        int[] colores = {
                0xFF4361EE, 0xFF7209B7, 0xFF3A0CA3, 0xFF4CC9F0,
                0xFFE63946, 0xFF2EC4B6, 0xFFFF6B6B, 0xFF06D6A0
        };
        int colorIdx = Math.abs(u.getNombre() != null ? u.getNombre().hashCode() : 0) % colores.length;
        GradientDrawable avatarBg = new GradientDrawable();
        avatarBg.setShape(GradientDrawable.OVAL);
        avatarBg.setColor(colores[colorIdx]);
        avatar.setBackground(avatarBg);

        // Info
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
        nombre.setTextColor(Color.parseColor("#1A1A2E"));

        TextView emailTv = new TextView(getContext());
        emailTv.setText(u.getEmail());
        emailTv.setTextSize(13);
        emailTv.setTextColor(Color.parseColor("#8A8FAD"));

        info.addView(nombre);
        info.addView(emailTv);

        // Flecha
        TextView flecha = new TextView(getContext());
        flecha.setText("\u203A");
        flecha.setTextSize(28);
        flecha.setTextColor(Color.parseColor("#C0C5DE"));
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setPadding(dp(8), 0, 0, 0);

        card.addView(avatar);
        card.addView(info);
        card.addView(flecha);
        wrapper.addView(card);

        // Click: crear conversación y navegar
        final String nombreFinal = nombreCompleto.trim();
        card.setOnClickListener(v -> {
            GradientDrawable bgPressed = new GradientDrawable();
            bgPressed.setColor(Color.parseColor("#F0F4FF"));
            bgPressed.setCornerRadius(dp(18));
            card.setBackground(bgPressed);
            card.postDelayed(() -> card.setBackground(bg), 150);

            String emailPropio = GestorSesion.obtenerEmail(requireContext());

            CrearConversacionRequest req = new CrearConversacionRequest(
                    Arrays.asList(emailPropio, u.getEmail())
            );
            viewModel.crearConversacion(req);

            viewModel.getConversacionId().observe(getViewLifecycleOwner(), idConv -> {
                if (idConv == null) return;

                Bundle args = new Bundle();
                args.putInt("idConversacion", idConv);
                args.putString("emailPropio", emailPropio);
                args.putString("nombreOtro", nombreFinal);

                Fragment fragment = new ConversacionChat();
                fragment.setArguments(args);

                requireActivity()
                        .getSupportFragmentManager()
                        .beginTransaction()
                        .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                        .addToBackStack(null)
                        .commit();
            });
        });

        return wrapper;
    }

    private int dp(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }
}
