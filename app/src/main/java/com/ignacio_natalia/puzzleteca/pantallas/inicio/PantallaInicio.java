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
import com.ignacio_natalia.puzzleteca.pantallas.registro.RecuperarContrasenna;
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

        FrameLayout.LayoutParams tituloParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        tituloParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;

        titulo.setLayoutParams(tituloParams);

        // ---------- CONTENEDOR BOTONES ----------
        LinearLayout contenedorBotones = new LinearLayout(this);
        contenedorBotones.setOrientation(LinearLayout.VERTICAL);
        contenedorBotones.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams botonesContainerParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        botonesContainerParams.gravity = Gravity.CENTER;

        contenedorBotones.setLayoutParams(botonesContainerParams);

        LinearLayout.LayoutParams botonesParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        botonesParams.setMargins(80, 25, 80, 25);

        // Botón Login
        Button btnLogin = crearBoton("Iniciar sesión", "#F06292");
        btnLogin.setLayoutParams(botonesParams);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Botón Registrarse
        Button btnRegister = crearBoton("Registrarse", "#F06292");
        btnRegister.setLayoutParams(botonesParams);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Botón continuar como invitado
        TextView txtGuest = new TextView(this);
        txtGuest.setText("Continuar como invitado");
        txtGuest.setTextColor(Color.parseColor("#455A64"));
        txtGuest.setTextSize(18);
        txtGuest.setGravity(Gravity.CENTER);
        txtGuest.setPadding(20, 30, 20, 0);

        txtGuest.setOnClickListener(v -> {
            // Navegar a la pantalla principal
            // Intent intent = new Intent(PantallaInicio.this, MainActivity.class);
            // startActivity(intent);
        });

        // Separador
        View separador = new View(this);
        LinearLayout.LayoutParams separadorParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        3
                );
        separadorParams.setMargins(250, 75, 250, 75);
        separador.setLayoutParams(separadorParams);
        separador.setBackgroundColor(Color.DKGRAY);

        // Botón recuperar contraseña
        Button btnForgot = crearBoton("¿Has olvidado la contraseña?", "#26A69A");
        btnForgot.setLayoutParams(botonesParams);
        btnForgot.setOnClickListener(view -> {
            Intent intent = new Intent(PantallaInicio.this, RecuperarContrasenna.class);
            startActivity(intent);
        });

        contenedorBotones.addView(btnLogin);
        contenedorBotones.addView(btnRegister);
        contenedorBotones.addView(txtGuest);
        contenedorBotones.addView(separador);
        contenedorBotones.addView(btnForgot);

        // Añadir elementos
        layout.addView(titulo);
        layout.addView(contenedorBotones);

        setContentView(layout);
    }

    private Button crearBoton(String texto, String colorHex) {

        Button btn = new Button(this);
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(20);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(60);
        shape.setColor(Color.parseColor(colorHex));

        btn.setBackground(shape);

        return btn;
    }
}