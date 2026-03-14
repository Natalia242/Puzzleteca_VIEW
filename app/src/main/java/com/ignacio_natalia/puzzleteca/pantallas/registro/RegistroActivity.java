package com.ignacio_natalia.puzzleteca.pantallas.registro;

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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.modelos.Usuario;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginViewModel;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class RegistroActivity extends AppCompatActivity {

    private RegistroViewModel registroViewModel;
    private LoginViewModel loginViewModel;

    private EditText nombreEditText, apellidoEditText, emailEditText, passwordEditText;
    private Button botonRegistro;

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

        // ---------- CAMPOS DE TEXTO ----------
        nombreEditText = crearEditText("Nombre");
        nombreEditText.setLayoutParams(campoParams);

        apellidoEditText = crearEditText("Apellido");
        apellidoEditText.setLayoutParams(campoParams);

        emailEditText = crearEditText("Email");
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setLayoutParams(campoParams);

        passwordEditText = crearEditText("Password");
        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        passwordEditText.setLayoutParams(campoParams);

        // ---------- BOTON REGISTRO ----------
        botonRegistro = crearBoton();
        botonRegistro.setLayoutParams(campoParams);

        // Añadir campos al contenedor
        contenedorForm.addView(nombreEditText);
        contenedorForm.addView(apellidoEditText);
        contenedorForm.addView(emailEditText);
        contenedorForm.addView(passwordEditText);
        contenedorForm.addView(botonRegistro);

        // Añadir elementos al layout
        layout.addView(titulo);
        layout.addView(contenedorForm);

        setContentView(layout);

        // ViewModel
        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        botonRegistro.setOnClickListener(v -> {
            String n = nombreEditText.getText().toString().trim();
            String a = apellidoEditText.getText().toString().trim();
            String e = emailEditText.getText().toString().trim();
            String p = passwordEditText.getText().toString().trim();

            if (n.isEmpty() || a.isEmpty() || e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Usuario usuario = new Usuario(n, a, e, p, Usuario.TipoUsuario.Usuario);

                // Intentamos registrar al usuario
                registroViewModel.crearUsuario(usuario);

                // Observamos el resultado del registro
                registroViewModel.getUsuarioCreado().observe(this, exito -> {
                    if (exito) {
                        Log.d("RegistroActivity", "Usuario registrado con éxito");

                        // Ahora hacemos login automático
                        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                        loginViewModel.iniciarSesion(e, p); // usamos email y password ingresados

                        // Observamos el resultado del login
                        loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {
                            GestorSesion.guardarToken(this, loginRespuesta.getToken());

                            Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(this, AppPrincipal.class);
                            startActivity(intent);
                            finish();
                        });

                        loginViewModel.getError().observe(this, mensaje -> {
                            Toast.makeText(this, "Login automático fallido: " + mensaje, Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        Log.e("RegistroActivity", "Error al registrar el usuario");
                        Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // ---------- BOTON BONITO ----------
    private Button crearBoton() {
        Button btn = new Button(this);
        btn.setText("Registrarse");
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(20);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(60);
        shape.setColor(Color.parseColor("#26A69A"));

        btn.setBackground(shape);

        return btn;
    }

    // ---------- EDITTEXT BONITO ----------
    private EditText crearEditText(String hint) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setTextSize(18);
        edit.setTextColor(Color.DKGRAY);
        edit.setHintTextColor(Color.GRAY);
        edit.setPadding(40, 35, 40, 35);

        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(55);
        shape.setColor(Color.parseColor("#EAF8E0")); // mismo color suave que botones
        edit.setBackground(shape);

        return edit;
    }
}