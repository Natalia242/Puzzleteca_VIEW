package com.ignacio_natalia.puzzleteca.pantallas.inicio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RecuperarContrasennaActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class PantallaInicio extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.app_green_light));

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, R.color.app_green_light),
                        ContextCompat.getColor(this, R.color.app_green_medium)
                }
        );

        // Layout principal
        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);
        layout.setPadding(40, 60, 40, 40);

        // ---------- TITULO ARRIBA ----------
        ImageView titulo = new ImageView(this);
        titulo.setImageResource(R.drawable.titulo);
        titulo.setAdjustViewBounds(true);

        FrameLayout.LayoutParams parametrosTitulo =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        parametrosTitulo.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titulo.setLayoutParams(parametrosTitulo);

        // ---------- CONTENEDOR BOTONES ----------
        LinearLayout contenedorBotones = new LinearLayout(this);
        contenedorBotones.setOrientation(LinearLayout.VERTICAL);
        contenedorBotones.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams parametrosContenedorBotones =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        parametrosContenedorBotones.gravity = Gravity.CENTER;
        contenedorBotones.setLayoutParams(parametrosContenedorBotones);

        LinearLayout.LayoutParams parametrosBotones =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        parametrosBotones.setMargins(80, 25, 80, 25);

        // Botón Login
        Button botonLogin = crearBoton("Iniciar sesión", R.color.app_rosa);
        botonLogin.setLayoutParams(parametrosBotones);
        botonLogin.setOnClickListener(vista -> {
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Botón Registrarse
        Button botonRegistro = crearBoton("Registrarse", R.color.app_rosa);
        botonRegistro.setLayoutParams(parametrosBotones);
        botonRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Botón continuar como invitado
        TextView textoInvitado = new TextView(this);
        textoInvitado.setText("Continuar como invitado");
        textoInvitado.setTextColor(ContextCompat.getColor(this, R.color.app_texto_link));
        textoInvitado.setTextSize(18);
        textoInvitado.setGravity(Gravity.CENTER);
        textoInvitado.setPadding(20, 30, 20, 0);

        textoInvitado.setOnClickListener(vista -> {
            com.ignacio_natalia.puzzleteca.utilidades.GestorSesion.iniciarSesionInvitado(PantallaInicio.this);
            Intent intent = new Intent(PantallaInicio.this,
                    com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal.class);
            startActivity(intent);
        });

        // Separador
        View separador = new View(this);
        LinearLayout.LayoutParams parametrosSeparador =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3
                );
        parametrosSeparador.setMargins(250, 75, 250, 75);
        separador.setLayoutParams(parametrosSeparador);
        separador.setBackgroundColor(Color.DKGRAY);

        // Botón recuperar contraseña
        Button botonOlvidada = crearBoton("¿Has olvidado la contraseña?", R.color.app_teal);
        botonOlvidada.setLayoutParams(parametrosBotones);
        botonOlvidada.setOnClickListener(view -> {
            Intent intent = new Intent(PantallaInicio.this, RecuperarContrasennaActivity.class);
            startActivity(intent);
        });

        contenedorBotones.addView(botonLogin);
        contenedorBotones.addView(botonRegistro);
        contenedorBotones.addView(textoInvitado);
        contenedorBotones.addView(separador);
        contenedorBotones.addView(botonOlvidada);

        layout.addView(titulo);
        layout.addView(contenedorBotones);

        setContentView(layout);
    }

    private Button crearBoton(String texto, int colorRes) {
        Button boton = new Button(this);
        boton.setText(texto);
        boton.setTextColor(ContextCompat.getColor(this, R.color.white));
        boton.setTextSize(20);
        boton.setAllCaps(false);
        boton.setPadding(20, 30, 20, 30);

        GradientDrawable forma = new GradientDrawable();
        forma.setCornerRadius(60);
        forma.setColor(ContextCompat.getColor(this, colorRes));

        boton.setBackground(forma);
        return boton;
    }
}