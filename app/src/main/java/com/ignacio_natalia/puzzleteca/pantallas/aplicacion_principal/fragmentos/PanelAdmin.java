package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

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

import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.EditarPerfil;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats.MisChats;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles.GestionPuzzles;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios.GestionUsuarios;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.RegistrarPuzzle;
import com.ignacio_natalia.puzzleteca.modelos.RankingUsuario;
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

        // ── Tarjeta de cabecera Admin ────────────────────────────────
        LinearLayout tarjetaAdmin = new LinearLayout(requireContext());
        tarjetaAdmin.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaAdmin.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaAdmin.setPadding(40, 36, 40, 36);
        LinearLayout.LayoutParams paramsTa = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsTa.setMargins(0, 0, 0, 20);
        tarjetaAdmin.setLayoutParams(paramsTa);
        GradientDrawable fondoAdmin = new GradientDrawable();
        fondoAdmin.setColor(Color.parseColor("#4527A0"));
        fondoAdmin.setCornerRadius(40);
        tarjetaAdmin.setBackground(fondoAdmin);

        // Avatar circular
        TextView avatar = new TextView(requireContext());
        avatar.setText(nombre.isEmpty() ? "A" : nombre.substring(0, 1).toUpperCase());
        avatar.setTextSize(22);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.parseColor("#4527A0"));
        avatar.setGravity(Gravity.CENTER);
        int tamAv = dpToPx(52);
        LinearLayout.LayoutParams paramsAv = new LinearLayout.LayoutParams(tamAv, tamAv);
        paramsAv.setMargins(0, 0, 24, 0);
        avatar.setLayoutParams(paramsAv);
        GradientDrawable fondoAv = new GradientDrawable();
        fondoAv.setShape(GradientDrawable.OVAL);
        fondoAv.setColor(Color.WHITE);
        avatar.setBackground(fondoAv);

        LinearLayout infoAdmin = new LinearLayout(requireContext());
        infoAdmin.setOrientation(LinearLayout.VERTICAL);
        infoAdmin.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView textoNombreAdmin = new TextView(requireContext());
        textoNombreAdmin.setText("¡Hola, " + nombre + "! 🛡️");
        textoNombreAdmin.setTextSize(18);
        textoNombreAdmin.setTypeface(null, Typeface.BOLD);
        textoNombreAdmin.setTextColor(Color.WHITE);

        TextView textoRolAdmin = new TextView(requireContext());
        textoRolAdmin.setText("Panel de Administración");
        textoRolAdmin.setTextSize(12);
        textoRolAdmin.setTextColor(Color.parseColor("#CE93D8"));

        infoAdmin.addView(textoNombreAdmin);
        infoAdmin.addView(textoRolAdmin);

        tarjetaAdmin.addView(avatar);
        tarjetaAdmin.addView(infoAdmin);
        layout.addView(tarjetaAdmin);

        // ── Tarjeta de Ranking del día ───────────────────────────────
        LinearLayout tarjetaRanking = new LinearLayout(requireContext());
        tarjetaRanking.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaRanking.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaRanking.setPadding(50, 40, 50, 40);
        LinearLayout.LayoutParams paramsRk = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsRk.setMargins(0, 0, 0, 20);
        tarjetaRanking.setLayoutParams(paramsRk);
        GradientDrawable fondoRanking = new GradientDrawable();
        fondoRanking.setColor(Color.parseColor("#00796B"));
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
        labelRanking.setTextColor(Color.parseColor("#B2DFDB"));
        labelRanking.setLetterSpacing(0.1f);

        TextView textoPosicion = new TextView(requireContext());
        textoPosicion.setText("—");
        textoPosicion.setTextSize(30);
        textoPosicion.setTypeface(null, Typeface.BOLD);
        textoPosicion.setTextColor(Color.WHITE);

        TextView textoDetalleRanking = new TextView(requireContext());
        textoDetalleRanking.setText("Cargando...");
        textoDetalleRanking.setTextSize(12);
        textoDetalleRanking.setTextColor(Color.parseColor("#B2DFDB"));

        infoRanking.addView(labelRanking);
        infoRanking.addView(textoPosicion);
        infoRanking.addView(textoDetalleRanking);

        TextView iconoTrofeo = new TextView(requireContext());
        iconoTrofeo.setText("🏆");
        iconoTrofeo.setTextSize(36);

        tarjetaRanking.addView(infoRanking);
        tarjetaRanking.addView(iconoTrofeo);
        layout.addView(tarjetaRanking);

        // ── Llamada al ranking diario ────────────────────────────────
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

        // ── Sección "Perfil" ─────────────────────────────────────────
        layout.addView(crearEtiquetaSeccion("PERFIL"));

        LinearLayout opEditarPerfil = crearOpcion("✏️", "Editar Perfil", "#E8EAF6", "#3F51B5");
        opEditarPerfil.setOnClickListener(v -> irA(new EditarPerfil()));
        layout.addView(opEditarPerfil);

        espacio(layout, 6);

        LinearLayout opMisChats = crearOpcion("💬", "Mis Chats", "#E0F2F1", "#26A69A");
        opMisChats.setOnClickListener(v -> irA(new MisChats()));
        layout.addView(opMisChats);

        espacio(layout, 18);

        // ── Sección "Gestión" ────────────────────────────────────────
        layout.addView(crearEtiquetaSeccion("GESTIÓN"));

        LinearLayout opCrearPuzzle = crearOpcion("➕", "Crear Nuevo Puzzle", "#FCE4EC", "#F06292");
        opCrearPuzzle.setOnClickListener(v -> irA(new RegistrarPuzzle()));
        layout.addView(opCrearPuzzle);

        espacio(layout, 6);

        LinearLayout opGestionPuzzles = crearOpcion("🧩", "Gestionar Puzzles", "#FFF8E1", "#FFA726");
        opGestionPuzzles.setOnClickListener(v -> irA(new GestionPuzzles()));
        layout.addView(opGestionPuzzles);

        espacio(layout, 6);

        LinearLayout opGestionUsuarios = crearOpcion("👥", "Gestionar Usuarios", "#E8F5E9", "#43A047");
        opGestionUsuarios.setOnClickListener(v -> irA(new GestionUsuarios()));
        layout.addView(opGestionUsuarios);

        espacio(layout, 28);

        // ── Divisor ──────────────────────────────────────────────────
        View divisor = new View(requireContext());
        divisor.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divisor.setBackgroundColor(Color.parseColor("#ECEFF1"));
        layout.addView(divisor);
        espacio(layout, 20);

        // ── Botón Cerrar Sesión ──────────────────────────────────────
        Button btnCerrarSesion = crearBotonCerrarSesion("🚪  Cerrar Sesión");
        btnCerrarSesion.setOnClickListener(v ->
                UtilidadesSesion.mostrarDialogoCerrarSesion(requireContext(), () ->
                        UtilidadesSesion.cerrarSesion(requireContext())));
        layout.addView(btnCerrarSesion);

        scroll.addView(layout);
        return scroll;
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void irA(Fragment destino) {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(AppPrincipal.FRAGMENTO_ID, destino)
                .addToBackStack(null)
                .commit();
    }

    /** Etiqueta de sección en mayúsculas con línea inferior */
    private TextView crearEtiquetaSeccion(String texto) {
        TextView label = new TextView(requireContext());
        label.setText(texto);
        label.setTextSize(11);
        label.setTypeface(null, Typeface.BOLD);
        label.setTextColor(Color.parseColor("#90A4AE"));
        label.setLetterSpacing(0.15f);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        p.setMargins(4, 0, 0, 10);
        label.setLayoutParams(p);
        return label;
    }

    private LinearLayout crearOpcion(String emoji, String texto,
                                     String colorFondo, String colorBorde) {
        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(40, 32, 40, 32);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 10);
        fila.setLayoutParams(params);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor(colorFondo));
        forma.setCornerRadius(dpToPx(16));
        forma.setStroke(dpToPx(1), Color.parseColor(colorBorde));
        fila.setBackground(forma);

        TextView emojiTv = new TextView(requireContext());
        emojiTv.setText(emoji);
        emojiTv.setTextSize(22);
        emojiTv.setPadding(0, 0, 24, 0);

        TextView textoTv = new TextView(requireContext());
        textoTv.setText(texto);
        textoTv.setTextSize(15);
        textoTv.setTypeface(null, Typeface.BOLD);
        textoTv.setTextColor(Color.parseColor("#37474F"));
        textoTv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView flecha = new TextView(requireContext());
        flecha.setText("›");
        flecha.setTextSize(22);
        flecha.setTypeface(null, Typeface.BOLD);
        flecha.setTextColor(Color.parseColor(colorBorde));

        fila.addView(emojiTv);
        fila.addView(textoTv);
        fila.addView(flecha);
        return fila;
    }

    private Button crearBotonCerrarSesion(String texto) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.parseColor("#C62828"));
        boton.setTextSize(15);
        boton.setTypeface(null, Typeface.BOLD);
        boton.setAllCaps(false);
        boton.setPadding(0, dpToPx(14), 0, dpToPx(14));
        boton.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        boton.setLayoutParams(params);
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor("#FFEBEE"));
        forma.setCornerRadius(dpToPx(14));
        forma.setStroke(dpToPx(1), Color.parseColor("#EF9A9A"));
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