package com.ignacio_natalia.puzzleteca.pantallas.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // ---------- CONTENEDOR FORMULARIO ----------
        LinearLayout contenedorForm = new LinearLayout(this);
        contenedorForm.setOrientation(LinearLayout.VERTICAL);
        contenedorForm.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams formParams =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );

        formParams.gravity = Gravity.CENTER;

        contenedorForm.setLayoutParams(formParams);

        LinearLayout.LayoutParams campoParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );

        campoParams.setMargins(80, 25, 80, 25);

        // ---------- EMAIL ----------
        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setHintTextColor(Color.GRAY);
        emailEditText.setBackground(crearFondoCampo());
        emailEditText.setPadding(40, 30, 40, 30);

        emailEditText.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_dialog_email, 0, 0, 0
        );

        emailEditText.setCompoundDrawablePadding(20);
        emailEditText.setLayoutParams(campoParams);

        // ---------- PASSWORD ----------
        passwordEditText = new EditText(this);
        passwordEditText.setHint("Contraseña");
        passwordEditText.setHintTextColor(Color.GRAY);

        passwordEditText.setInputType(
                InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PASSWORD
        );

        passwordEditText.setBackground(crearFondoCampo());
        passwordEditText.setPadding(40, 30, 40, 30);

        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_lock_lock, 0, 0, 0
        );

        passwordEditText.setCompoundDrawablePadding(20);
        passwordEditText.setLayoutParams(campoParams);

        // ---------- BOTON LOGIN ----------
        loginButton = crearBoton();
        loginButton.setLayoutParams(campoParams);

        contenedorForm.addView(emailEditText);
        contenedorForm.addView(passwordEditText);
        contenedorForm.addView(loginButton);

        // Añadir elementos al layout
        layout.addView(titulo);
        layout.addView(contenedorForm);

        setContentView(layout);

        // ViewModel
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {

            GestorSesion.guardarToken(this, loginRespuesta.getToken());

            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, AppPrincipal.class);
            startActivity(intent);
            finish();
        });

        loginViewModel.getError().observe(this, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        );

        // Acción botón login
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

    // ---------- BOTON BONITO ----------
    @SuppressLint("SetTextI18n")
    private Button crearBoton() {

        Button btn = new Button(this);
        btn.setText("Entrar");
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(20);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(60);
        shape.setColor(Color.parseColor("#F06292"));

        btn.setBackground(shape);

        return btn;
    }

    // ---------- FONDO EDITTEXT ----------
    private GradientDrawable crearFondoCampo() {

        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(50);
        shape.setStroke(3, Color.parseColor("#A5D6A7"));

        return shape;
    }

}