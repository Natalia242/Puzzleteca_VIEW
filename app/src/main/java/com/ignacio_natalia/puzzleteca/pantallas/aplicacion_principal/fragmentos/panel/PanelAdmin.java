package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.panel;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.ranking.RankingUsuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats.MisChats;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles.GestionPuzzles;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios.GestionUsuarios;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.perfil.EditarPerfil;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.NuevoPuzzle;
import com.ignacio_natalia.puzzleteca.repositorios.RankingRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import java.util.List;

public class PanelAdmin extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle instanciaEstadoGuardado) {

        String token     = GestorSesion.obtenerToken(requireContext());
        String nombre    = GestorSesion.obtenerNombre(requireContext());
        int    idUsuario = GestorSesion.obtenerId_usuario(requireContext());

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 50, 40, 50);

        // ── Tarjeta cabecera Admin ───────────────────────────────────────
        LinearLayout tarjetaAdmin = new LinearLayout(requireContext());
        tarjetaAdmin.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaAdmin.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaAdmin.setPadding(40, 36, 40, 36);
        LinearLayout.LayoutParams paramsTa = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTa.setMargins(0, 0, 0, 20);
        tarjetaAdmin.setLayoutParams(paramsTa);
        GradientDrawable fondoAdmin = new GradientDrawable();
        fondoAdmin.setColor(ContextCompat.getColor(requireContext(), R.color.app_admin_bg));
        fondoAdmin.setCornerRadius(40);
        tarjetaAdmin.setBackground(fondoAdmin);

        TextView avatar = new TextView(requireContext());
        avatar.setText(nombre.isEmpty() ? "A" : nombre.substring(0, 1).toUpperCase());
        avatar.setTextSize(22);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_admin_bg));
        avatar.setGravity(Gravity.CENTER);
        int tamAv = dpToPx(52);
        LinearLayout.LayoutParams paramsAv = new LinearLayout.LayoutParams(tamAv, tamAv);
        paramsAv.setMargins(0, 0, 24, 0);
        avatar.setLayoutParams(paramsAv);
        GradientDrawable fondoAv = new GradientDrawable();
        fondoAv.setShape(GradientDrawable.OVAL);
        fondoAv.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        avatar.setBackground(fondoAv);

        LinearLayout infoAdmin = new LinearLayout(requireContext());
        infoAdmin.setOrientation(LinearLayout.VERTICAL);
        infoAdmin.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView textoNombreAdmin = new TextView(requireContext());
        textoNombreAdmin.setText("¡Hola, " + nombre + "! 🛡️");
        textoNombreAdmin.setTextSize(18);
        textoNombreAdmin.setTypeface(null, Typeface.BOLD);
        textoNombreAdmin.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        TextView textoRolAdmin = new TextView(requireContext());
        textoRolAdmin.setText("Panel de Administración");
        textoRolAdmin.setTextSize(12);
        textoRolAdmin.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_admin_rol_text));

        infoAdmin.addView(textoNombreAdmin);
        infoAdmin.addView(textoRolAdmin);
        tarjetaAdmin.addView(avatar);
        tarjetaAdmin.addView(infoAdmin);
        layout.addView(tarjetaAdmin);

        // ── Tarjeta Ranking ──────────────────────────────────────────────
        LinearLayout tarjetaRanking = new LinearLayout(requireContext());
        tarjetaRanking.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaRanking.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaRanking.setPadding(50, 40, 50, 40);
        LinearLayout.LayoutParams paramsRk = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsRk.setMargins(0, 0, 0, 20);
        tarjetaRanking.setLayoutParams(paramsRk);
        GradientDrawable fondoRanking = new GradientDrawable();
        fondoRanking.setColor(ContextCompat.getColor(requireContext(), R.color.app_teal_darker));
        fondoRanking.setCornerRadius(40);
        tarjetaRanking.setBackground(fondoRanking);

        LinearLayout infoRanking = new LinearLayout(requireContext());
        infoRanking.setOrientation(LinearLayout.VERTICAL);
        infoRanking.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView labelRanking = new TextView(requireContext());
        labelRanking.setText("🏆  TU POSICIÓN HOY");
        labelRanking.setTextSize(10);
        labelRanking.setTypeface(null, Typeface.BOLD);
        labelRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_teal_borde));
        labelRanking.setLetterSpacing(0.1f);

        TextView textoPosicion = new TextView(requireContext());
        textoPosicion.setText("—");
        textoPosicion.setTextSize(30);
        textoPosicion.setTypeface(null, Typeface.BOLD);
        textoPosicion.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));

        TextView textoDetalleRanking = new TextView(requireContext());
        textoDetalleRanking.setText("Cargando...");
        textoDetalleRanking.setTextSize(12);
        textoDetalleRanking.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_teal_borde));

        infoRanking.addView(labelRanking);
        infoRanking.addView(textoPosicion);
        infoRanking.addView(textoDetalleRanking);

        TextView iconoTrofeo = new TextView(requireContext());
        iconoTrofeo.setText("🏆");
        iconoTrofeo.setTextSize(36);

        tarjetaRanking.addView(infoRanking);
        tarjetaRanking.addView(iconoTrofeo);
        layout.addView(tarjetaRanking);

        // ── Llamada al ranking ───────────────────────────────────────────
        new RankingRepositorio().obtenerRankingDiario(token, new retrofit2.Callback<List<RankingUsuario>>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<List<RankingUsuario>> call,
                                   @NonNull retrofit2.Response<List<RankingUsuario>> response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        List<RankingUsuario> ranking = response.body();
                        int posicion = -1;
                        RankingUsuario miEntrada = null;
                        for (int i = 0; i < ranking.size(); i++) {
                            if (ranking.get(i).getIdUsuario() != null
                                    && ranking.get(i).getIdUsuario() == idUsuario) {
                                posicion = i + 1;
                                miEntrada = ranking.get(i);
                                break;
                            }
                        }
                        if (posicion > 0) {
                            String medalla = posicion == 1 ? "🥇 " : posicion == 2 ? "🥈 " : posicion == 3 ? "🥉 " : "";
                            textoPosicion.setText(medalla + "#" + posicion);
                            iconoTrofeo.setText("🏆");
                            String media = miEntrada != null && miEntrada.getMediaDiaria() != null
                                    ? String.format("%.1f ★", miEntrada.getMediaDiaria()) : "—";
                            textoDetalleRanking.setText("Media: " + media + "  ·  de " + ranking.size() + " jugadores");
                        } else {
                            textoPosicion.setText("Sin datos");
                            textoDetalleRanking.setText("Aún no has valorado hoy");
                            iconoTrofeo.setText("🏆");
                        }
                    } else {
                        textoPosicion.setText("—");
                        textoDetalleRanking.setText("No disponible");
                    }
                });
            }
            @Override
            public void onFailure(@NonNull retrofit2.Call<List<RankingUsuario>> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    textoPosicion.setText("—");
                    textoDetalleRanking.setText("Sin conexión");
                });
            }
        });

        // ── Sección Perfil ───────────────────────────────────────────────
        layout.addView(crearEtiquetaSeccion("PERFIL"));
        LinearLayout opEditarPerfil = crearOpcion("✏️", "Editar Perfil", R.color.app_admin_indigo_soft, R.color.app_admin_accent);
        opEditarPerfil.setOnClickListener(v -> irA(new EditarPerfil()));
        layout.addView(opEditarPerfil);
        espacio(layout, 6);
        LinearLayout opMisChats = crearOpcion("💬", "Mis Chats", R.color.app_teal_soft, R.color.app_teal);
        opMisChats.setOnClickListener(v -> irA(new MisChats()));
        layout.addView(opMisChats);
        espacio(layout, 18);

        // ── Sección Gestión ──────────────────────────────────────────────
        layout.addView(crearEtiquetaSeccion("GESTIÓN"));
        LinearLayout opCrearPuzzle = crearOpcion("➕", "Crear Nuevo Puzzle", R.color.app_rosa_soft, R.color.app_rosa);
        opCrearPuzzle.setOnClickListener(v -> irA(new NuevoPuzzle()));
        layout.addView(opCrearPuzzle);
        espacio(layout, 6);
        LinearLayout opGestionPuzzles = crearOpcion("🧩", "Gestionar Puzzles", R.color.app_naranja_soft, R.color.app_naranja);
        opGestionPuzzles.setOnClickListener(v -> irA(new GestionPuzzles()));
        layout.addView(opGestionPuzzles);
        espacio(layout, 6);
        LinearLayout opGestionUsuarios = crearOpcion("👥", "Gestionar Usuarios", R.color.app_green_success, R.color.app_green_success_text_dark);
        opGestionUsuarios.setOnClickListener(v -> irA(new GestionUsuarios()));
        layout.addView(opGestionUsuarios);
        espacio(layout, 28);

        // ── Divisor ──────────────────────────────────────────────────────
        View divisor = new View(requireContext());
        divisor.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divisor.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_borde));
        layout.addView(divisor);
        espacio(layout, 20);

        // ── Botón Cerrar Sesión ──────────────────────────────────────────
        Button btnCerrarSesion = crearBotonCerrarSesion("🚪  Cerrar Sesión");
        btnCerrarSesion.setOnClickListener(v ->
                UtilidadesSesion.mostrarDialogoCerrarSesion(requireContext(), () ->
                        UtilidadesSesion.cerrarSesion(requireContext())));
        layout.addView(btnCerrarSesion);

        scroll.addView(layout);
        return scroll;
    }

    private void irA(Fragment destino) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(AppPrincipal.FRAGMENTO_ID, destino)
                .addToBackStack(null)
                .commit();
    }

    private TextView crearEtiquetaSeccion(String texto) {
        TextView label = new TextView(requireContext());
        label.setText(texto);
        label.setTextSize(11);
        label.setTypeface(null, Typeface.BOLD);
        label.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_subtexto_label));
        label.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(4, 0, 0, 10);
        label.setLayoutParams(p);
        return label;
    }

    private LinearLayout crearOpcion(String emoji, String texto, int colorFondoRes, int colorBordeRes) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(40, 32, 40, 32);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 10);
        fila.setLayoutParams(params);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(ContextCompat.getColor(requireContext(), colorFondoRes));
        forma.setCornerRadius(dpToPx(16));
        forma.setStroke(dpToPx(1), ContextCompat.getColor(requireContext(), colorBordeRes));
        fila.setBackground(forma);

        TextView emojiTv = new TextView(requireContext());
        emojiTv.setText(emoji);
        emojiTv.setTextSize(22);
        emojiTv.setPadding(0, 0, 24, 0);

        TextView textoTv = new TextView(requireContext());
        textoTv.setText(texto);
        textoTv.setTextSize(15);
        textoTv.setTypeface(null, Typeface.BOLD);
        textoTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        textoTv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView flecha = new TextView(requireContext());
        flecha.setText("›");
        flecha.setTextSize(22);
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setTextColor(ContextCompat.getColor(requireContext(), colorBordeRes));

        fila.addView(emojiTv);
        fila.addView(textoTv);
        fila.addView(flecha);
        return fila;
    }

    private Button crearBotonCerrarSesion(String texto) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_peligro_dark));
        boton.setTextSize(15);
        boton.setTypeface(null, Typeface.BOLD);
        boton.setAllCaps(false);
        boton.setPadding(0, dpToPx(14), 0, dpToPx(14));
        boton.setGravity(Gravity.CENTER);
        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(ContextCompat.getColor(requireContext(), R.color.app_peligro_bg));
        forma.setCornerRadius(dpToPx(14));
        forma.setStroke(dpToPx(1), ContextCompat.getColor(requireContext(), R.color.app_peligro_borde));
        boton.setBackground(forma);
        return boton;
    }

    private void espacio(LinearLayout layout, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(v);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * requireContext().getResources().getDisplayMetrics().density);
    }
}