package com.ignacio_natalia.puzzleteca.pantallas.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.aplicacion_principal.AppPrincipal;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RecuperarContrasennaActivity;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;
import com.ignacio_natalia.puzzleteca.utilidades.GestorSesion;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private EditText emailEditText;
    private EditText contrasenaEditText;

    // ✅ Campo de instancia para que el observer pueda leerlo
    private String emailPendiente = "";

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{
                        ContextCompat.getColor(this, R.color.app_green_light),
                        ContextCompat.getColor(this, R.color.app_green_medium)
                }
        );

        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);
        layout.setPadding(40, 60, 40, 40);

        ImageView titulo = new ImageView(this);
        titulo.setImageResource(R.drawable.titulo);
        titulo.setAdjustViewBounds(true);
        FrameLayout.LayoutParams parametrosTitulo = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        parametrosTitulo.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titulo.setLayoutParams(parametrosTitulo);

        LinearLayout contenedorFormulario = new LinearLayout(this);
        contenedorFormulario.setOrientation(LinearLayout.VERTICAL);
        contenedorFormulario.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams parametrosFormulario = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        parametrosFormulario.gravity = Gravity.CENTER;
        contenedorFormulario.setLayoutParams(parametrosFormulario);

        LinearLayout.LayoutParams parametrosCampos = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosCampos.setMargins(80, 25, 80, 25);

        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setHintTextColor(Color.GRAY);
        emailEditText.setBackground(crearFondoCampo());
        emailEditText.setPadding(40, 30, 40, 30);
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email, 0, 0, 0);
        emailEditText.setCompoundDrawablePadding(20);
        emailEditText.setLayoutParams(parametrosCampos);

        boolean[] contrasenaVisible = {false};
        contrasenaEditText = new EditText(this);
        contrasenaEditText.setHint("Contraseña");
        contrasenaEditText.setHintTextColor(Color.GRAY);
        contrasenaEditText.setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        contrasenaEditText.setBackground(crearFondoCampo());
        contrasenaEditText.setPadding(40, 30, 40, 30);
        contrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
        contrasenaEditText.setCompoundDrawablePadding(20);
        contrasenaEditText.setLayoutParams(parametrosCampos);

        contrasenaEditText.setOnTouchListener((vista, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (contrasenaEditText.getRight()
                        - contrasenaEditText.getCompoundDrawables()[2].getBounds().width())) {
                    contrasenaVisible[0] = !contrasenaVisible[0];
                    if (contrasenaVisible[0]) {
                        contrasenaEditText.setTransformationMethod(
                                android.text.method.HideReturnsTransformationMethod.getInstance());
                        contrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(
                                android.R.drawable.ic_lock_lock, 0, R.drawable.eye, 0);
                    } else {
                        contrasenaEditText.setTransformationMethod(
                                android.text.method.PasswordTransformationMethod.getInstance());
                        contrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(
                                android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
                    }
                    contrasenaEditText.setSelection(contrasenaEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        Button botonLogin = crearBoton();
        botonLogin.setLayoutParams(parametrosCampos);

        TextView textoOlvidada = new TextView(this);
        textoOlvidada.setText("¿Has olvidado la contraseña?");
        textoOlvidada.setTextColor(ContextCompat.getColor(this, R.color.app_teal_darker));
        textoOlvidada.setTextSize(16);
        textoOlvidada.setGravity(Gravity.CENTER);
        textoOlvidada.setPadding(20, 20, 20, 10);
        textoOlvidada.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RecuperarContrasennaActivity.class);
            startActivity(intent);
        });

        View separador = new View(this);
        LinearLayout.LayoutParams separadorParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 3);
        separadorParams.setMargins(250, 75, 250, 75);
        separador.setLayoutParams(separadorParams);
        separador.setBackgroundColor(Color.DKGRAY);

        TextView textoRegistro = new TextView(this);
        textoRegistro.setText("¿No tienes cuenta? Crear cuenta");
        textoRegistro.setTextColor(ContextCompat.getColor(this, R.color.app_texto_link));
        textoRegistro.setTextSize(16);
        textoRegistro.setGravity(Gravity.CENTER);
        textoRegistro.setPadding(20, 20, 20, 10);
        textoRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        contenedorFormulario.addView(emailEditText);
        contenedorFormulario.addView(contrasenaEditText);
        contenedorFormulario.addView(botonLogin);
        contenedorFormulario.addView(textoOlvidada);
        contenedorFormulario.addView(separador);
        contenedorFormulario.addView(textoRegistro);

        layout.addView(titulo);
        layout.addView(contenedorFormulario);
        setContentView(layout);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {
            GestorSesion.guardarEmail(this, emailPendiente);
            GestorSesion.guardarToken(this, loginRespuesta.getToken());
            GestorSesion.guardarRol(this, loginRespuesta.getTipoUsuario());
            if (loginRespuesta.getId_usuario() != null)
                GestorSesion.guardarId_usuario(this, loginRespuesta.getId_usuario());
            if (loginRespuesta.getNombre() != null)
                GestorSesion.guardarNombre(this, loginRespuesta.getNombre());
            Intent intent = new Intent(this, AppPrincipal.class);
            startActivity(intent);
            finish();
        });

        loginViewModel.getError().observe(this, mensaje ->
                Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show());

        botonLogin.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String contrasena = contrasenaEditText.getText().toString().trim();
            if (email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            emailPendiente = email;
            loginViewModel.iniciarSesion(email, contrasena);
        });
    }

    @SuppressLint("SetTextI18n")
    private Button crearBoton() {
        Button boton = new Button(this);
        boton.setText("Entrar");
        boton.setTextColor(ContextCompat.getColor(this, R.color.white));
        boton.setTextSize(20);
        boton.setAllCaps(false);
        boton.setPadding(20, 30, 20, 30);
        GradientDrawable forma = new GradientDrawable();
        forma.setCornerRadius(60);
        forma.setColor(ContextCompat.getColor(this, R.color.app_rosa));
        boton.setBackground(forma);
        return boton;
    }

    private GradientDrawable crearFondoCampo() {
        GradientDrawable forma = new GradientDrawable();
        forma.setColor(ContextCompat.getColor(this, R.color.white));
        forma.setCornerRadius(50);
        forma.setStroke(3, ContextCompat.getColor(this, R.color.app_green_border));
        return forma;
    }
}