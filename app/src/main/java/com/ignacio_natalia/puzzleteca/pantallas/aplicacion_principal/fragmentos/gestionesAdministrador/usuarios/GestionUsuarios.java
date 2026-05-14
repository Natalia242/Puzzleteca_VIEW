package com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.fragmentos.gestionesAdministrador.usuarios;

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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

import java.util.List;

public class GestionUsuarios extends Fragment {

    private GestionUsuarioViewModel viewModel;
    private LinearLayout contenedorUsuarios;

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ScrollView scroll = new ScrollView(requireContext());
        scroll.setFillViewport(true);

        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        TextView titulo = new TextView(requireContext());
        titulo.setText("👥 Gestión de Usuarios");
        titulo.setTextSize(20);
        titulo.setTypeface(null, Typeface.BOLD);
        titulo.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));
        titulo.setGravity(Gravity.CENTER);
        layout.addView(titulo);

        espacio(layout, 30);

        contenedorUsuarios = new LinearLayout(requireContext());
        contenedorUsuarios.setOrientation(LinearLayout.VERTICAL);
        layout.addView(contenedorUsuarios);

        viewModel = new ViewModelProvider(this).get(GestionUsuarioViewModel.class);
        viewModel.getUsuarios().observe(getViewLifecycleOwner(), this::renderizarUsuarios);
        viewModel.getError().observe(getViewLifecycleOwner(), error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show());

        String token = GestorSesion.obtenerToken(this.requireContext());
        String email = GestorSesion.obtenerEmail(this.requireContext());
        viewModel.cargarUsuarios(token, email);

        scroll.addView(layout);
        return scroll;
    }

    @SuppressLint("SetTextI18n")
    private void renderizarUsuarios(List<Usuario> usuarios) {
        contenedorUsuarios.removeAllViews();
        if (usuarios == null || usuarios.isEmpty()) {
            TextView vacio = new TextView(requireContext());
            vacio.setText("No hay más usuarios");
            vacio.setTextColor(Color.GRAY);
            contenedorUsuarios.addView(vacio);
            return;
        }
        for (Usuario u : usuarios) {
            contenedorUsuarios.addView(crearTarjetaUsuario(u));
            espacio(contenedorUsuarios, 16);
        }
    }

    @SuppressLint("SetTextI18n")
    private View crearTarjetaUsuario(Usuario usuario) {
        LinearLayout tarjeta = new LinearLayout(requireContext());
        tarjeta.setOrientation(LinearLayout.HORIZONTAL);
        tarjeta.setGravity(Gravity.CENTER_VERTICAL);
        tarjeta.setPadding(40, 30, 40, 30);

        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(ContextCompat.getColor(requireContext(), R.color.white));
        fondo.setCornerRadius(40);
        fondo.setStroke(2, ContextCompat.getColor(requireContext(), R.color.app_green_border));
        tarjeta.setBackground(fondo);
        tarjeta.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        LinearLayout info = new LinearLayout(requireContext());
        info.setOrientation(LinearLayout.VERTICAL);
        info.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        TextView nombre = new TextView(requireContext());
        nombre.setText("👤 " + usuario.getNombre() + " " + usuario.getApellido());
        nombre.setTextSize(15);
        nombre.setTypeface(null, Typeface.BOLD);
        nombre.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_texto));

        TextView estado = new TextView(requireContext());
        estado.setTextSize(13);

        info.addView(nombre);
        info.addView(estado);

        Spinner spinnerTipo = new Spinner(requireContext());
        Usuario.TipoUsuario[] tipos = Usuario.TipoUsuario.values();
        ArrayAdapter<Usuario.TipoUsuario> adapter = new ArrayAdapter<>(
                requireContext(), android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);
        spinnerTipo.setSelection(usuario.getTipoUsuario().ordinal());
        actualizarEstadoUI(estado, usuario.getTipoUsuario().name());

        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean inicializado = false;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!inicializado) { inicializado = true; return; }
                Usuario.TipoUsuario seleccionado = tipos[position];
                usuario.setTipoUsuario(seleccionado);
                actualizarEstadoUI(estado, seleccionado.name());
                viewModel.actualizarEstadoUsuario(usuario.getEmail(), seleccionado.name());
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        tarjeta.addView(info);
        tarjeta.addView(spinnerTipo);
        return tarjeta;
    }

    @SuppressLint("SetTextI18n")
    private void actualizarEstadoUI(TextView estado, String tipo) {
        switch (tipo) {
            case "Bloqueado":
                estado.setText("Estado: Bloqueado");
                estado.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_peligro));
                break;
            case "Admin":
                estado.setText("Estado: Admin");
                estado.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_admin_accent));
                break;
            case "Usuario":
                estado.setText("Estado: Usuario");
                estado.setTextColor(ContextCompat.getColor(requireContext(), R.color.app_green_success_text));
                break;
        }
    }

    private void espacio(LinearLayout layout, int dp) {
        View v = new View(requireContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp));
        layout.addView(v);
    }
}