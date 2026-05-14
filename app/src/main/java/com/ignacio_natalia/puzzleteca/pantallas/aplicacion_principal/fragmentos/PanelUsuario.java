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
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats.MisChats;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.RegistrarPuzzle;
import com.ignacio_natalia.puzzleteca.modelos.RankingUsuario;
import com.ignacio_natalia.puzzleteca.repositorios.RankingRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import java.util.List;

public class PanelUsuario extends Fragment {

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
        layout.setPadding(50, 50, 50, 50);

        // ── Cabecera con saludo ──────────────────────────────────────
        LinearLayout cabecera = new LinearLayout(requireContext());
        cabecera.setOrientation(LinearLayout.HORIZONTAL);
        cabecera.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams paramsCab = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsCab.setMargins(0, 0, 0, 30);
        cabecera.setLayoutParams(paramsCab);

        LinearLayout textosCabecera = new LinearLayout(requireContext());
        textosCabecera.setOrientation(LinearLayout.VERTICAL);
        textosCabecera.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView textoSaludo = new TextView(requireContext());
        textoSaludo.setText("¡Hola, " + nombre + "! 👋");
        textoSaludo.setTextSize(22);
        textoSaludo.setTypeface(null, Typeface.BOLD);
        textoSaludo.setTextColor(Color.parseColor("#37474F"));

        TextView textoBienvenida = new TextView(requireContext());
        textoBienvenida.setText("¿Qué puzzle resolvemos hoy?");
        textoBienvenida.setTextSize(13);
        textoBienvenida.setTextColor(Color.parseColor("#90A4AE"));

        textosCabecera.addView(textoSaludo);
        textosCabecera.addView(textoBienvenida);

        // Avatar circular con inicial
        TextView avatar = new TextView(requireContext());
        avatar.setText(nombre.isEmpty() ? "U" : nombre.substring(0, 1).toUpperCase());
        avatar.setTextSize(20);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setTextColor(Color.WHITE);
        avatar.setGravity(Gravity.CENTER);
        int tamAvatar = dpToPx(48);
        LinearLayout.LayoutParams paramsAv = new LinearLayout.LayoutParams(tamAvatar, tamAvatar);
        paramsAv.setMargins(16, 0, 0, 0);
        avatar.setLayoutParams(paramsAv);
        GradientDrawable fondoAvatar = new GradientDrawable();
        fondoAvatar.setShape(GradientDrawable.OVAL);
        fondoAvatar.setColor(Color.parseColor("#26A69A"));
        avatar.setBackground(fondoAvatar);

        cabecera.addView(textosCabecera);
        cabecera.addView(avatar);
        layout.addView(cabecera);

        // ── Tarjeta de Ranking ───────────────────────────────────────
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
                            String media = miEntrada.getMediaDiaria() != null
                                    ? String.format("%.1f", miEntrada.getMediaDiaria()) : "—";
                            textoDetalleRanking.setText("Media: " + media + " · de " + ranking.size() + " jugadores");
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

        // ── Fila de acciones rápidas (Mis Puzzles + Editar Perfil) ───
        LinearLayout filaAcciones = new LinearLayout(requireContext());
        filaAcciones.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams paramsFila = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsFila.setMargins(0, 0, 0, 20);
        filaAcciones.setLayoutParams(paramsFila);

        LinearLayout cardMisPuzzles = crearCardAccion("🧩", "Mis Puzzles", "#FCE4EC", "#F06292");
        LinearLayout.LayoutParams paramsC1 = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        paramsC1.setMargins(0, 0, 10, 0);
        cardMisPuzzles.setLayoutParams(paramsC1);
        cardMisPuzzles.setOnClickListener(v -> irA(new MisPuzzles(), com.ignacio_natalia.puzzleteca.R.drawable.titulo_mis_puzzles_recortado));

        LinearLayout cardEditarPerfil = crearCardAccion("✏️", "Editar Perfil", "#E0F2F1", "#26A69A");
        cardEditarPerfil.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        cardEditarPerfil.setOnClickListener(v -> irA(new EditarPerfil(), 0));

        filaAcciones.addView(cardMisPuzzles);
        filaAcciones.addView(cardEditarPerfil);
        layout.addView(filaAcciones);

        // ── Botón Mis Chats ──────────────────────────────────────────
        Button botonMisChats = crearBotonPrimario("💬  Mis Chats", "#26A69A");
        botonMisChats.setOnClickListener(v -> irA(new MisChats(), 0));
        layout.addView(botonMisChats);
        espacio(layout, 14);

        // ── Botón Crear Nuevo Puzzle ─────────────────────────────────
        Button botonCrearPuzzle = crearBotonPrimario("➕  Crear Nuevo Puzzle", "#F06292");
        botonCrearPuzzle.setOnClickListener(v -> irA(new RegistrarPuzzle(), 0));
        layout.addView(botonCrearPuzzle);
        espacio(layout, 28);

        // ── Divisor ──────────────────────────────────────────────────
        View divisor = new View(requireContext());
        divisor.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1));
        divisor.setBackgroundColor(Color.parseColor("#ECEFF1"));
        layout.addView(divisor);
        espacio(layout, 20);

        // ── Botón Cerrar Sesión ──────────────────────────────────────
        Button botonCerrarSesion = crearBotonCerrarSesion("🚪  Cerrar Sesión");
        botonCerrarSesion.setOnClickListener(v ->
                UtilidadesSesion.mostrarDialogoCerrarSesion(requireContext(), () ->
                        UtilidadesSesion.cerrarSesion(requireContext())));
        layout.addView(botonCerrarSesion);

        scroll.addView(layout);
        return scroll;
    }

    // ── Helpers ──────────────────────────────────────────────────────

    private void irA(Fragment destino, int tituloDrawable) {
        AppPrincipal activity = (AppPrincipal) requireActivity();
        if (tituloDrawable != 0) {
            activity.actualizarTituloPantalla(tituloDrawable);
        }
        activity.getSupportFragmentManager()
                .beginTransaction()
                .replace(AppPrincipal.FRAGMENTO_ID, destino)
                .addToBackStack(null)
                .commit();
    }

    /** Tarjeta pequeña con emoji, texto y borde de color */
    private LinearLayout crearCardAccion(String emoji, String texto,
                                         String colorFondo, String colorBorde) {
        LinearLayout card = new LinearLayout(requireContext());
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(20, 35, 20, 35);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor(colorFondo));
        forma.setCornerRadius(dpToPx(16));
        forma.setStroke(dpToPx(1), Color.parseColor(colorBorde));
        card.setBackground(forma);

        TextView textoEmoji = new TextView(requireContext());
        textoEmoji.setText(emoji);
        textoEmoji.setTextSize(28);
        textoEmoji.setGravity(Gravity.CENTER);

        TextView textoLabel = new TextView(requireContext());
        textoLabel.setText(texto);
        textoLabel.setTextSize(13);
        textoLabel.setTypeface(null, Typeface.BOLD);
        textoLabel.setTextColor(Color.parseColor("#37474F"));
        textoLabel.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pl.setMargins(0, 10, 0, 0);
        textoLabel.setLayoutParams(pl);

        card.addView(textoEmoji);
        card.addView(textoLabel);
        return card;
    }

    private Button crearBotonPrimario(String texto, String color) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(15);
        boton.setTypeface(null, Typeface.BOLD);
        boton.setAllCaps(false);
        boton.setPadding(30, 30, 30, 30);
        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor(color));
        forma.setCornerRadius(dpToPx(14));
        boton.setBackground(forma);
        return boton;
    }

    private Button crearBotonCerrarSesion(String texto) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.parseColor("#C62828"));
        boton.setTextSize(15);
        boton.setTypeface(null, Typeface.BOLD);
        boton.setAllCaps(false);
        boton.setPadding(dpToPx(16), dpToPx(14), dpToPx(16), dpToPx(14));
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