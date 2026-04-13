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

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RecuperarContrasennaActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class PantallaInicio extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#DFF5C9")); // color personalizado

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
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
        Button botonLogin = crearBoton("Iniciar sesión", "#F06292");
        botonLogin.setLayoutParams(parametrosBotones);
        botonLogin.setOnClickListener(vista -> {
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Botón Registrarse
        Button botonRegistro = crearBoton("Registrarse", "#F06292");
        botonRegistro.setLayoutParams(parametrosBotones);
        botonRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Botón continuar como invitado
        TextView textoInvitado = new TextView(this);
        textoInvitado.setText("Continuar como invitado");
        textoInvitado.setTextColor(Color.parseColor("#455A64"));
        textoInvitado.setTextSize(18);
        textoInvitado.setGravity(Gravity.CENTER);
        textoInvitado.setPadding(20, 30, 20, 0);

        textoInvitado.setOnClickListener(vista -> {
//             Navegar a la pantalla principal
//             Intent intent = new Intent(PantallaInicio.this, MainActivity.class);
//             startActivity(intent);
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
        Button botonOlvidada = crearBoton("¿Has olvidado la contraseña?", "#26A69A");
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

        // Añadir elementos
        layout.addView(titulo);
        layout.addView(contenedorBotones);

        setContentView(layout);
    }

    private Button crearBoton(String texto, String color) {

        Button boton = new Button(this);
        boton.setText(texto);
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(20);
        boton.setAllCaps(false);
        boton.setPadding(20, 30, 20, 30);

        GradientDrawable forma = new GradientDrawable();
        forma.setCornerRadius(60);
        forma.setColor(Color.parseColor(color));

        boton.setBackground(forma);

        return boton;
    }

}