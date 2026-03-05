package com.ignacio_natalia.puzzleteca.pantallas.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class PantallaInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(60,120,60,60);

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );
        fondo.setCornerRadius(40);
        layout.setBackground(fondo);

        // Título
        TextView titulo = new TextView(this);
        titulo.setText("🧩 Puzzleteca");
        titulo.setTextSize(28);
        titulo.setTextColor(Color.DKGRAY);
        titulo.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.setMargins(0,0,0,80);

        titulo.setLayoutParams(tituloParams);

        // Texto Welcome
        TextView welcome = new TextView(this);
        welcome.setText("¡Bienvenido!");
        welcome.setTextSize(24);
        welcome.setTextColor(Color.DKGRAY);

        LinearLayout.LayoutParams welcomeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        welcomeParams.setMargins(0,0,0,80);

        welcome.setLayoutParams(welcomeParams);

        // Parámetros botones
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0,25,0,25);

        // Botón Register
        Button btnRegister = crearBoton("Registrarse", "#F06292");
        btnRegister.setLayoutParams(params);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Botón Login
        Button btnLogin = crearBoton("Iniciar sesión", "#F06292");
        btnLogin.setLayoutParams(params);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Botón Forgot Password
        Button btnForgot = crearBoton("¿Has olvidado la contraseña?", "#26A69A");
        btnForgot.setLayoutParams(params);

        // Añadir elementos
        layout.addView(titulo);
        layout.addView(welcome);
        layout.addView(btnRegister);
        layout.addView(btnLogin);
        layout.addView(btnForgot);

        setContentView(layout);
    }

    // Método para crear botones con estilo
    private Button crearBoton(String texto, String colorHex) {

        Button btn = new Button(this);
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(16);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(50);
        shape.setColor(Color.parseColor(colorHex));

        btn.setBackground(shape);

        btn.setPadding(20,30,20,30);

        return btn;
    }
}