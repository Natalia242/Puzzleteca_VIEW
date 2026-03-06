package com.ignacio_natalia.puzzleteca.pantallas.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class PantallaInicio extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );
        fondo.setCornerRadius(40);

        // Layout principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);
        layout.setBackground(fondo);

        // Contenedor superior (título)
        LinearLayout contenedorArriba = getLinearLayout();

        // Contenedor de botones
        LinearLayout contenedorBotones = new LinearLayout(this);
        contenedorBotones.setOrientation(LinearLayout.VERTICAL);
        contenedorBotones.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams botonesParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        botonesParams.setMargins(100, 25, 100, 25);

        // Botón Registrarse
        Button btnRegister = crearBoton("Registrarse", "#F06292");
        btnRegister.setLayoutParams(botonesParams);
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Botón Iniciar sesión
        Button btnLogin = crearBoton("Iniciar sesión", "#F06292");
        btnLogin.setLayoutParams(botonesParams);
        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(PantallaInicio.this, LoginActivity.class);
            startActivity(intent);
        });

        // Separador
        View separador = new View(this);
        LinearLayout.LayoutParams separadorParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        4);
        separadorParams.setMargins(250, 75, 250, 75);
        separador.setLayoutParams(separadorParams);
        separador.setBackgroundColor(Color.DKGRAY);

        // Botón recuperar contraseña
        Button btnForgot = crearBoton("¿Has olvidado la contraseña?", "#26A69A");

        LinearLayout.LayoutParams forgotParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        forgotParams.setMargins(20, 25, 20, 25);
        btnForgot.setLayoutParams(forgotParams);

        // Añadir botones
        contenedorBotones.addView(btnRegister);
        contenedorBotones.addView(btnLogin);
        contenedorBotones.addView(separador);
        contenedorBotones.addView(btnForgot);

        // Añadir al layout principal
        layout.addView(contenedorArriba);
        layout.addView(contenedorBotones);

        setContentView(layout);
    }

    @NonNull
    private LinearLayout getLinearLayout() {
        LinearLayout contenedorArriba = new LinearLayout(this);
        contenedorArriba.setOrientation(LinearLayout.VERTICAL);
        contenedorArriba.setGravity(Gravity.CENTER_HORIZONTAL);

        LinearLayout.LayoutParams arribaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        contenedorArriba.setLayoutParams(arribaParams);

        // Imagen título
        ImageView titulo = new ImageView(this);
        titulo.setImageResource(R.drawable.titulo);

        LinearLayout.LayoutParams tituloParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

        tituloParams.setMargins(0, -300, 0, -100);
        titulo.setLayoutParams(tituloParams);

        contenedorArriba.addView(titulo);
        return contenedorArriba;
    }

    private Button crearBoton(String texto, String colorHex) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(20);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(55);
        shape.setColor(Color.parseColor(colorHex));

        btn.setBackground(shape);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);

        return btn;
    }
}