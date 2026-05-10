package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos;

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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.ignacio_natalia.puzzleteca.modelos.ActualizarUsuarioRequest;
import com.ignacio_natalia.puzzleteca.repositorios.UsuarioRepositorio;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;
import com.ignacio_natalia.puzzleteca.utilidades.UtilidadesSesion;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditarPerfil extends Fragment {

    private UsuarioRepositorio repositorio;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup contenedor,
                             @Nullable Bundle instanciaEstadoGuardado) {

        repositorio = new UsuarioRepositorio();

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);

        // ── Título ──
        TextView titulo = new TextView(requireContext());
        titulo.setText("✏️ Editar Perfil");
        titulo.setTextSize(22);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(Color.parseColor("#37474F"));
        titulo.setGravity(Gravity.CENTER);
        layout.addView(titulo);
        espacio(layout, 30);

        // ── Tarjeta formulario ──
        LinearLayout tarjeta = crearTarjeta();
        tarjeta.setOrientation(LinearLayout.VERTICAL);
        tarjeta.setPadding(50, 50, 50, 50);

        // Campo Nombre
        TextView labelNombre = crearLabel("Nuevo nombre");
        EditText campoNombre = crearCampoTexto("Escribe tu nombre");

        // Campo Apellido
        TextView labelApellido = crearLabel("Nuevo apellido");
        EditText campoApellido = crearCampoTexto("Escribe tu apellido");

        tarjeta.addView(labelNombre);
        espacio(tarjeta, 8);
        tarjeta.addView(campoNombre);
        espacio(tarjeta, 24);
        tarjeta.addView(labelApellido);
        espacio(tarjeta, 8);
        tarjeta.addView(campoApellido);

        layout.addView(tarjeta);
        espacio(layout, 30);

        // ── Botón Guardar ──
        Button botonGuardar = crearBotonPrimario("💾 Guardar Cambios", "#26A69A");
        botonGuardar.setOnClickListener(vista -> {
            String nuevoNombre = campoNombre.getText().toString().trim();
            String nuevoApellido = campoApellido.getText().toString().trim();

            boolean nombreRelleno = !nuevoNombre.isEmpty();
            boolean apellidoRelleno = !nuevoApellido.isEmpty();

            if (!nombreRelleno && !apellidoRelleno) {
                Toast.makeText(requireContext(),
                        "Rellena al menos el nombre o el apellido.", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = GestorSesion.obtenerEmail(requireContext());
            if (email == null) {
                Toast.makeText(requireContext(),
                        "No se pudo obtener tu sesión.", Toast.LENGTH_SHORT).show();
                return;
            }

            botonGuardar.setEnabled(false);
            botonGuardar.setText("Guardando…");

            // Si rellenó nombre, actualizar nombre
            if (nombreRelleno) {
                actualizarAtributo(email, "nombre", nuevoNombre, apellidoRelleno ? () -> {
                    // Después de nombre, actualizar apellido
                    actualizarAtributo(email, "apellido", nuevoApellido, () -> {
                        mostrarExito(botonGuardar);
                    }, botonGuardar);
                } : () -> mostrarExito(botonGuardar), botonGuardar);
            } else {
                // Solo apellido
                actualizarAtributo(email, "apellido", nuevoApellido, () -> {
                    mostrarExito(botonGuardar);
                }, botonGuardar);
            }
        });
        layout.addView(botonGuardar);
        espacio(layout, 14);

        // ── Botón Cancelar ──
        Button botonCancelar = crearBotonSecundario("← Volver");
        botonCancelar.setOnClickListener(vista -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        layout.addView(botonCancelar);

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

    // ────────────────────────────────────────────────────────────────────────
    // Helpers internos
    // ────────────────────────────────────────────────────────────────────────

    private void actualizarAtributo(String email, String atributo, String valor,
                                    Runnable onExito, Button boton) {
        ActualizarUsuarioRequest request = new ActualizarUsuarioRequest(email, atributo, valor);
        repositorio.actualizarPerfil(request, new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (response.isSuccessful()) {
                        onExito.run();
                    } else {
                        boton.setEnabled(true);
                        boton.setText("💾 Guardar Cambios");
                        String msg = response.code() == 409
                                ? "El " + atributo + " ya tiene ese valor."
                                : "Error al actualizar el " + atributo + " (" + response.code() + ").";
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    boton.setEnabled(true);
                    boton.setText("💾 Guardar Cambios");
                    Toast.makeText(requireContext(),
                            "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void mostrarExito(Button boton) {
        if (!isAdded()) return;
        boton.setEnabled(true);
        boton.setText("💾 Guardar Cambios");
        new AlertDialog.Builder(requireContext())
                .setTitle("✅ Perfil actualizado")
                .setMessage("Los cambios se han guardado correctamente.")
                .setPositiveButton("Aceptar", (d, w) -> {
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .show();
    }

    // ── Utilidades UI ──

    private TextView crearLabel(String texto) {
        TextView label = new TextView(requireContext());
        label.setText(texto);
        label.setTextSize(14);
        label.setTypeface(null, Typeface.BOLD);
        label.setTextColor(Color.parseColor("#37474F"));
        return label;
    }

    private EditText crearCampoTexto(String hint) {
        EditText campo = new EditText(requireContext());
        campo.setHint(hint);
        campo.setTextSize(15);
        campo.setTextColor(Color.parseColor("#263238"));
        campo.setHintTextColor(Color.parseColor("#90A4AE"));
        campo.setPadding(30, 25, 30, 25);
        campo.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor("#F5F5F5"));
        forma.setCornerRadius(30);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        campo.setBackground(forma);

        return campo;
    }

    private LinearLayout crearTarjeta() {
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.WHITE);
        forma.setCornerRadius(40);
        forma.setStroke(2, Color.parseColor("#A5D6A7"));
        tarjeta.setBackground(forma);
        return tarjeta;
    }

    private Button crearBotonPrimario(String texto, String color) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(16);
        boton.setAllCaps(false);
        boton.setPadding(30, 30, 30, 30);
        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor(color));
        forma.setCornerRadius(60);
        boton.setBackground(forma);
        return boton;
    }

    private Button crearBotonSecundario(String texto) {
        Button boton = new Button(requireContext());
        boton.setText(texto);
        boton.setTextColor(Color.parseColor("#37474F"));
        boton.setTextSize(14);
        boton.setAllCaps(false);
        boton.setPadding(30, 20, 30, 20);
        boton.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(Color.parseColor("#ECEFF1"));
        forma.setCornerRadius(60);
        forma.setStroke(2, Color.parseColor("#B0BEC5"));
        boton.setBackground(forma);
        return boton;
    }

    private void espacio(LinearLayout layout, int dp) {
        View vista = new View(requireContext());
        vista.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(vista);
    }
}