package com.ignacio_natalia.puzzleteca.pantallas.inicio;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class PantallaInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Crear el LinearLayout principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);

        // Centrar los botones
        layout.setGravity(android.view.Gravity.CENTER);

        // Parámetros de diseño para los botones
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 30, 0, 30);

        // Botón "Iniciar sesión"
        Button btnIniciarSesion = new Button(this);
        btnIniciarSesion.setText("Iniciar sesión");
        btnIniciarSesion.setLayoutParams(params);
        btnIniciarSesion.setOnClickListener(v -> {
            // Redirigir a LoginActivity
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Botón "Registrarse"
        Button btnRegistrarse = new Button(this);
        btnRegistrarse.setText("Registrarse");
        btnRegistrarse.setLayoutParams(params);
        btnRegistrarse.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Añadir los botones al layout
        layout.addView(btnIniciarSesion);
        layout.addView(btnRegistrarse);

        // Establecer el contenido de la actividad
        setContentView(layout);
    }
}