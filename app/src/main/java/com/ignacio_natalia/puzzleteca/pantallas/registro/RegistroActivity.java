package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
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

    private EditText nombreEditText, apellidoEditText, emailEditText, contrasenaEditText;
    private Button botonRegistro;
    private boolean politicasAceptadas = false;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fondo degradado
        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);
        layout.setPadding(40, 60, 40, 40);

        // ---------- TITULO ----------
        ImageView titulo = new ImageView(this);
        titulo.setImageResource(R.drawable.titulo);
        titulo.setAdjustViewBounds(true);

        FrameLayout.LayoutParams parametrosTitulo =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        parametrosTitulo.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titulo.setLayoutParams(parametrosTitulo);

        // ---------- FORMULARIO ----------
        LinearLayout contenedorFormulario = new LinearLayout(this);
        contenedorFormulario.setOrientation(LinearLayout.VERTICAL);
        contenedorFormulario.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams parametrosFormulario =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
        parametrosFormulario.gravity = Gravity.CENTER;
        contenedorFormulario.setLayoutParams(parametrosFormulario);

        LinearLayout.LayoutParams parametrosCampos =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
        parametrosCampos.setMargins(80, 25, 80, 25);

        // ---------- CAMPOS ----------
        nombreEditText = crearEditText("Nombre", R.drawable.people);
        nombreEditText.setLayoutParams(parametrosCampos);

        apellidoEditText = crearEditText("Apellido", R.drawable.family);
        apellidoEditText.setLayoutParams(parametrosCampos);

        emailEditText = crearEditText("Email", R.drawable.email);
        emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setLayoutParams(parametrosCampos);

        contrasenaEditText = crearPasswordEditText("Password");
        contrasenaEditText.setLayoutParams(parametrosCampos);

        // ---------- BOTÓN ----------
        botonRegistro = crearBoton();
        botonRegistro.setLayoutParams(parametrosCampos);

        // 🔴 DESHABILITADO DESDE EL INICIO
        botonRegistro.setEnabled(false);
        botonRegistro.setAlpha(0.5f);

        // ---------- CHECKBOX POLÍTICAS ----------
        CheckBox checkPoliticas = new CheckBox(this);
        checkPoliticas.setText("Acepto la Política de Privacidad y los Términos");
        checkPoliticas.setTextColor(Color.DKGRAY);
        checkPoliticas.setPadding(60, 20, 60, 20);

        checkPoliticas.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked) {
                mostrarPoliticas(checkPoliticas);
            } else {
                politicasAceptadas = false;
                botonRegistro.setEnabled(false);
                botonRegistro.setAlpha(0.5f);
            }
        });

        // ---------- LOGIN TEXTO ----------
        TextView textoLogin = new TextView(this);
        textoLogin.setText("¿Ya tienes cuenta? Iniciar sesión");
        textoLogin.setTextColor(Color.parseColor("#455A64"));
        textoLogin.setTextSize(16);
        textoLogin.setGravity(Gravity.CENTER);
        textoLogin.setPadding(20, 20, 20, 10);

        textoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this,
                    com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity.class);
            startActivity(intent);
        });

        // ---------- AÑADIR VISTAS ----------
        contenedorFormulario.addView(nombreEditText);
        contenedorFormulario.addView(apellidoEditText);
        contenedorFormulario.addView(emailEditText);
        contenedorFormulario.addView(contrasenaEditText);
        contenedorFormulario.addView(checkPoliticas);
        contenedorFormulario.addView(botonRegistro);
        contenedorFormulario.addView(textoLogin);

        layout.addView(titulo);
        layout.addView(contenedorFormulario);

        setContentView(layout);

        // ---------- VIEWMODEL ----------
        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        // ---------- REGISTRO ----------
        botonRegistro.setOnClickListener(v -> {

            if (!politicasAceptadas) {
                Toast.makeText(this,
                        "Debes aceptar las políticas para registrarte",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            String nombre = nombreEditText.getText().toString().trim();
            String apellido = apellidoEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String contrasena = contrasenaEditText.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario usuario = new Usuario(nombre, apellido, email, contrasena,
                    Usuario.TipoUsuario.Usuario);

            registroViewModel.crearUsuario(usuario);

            registroViewModel.getUsuarioCreado().observe(this, exito -> {

                if (exito) {

                    loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
                    loginViewModel.iniciarSesion(email, contrasena);

                    loginViewModel.getLoginExitoso().observe(this, loginRespuesta -> {
                        GestorSesion.guardarToken(this, loginRespuesta.getToken());

                        startActivity(new Intent(this, AppPrincipal.class));
                        finish();
                    });

                    loginViewModel.getError().observe(this, mensaje -> {
                        Toast.makeText(this,
                                "Login automático fallido: " + mensaje,
                                Toast.LENGTH_SHORT).show();
                    });

                } else {
                    Log.e("RegistroActivity", "Error al registrar usuario");
                    Toast.makeText(this,
                            "Error al registrar el usuario",
                            Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // ---------- BOTÓN ----------
    private Button crearBoton() {
        Button boton = new Button(this);
        boton.setText("Registrarse");
        boton.setTextColor(Color.WHITE);
        boton.setTextSize(20);
        boton.setAllCaps(false);
        boton.setPadding(20, 30, 20, 30);

        GradientDrawable forma = new GradientDrawable();
        forma.setCornerRadius(60);
        forma.setColor(Color.parseColor("#26A69A"));

        boton.setBackground(forma);
        return boton;
    }

    // ---------- EDITTEXT ----------
    private EditText crearEditText(String campo, int icono) {
        EditText texto = new EditText(this);
        texto.setHint(campo);
        texto.setTextSize(18);
        texto.setTextColor(Color.DKGRAY);
        texto.setHintTextColor(Color.GRAY);
        texto.setPadding(40, 35, 40, 35);

        texto.setCompoundDrawablesWithIntrinsicBounds(icono, 0, 0, 0);
        texto.setCompoundDrawablePadding(20);

        GradientDrawable forma = new GradientDrawable();
        forma.setCornerRadius(55);
        forma.setColor(Color.parseColor("#EAF8E0"));
        texto.setBackground(forma);

        return texto;
    }

    @SuppressLint("ClickableViewAccessibility")
    private EditText crearPasswordEditText(String campo) {

        EditText texto = crearEditText(campo, android.R.drawable.ic_lock_lock);

        texto.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        boolean[] visible = {false};

        texto.setCompoundDrawablesWithIntrinsicBounds(
                android.R.drawable.ic_lock_lock, 0,
                R.drawable.visibility_off, 0
        );

        texto.setOnTouchListener((v, event) -> {

            if (event.getAction() == MotionEvent.ACTION_UP) {

                if (event.getRawX() >= (texto.getRight()
                        - texto.getCompoundDrawables()[2].getBounds().width())) {

                    visible[0] = !visible[0];

                    if (visible[0]) {
                        texto.setTransformationMethod(
                                android.text.method.HideReturnsTransformationMethod.getInstance()
                        );

                        texto.setCompoundDrawablesWithIntrinsicBounds(
                                android.R.drawable.ic_lock_lock, 0,
                                R.drawable.eye, 0
                        );

                    } else {
                        texto.setTransformationMethod(
                                android.text.method.PasswordTransformationMethod.getInstance()
                        );

                        texto.setCompoundDrawablesWithIntrinsicBounds(
                                android.R.drawable.ic_lock_lock, 0,
                                R.drawable.visibility_off, 0
                        );
                    }

                    texto.setSelection(texto.getText().length());
                    return true;
                }
            }
            return false;
        });

        return texto;
    }

    // ---------- POLÍTICAS ----------
    @SuppressLint("SetTextI18n")
    private void mostrarPoliticas(CheckBox checkPoliticas) {

        ScrollView scrollView = new ScrollView(this);

        TextView texto = new TextView(this);
        texto.setText(Html.fromHtml(
                "Esta aplicación permite publicar dentro de una red social.<br><br>" +
                        "<b>Recopilamos datos básicos</b> como nombre, email, contraseña (cifrada) y contenido que publiques en la app.<br><br>" +
                        "Estos datos se utilizan únicamente para:<br>" +
                        "- <b>Gestionar tu cuenta</b><br>" +
                        "- <b>Permitir la publicación de puzzles</b><br>" +
                        "- <b>Mejorar la experiencia de la aplicación</b><br>" +
                        "- <b>Mantener la seguridad del servicio</b><br><br>" +
                        "Tu contenido puede ser visible para otros usuarios dentro de la plataforma.<br><br>" +
                        "<b>No compartimos tus datos con terceros</b>, salvo obligación legal.<br><br>" +
                        "<b>Puedes eliminar tu cuenta</b> y tus datos <b>en cualquier momento.</b><br><br>" +
                        "<b>Al continuar, aceptas estas condiciones.</b>"
        ));
        texto.setPadding(40, 40, 40, 40);
        texto.setTextColor(Color.DKGRAY);

        scrollView.addView(texto);

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Política de Privacidad")
                .setView(scrollView)
                .setPositiveButton("Acepto", (dialog, which) -> {

                    checkPoliticas.setChecked(true);
                    politicasAceptadas = true;

                    botonRegistro.setEnabled(true);
                    botonRegistro.setAlpha(1f);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {

                    checkPoliticas.setChecked(false);
                    politicasAceptadas = false;

                    botonRegistro.setEnabled(false);
                    botonRegistro.setAlpha(0.5f);
                })
                .show();
    }

}