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

import androidx.appcompat.app.AlertDialog;

import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.chats.MisChats;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.puzzles.GestionPuzzles;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios.GestionUsuarios;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.puzzles.RegistrarPuzzle;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.EditarPerfil;
import com.ignacio_natalia.puzzleteca.repositorios.PuzzleRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

public class PanelAdmin extends Fragment {

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle instanciaEstadoGuardado) {

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        // ── Tarjeta Admin ──
        LinearLayout tarjetaAdministrador = crearTarjeta();
        tarjetaAdministrador.setOrientation(LinearLayout.HORIZONTAL);
        tarjetaAdministrador.setGravity(Gravity.CENTER_VERTICAL);
        tarjetaAdministrador.setPadding(40, 30, 40, 30);

        TextView iconoAdministrador = new TextView(requireContext());
        iconoAdministrador.setText("🛡️");
        iconoAdministrador.setTextSize(36);
        iconoAdministrador.setPadding(0, 0, 24, 0);

        LinearLayout infoAdministrador = new LinearLayout(requireContext());
        infoAdministrador.setOrientation(LinearLayout.VERTICAL);

        TextView textoNombre = new TextView(requireContext());
        textoNombre.setText("Panel Admin");
        textoNombre.setTextSize(18);
        textoNombre.setTypeface(null, Typeface.BOLD);
        textoNombre.setTextColor(Color.parseColor("#37474F"));

        TextView textoId = new TextView(requireContext());
        textoId.setText("Admin ID: " + GestorSesion.obtenerId_usuario(this.requireContext()));
        textoId.setTextSize(13);
        textoId.setTextColor(Color.parseColor("#78909C"));

        infoAdministrador.addView(textoNombre);
        infoAdministrador.addView(textoId);

        tarjetaAdministrador.addView(iconoAdministrador);
        tarjetaAdministrador.addView(infoAdministrador);
        layout.addView(tarjetaAdministrador);

        espacio(layout, 20);

        LinearLayout opEditarPerfil = crearOpcion("✏️", "Editar Perfil");
        opEditarPerfil.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, new EditarPerfil())
                    .addToBackStack(null)
                    .commit();
        });
        layout.addView(opEditarPerfil);

        espacio(layout, 10);

        LinearLayout tarjetaMejorPuzzle = crearTarjeta();
        tarjetaMejorPuzzle.setOrientation(LinearLayout.VERTICAL);
        tarjetaMejorPuzzle.setPadding(40, 30, 40, 30);

        TextView textoMejorPuzzle = new TextView(requireContext());
        textoMejorPuzzle.setText("⭐ Mejor Puzzle");
        textoMejorPuzzle.setTextSize(15);
        textoMejorPuzzle.setTypeface(null, Typeface.BOLD);
        textoMejorPuzzle.setTextColor(Color.parseColor("#37474F"));
        tarjetaMejorPuzzle.addView(textoMejorPuzzle);

        espacio(tarjetaMejorPuzzle, 10);

        LinearLayout filaMejorPuzzle = new LinearLayout(requireContext());
        filaMejorPuzzle.setOrientation(LinearLayout.HORIZONTAL);
        filaMejorPuzzle.setGravity(Gravity.CENTER_VERTICAL);

        TextView textoNombrePuzzle = new TextView(requireContext());
        textoNombrePuzzle.setText("🌸 Flor Amarilla");
        textoNombrePuzzle.setTextSize(14);
        textoNombrePuzzle.setTextColor(Color.parseColor("#37474F"));
        textoNombrePuzzle.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        LinearLayout columnaDerecha = new LinearLayout(requireContext());
        columnaDerecha.setOrientation(LinearLayout.VERTICAL);
        columnaDerecha.setGravity(Gravity.END);

        TextView textoValoracion = new TextView(requireContext());
        textoValoracion.setTextSize(13);
        textoValoracion.setTextColor(Color.parseColor("#78909C"));

        TextView textoTiempo = new TextView(requireContext());
        textoTiempo.setText("--:--");
        textoTiempo.setTextSize(13);
        textoTiempo.setTypeface(null, Typeface.BOLD);
        textoTiempo.setTextColor(Color.WHITE);
        textoTiempo.setPadding(20, 10, 20, 10);

        GradientDrawable formaTiempo = new GradientDrawable();
        formaTiempo.setColor(Color.parseColor("#26A69A"));
        formaTiempo.setCornerRadius(60);
        textoTiempo.setBackground(formaTiempo);

        columnaDerecha.addView(textoValoracion);
        columnaDerecha.addView(textoTiempo);

        filaMejorPuzzle.addView(textoNombrePuzzle);
        filaMejorPuzzle.addView(columnaDerecha);

        tarjetaMejorPuzzle.addView(filaMejorPuzzle);
        layout.addView(tarjetaMejorPuzzle);

        // Cargar el mejor tiempo real desde el backend
        String token = GestorSesion.obtenerToken(requireContext());
        new PuzzleRepositorio().obtenerMejorTiempo(token, new retrofit2.Callback<Integer>() {
            @Override
            public void onResponse(@androidx.annotation.NonNull retrofit2.Call<Integer> call,
                                   @androidx.annotation.NonNull retrofit2.Response<Integer> response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful() && response.body() != null) {
                        int segundos = response.body();
                        int mm = segundos / 60;
                        int ss = segundos % 60;
                        textoTiempo.setText(String.format("%02d:%02d", mm, ss));
                    } else {
                        textoTiempo.setText("N/A");
                    }
                });
            }
            @Override
            public void onFailure(@androidx.annotation.NonNull retrofit2.Call<Integer> call,
                                  @androidx.annotation.NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> textoTiempo.setText("--:--"));
            }
        });

        LinearLayout opMisChats = crearOpcion("💬", "Mis Chats");
        opMisChats.setOnClickListener(vista -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, new MisChats())
                    .addToBackStack(null).commit();
        });
        layout.addView(opMisChats);

        espacio(layout, 10);
        LinearLayout opGestionarCrearPuzzles = crearOpcion("➕", "Crear nuevo puzzle");

        opGestionarCrearPuzzles.setOnClickListener(vista -> {
            Fragment fragment = new RegistrarPuzzle();
            requireActivity().
                    getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        layout.addView(opGestionarCrearPuzzles);

        LinearLayout opGestionarPuzzles = crearOpcion("🧩", "Gestionar Puzzles");

        opGestionarPuzzles.setOnClickListener(vista -> {
            Fragment fragment = new GestionPuzzles();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        layout.addView(opGestionarPuzzles);

        LinearLayout opGestionarUsuarios = crearOpcion("👥", "Gestionar Usuarios");

        opGestionarUsuarios.setOnClickListener(vista -> {
            Fragment fragment = new GestionUsuarios();
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(AppPrincipal.FRAGMENTO_ID, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        layout.addView(opGestionarUsuarios);

        // ── CERRAR SESIÓN (BOTÓN SECUNDARIO) ──
        Button btnCerrarSesion = crearBotonSecundario("Cerrar sesión");

        btnCerrarSesion.setOnClickListener(vista -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Cerrar sesión")
                    .setMessage("¿Seguro que quieres cerrar sesión?")
                    .setPositiveButton("Cerrar sesión", (dialog, which) -> {

                        UtilidadesSesion.cerrarSesion(requireContext());

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        layout.addView(btnCerrarSesion);

        espacio(layout, 10);

        // ── ELIMINAR CUENTA (BOTÓN SECUNDARIO) ──
        Button btnEliminarCuenta = crearBotonSecundario("Eliminar cuenta");

        btnEliminarCuenta.setOnClickListener(vista -> {

            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar cuenta")
                    .setMessage("Esta acción es irreversible. ¿Deseas continuar?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {

                        UtilidadesSesion.eliminarCuenta(requireContext(), null);

                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        layout.addView(btnEliminarCuenta);

        scroll.addView(layout);
        return scroll;
    }
    private LinearLayout crearOpcion(String emoji, String opcion) {

        LinearLayout fila = new LinearLayout(requireContext());
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        fila.setPadding(40, 28, 40, 28);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 10);
        fila.setLayoutParams(params);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        fila.setBackground(forma);

        TextView emojiTv = new TextView(requireContext());
        emojiTv.setText(emoji);
        emojiTv.setTextSize(20);
        emojiTv.setPadding(0, 0, 20, 0);

        TextView textoTv = new TextView(requireContext());
        textoTv.setText(opcion);
        textoTv.setTextSize(15);
        textoTv.setTextColor(Color.parseColor("#37474F"));
        textoTv.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView flecha = new TextView(requireContext());
        flecha.setText("›");
        flecha.setTextSize(20);
        flecha.setTextColor(Color.parseColor("#90A4AE"));

        fila.addView(emojiTv);
        fila.addView(textoTv);
        fila.addView(flecha);

        return fila;
    }
    private Button crearBotonSecundario(String texto) {

        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.RED);
        boton.setTextSize(14);
        boton.setAllCaps(false);
        boton.setPadding(30, 20, 30, 20);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 20, 0, 0);
        boton.setLayoutParams(params);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor("#FFF9C4"));
        forma.setCornerRadius(60);
        forma.setStroke(2, Color.parseColor("#F0CC50"));

        boton.setBackground(forma);

        return boton;
    }
    private LinearLayout crearTarjeta() {

        LinearLayout tarjeta = new LinearLayout(requireContext());

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        params.setMargins(0, 0, 0, 14);
        tarjeta.setLayoutParams(params);

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));

        tarjeta.setBackground(forma);

        return tarjeta;
    }
    private void espacio(LinearLayout layout, int dp) {

        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));

        layout.addView(v);
    }
}