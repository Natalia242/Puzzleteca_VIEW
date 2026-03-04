package com.ignacio_natalia.puzzleteca.pantallas.login;

import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

        // Crear layout principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(50, 50, 50, 50);

        // Crear EditText para email
        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        LinearLayout.LayoutParams paramsEmail = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        paramsEmail.setMargins(0, 0, 0, 30);
        emailEditText.setLayoutParams(paramsEmail);

        // Crear EditText para contraseña
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Contraseña");
        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT |
                android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout.LayoutParams paramsPassword = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        paramsPassword.setMargins(0, 0, 0, 30);
        passwordEditText.setLayoutParams(paramsPassword);

        // Crear botón de login
        loginButton = new Button(this);
        loginButton.setText("Iniciar sesión");
        LinearLayout.LayoutParams paramsButton = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        loginButton.setLayoutParams(paramsButton);

        // Añadir vistas al layout
        layout.addView(emailEditText);
        layout.addView(passwordEditText);
        layout.addView(loginButton);

        // Establecer layout como content view
        setContentView(layout);

        // Inicializar ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Observadores LiveData
        loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {
            GestorSesion.guardarToken(this, loginRespuesta.getToken());
            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();
            // Aquí puedes navegar a otra Activity o Fragment
        });

        loginViewModel.getError().observe(this, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        );

        // Botón de login
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
}