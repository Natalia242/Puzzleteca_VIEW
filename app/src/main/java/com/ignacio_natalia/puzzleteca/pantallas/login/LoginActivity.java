package com.ignacio_natalia.puzzleteca.pantallas.login;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fondo degradado igual que la pantalla de inicio
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );
        fondo.setCornerRadius(40);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 120, 60, 60);
        layout.setBackground(fondo);

        // Contenedor superior
        LinearLayout contenedorArriba = new LinearLayout(this);
        contenedorArriba.setOrientation(LinearLayout.VERTICAL);
        contenedorArriba.setGravity(Gravity.CENTER_HORIZONTAL);

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

        // Texto login
        TextView loginText = new TextView(this);
        loginText.setText("Iniciar sesión");
        loginText.setTextSize(24);
        loginText.setTextColor(Color.DKGRAY);
        loginText.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams loginParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        loginParams.setMargins(0, 0, 0, 80);
        loginText.setLayoutParams(loginParams);

        contenedorArriba.addView(titulo);
        contenedorArriba.addView(loginText);

        // Spacer superior
        View spacerArriba = new View(this);
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        spacerArriba.setLayoutParams(spacerParams);

        // Contenedor formulario
        LinearLayout contenedorForm = new LinearLayout(this);
        contenedorForm.setOrientation(LinearLayout.VERTICAL);
        contenedorForm.setGravity(Gravity.CENTER);

        // Email
        emailEditText = new EditText(this);
        emailEditText.setHint("Email");

        LinearLayout.LayoutParams paramsEmail =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsEmail.setMargins(0, 0, 0, 30);
        emailEditText.setLayoutParams(paramsEmail);

        // Contraseña
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Contraseña");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);

        LinearLayout.LayoutParams paramsPassword =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsPassword.setMargins(0, 0, 0, 50);
        passwordEditText.setLayoutParams(paramsPassword);

        // Botón login con mismo estilo
        loginButton = crearBoton("Entrar", "#F06292");

        LinearLayout.LayoutParams paramsButton =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        loginButton.setLayoutParams(paramsButton);

        contenedorForm.addView(emailEditText);
        contenedorForm.addView(passwordEditText);
        contenedorForm.addView(loginButton);

        // Spacer inferior
        View spacerAbajo = new View(this);
        spacerAbajo.setLayoutParams(spacerParams);

        layout.addView(contenedorArriba);
        layout.addView(spacerArriba);
        layout.addView(contenedorForm);
        layout.addView(spacerAbajo);

        setContentView(layout);

        // Inicializar ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observadores LiveData
        loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {
            GestorSesion.guardarToken(this, loginRespuesta.getToken());
            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
        });

        loginViewModel.getError().observe(this, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        );

        // Botón login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            loginViewModel.iniciarSesion(email, password);
        });
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