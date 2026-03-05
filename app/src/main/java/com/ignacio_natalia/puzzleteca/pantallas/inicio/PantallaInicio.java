package com.ignacio_natalia.puzzleteca.pantallas.inicio;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
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

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );
        fondo.setCornerRadius(40);

        // Layout principal vertical
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);
        layout.setBackground(fondo);

        // Contenedor para título y bienvenida (arriba)
        LinearLayout contenedorArriba = new LinearLayout(this);
        contenedorArriba.setOrientation(LinearLayout.VERTICAL);
        contenedorArriba.setGravity(Gravity.CENTER_HORIZONTAL);
        LinearLayout.LayoutParams arribaParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        contenedorArriba.setLayoutParams(arribaParams);

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
        tituloParams.setMargins(0, 0, 0, 20);
        titulo.setLayoutParams(tituloParams);

        // Texto Welcome
        TextView welcome = new TextView(this);
        welcome.setText("¡Bienvenido!");
        welcome.setTextSize(24);
        welcome.setTextColor(Color.DKGRAY);
        welcome.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams welcomeParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        welcomeParams.setMargins(0, 0, 0, 80);
        welcome.setLayoutParams(welcomeParams);

        // Agregar título y welcome al contenedor arriba
        contenedorArriba.addView(titulo);
        contenedorArriba.addView(welcome);

        // Spacer arriba (para empujar botones hacia el centro)
        View spacerArriba = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f);  // peso 1 para ocupar espacio flexible
        spacerArriba.setLayoutParams(spacerParams);

        // Contenedor botones (sin peso, tamaño wrap_content)
        LinearLayout contenedorBotones = new LinearLayout(this);
        contenedorBotones.setOrientation(LinearLayout.VERTICAL);
        contenedorBotones.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams botonesParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        botonesParams.setMargins(0, 25, 0, 25);

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

        // Botón ¿Has olvidado la contraseña? con margen superior extra para separarlo
        Button btnForgot = crearBoton("¿Has olvidado la contraseña?", "#26A69A");
        LinearLayout.LayoutParams forgotParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        forgotParams.setMargins(0, 25, 0, 25); // margen superior más grande para separación
        btnForgot.setLayoutParams(forgotParams);

        // Línea divisoria entre botones rosa y azul
        View separador = new View(this);
        LinearLayout.LayoutParams separadorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                4  // un poco más gruesa para que se note
        );
        separadorParams.setMargins(200, 100, 200, 100); // espacio arriba y abajo
        separador.setLayoutParams(separadorParams);
        separador.setBackgroundColor(Color.DKGRAY); // color más oscuro para que se vea

        // Añadir botones al contenedor
        contenedorBotones.addView(btnRegister);
        contenedorBotones.addView(btnLogin);
        contenedorBotones.addView(separador);
        contenedorBotones.addView(btnForgot);

        // Spacer abajo (equilibrar espacio)
        View spacerAbajo = new View(this);
        spacerAbajo.setLayoutParams(spacerParams);

        // Agregar las cosas al layout principal
        layout.addView(contenedorArriba);
        layout.addView(spacerArriba);
        layout.addView(contenedorBotones);
        layout.addView(spacerAbajo);

        setContentView(layout);
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

        // Padding original para botones
        btn.setPadding(20, 30, 20, 30);

        return btn;
    }
}