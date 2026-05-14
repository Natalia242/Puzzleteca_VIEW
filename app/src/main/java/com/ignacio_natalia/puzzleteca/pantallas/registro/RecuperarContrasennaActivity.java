package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
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
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;

public class RecuperarContrasennaActivity extends AppCompatActivity {

    private RecuperarContrasennaViewModel viewModel;
    private String emailGuardado;

    private EditText emailEditText;
    private Button botonSolicitarCodigo;
    private LinearLayout pasoUnoLayout;

    private EditText codigoEditText;
    private EditText nuevaContrasenaEditText;
    private EditText confirmarContrasenaEditText;
    private Button botonConfirmar;
    private LinearLayout pasoDosLayout;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RecuperarContrasennaViewModel.class);

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

        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams parametrosContenedor = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        parametrosContenedor.gravity = Gravity.CENTER;
        contenedor.setLayoutParams(parametrosContenedor);

        LinearLayout.LayoutParams parametrosCampos = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosCampos.setMargins(80, 25, 80, 25);

        // ═══ PASO 1 ═══
        pasoUnoLayout = new LinearLayout(this);
        pasoUnoLayout.setOrientation(LinearLayout.VERTICAL);
        pasoUnoLayout.setGravity(Gravity.CENTER);

        TextView textoTituloUno = new TextView(this);
        textoTituloUno.setText("Recuperar contraseña");
        textoTituloUno.setTextColor(ContextCompat.getColor(this, R.color.app_texto));
        textoTituloUno.setTextSize(22);
        textoTituloUno.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams parametrosTituloUno = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosTituloUno.setMargins(80, 0, 80, 10);
        textoTituloUno.setLayoutParams(parametrosTituloUno);

        TextView pasoUno = new TextView(this);
        pasoUno.setText("Introduce tu email y te enviaremos un código");
        pasoUno.setTextColor(ContextCompat.getColor(this, R.color.app_subtexto));
        pasoUno.setTextSize(14);
        pasoUno.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams parametrosPasoUno = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosPasoUno.setMargins(80, 0, 80, 20);
        pasoUno.setLayoutParams(parametrosPasoUno);

        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setHintTextColor(Color.GRAY);
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setBackground(crearFondoCampo());
        emailEditText.setPadding(40, 30, 40, 30);
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email, 0, 0, 0);
        emailEditText.setCompoundDrawablePadding(20);
        emailEditText.setLayoutParams(parametrosCampos);

        botonSolicitarCodigo = crearBoton("Enviar código");
        botonSolicitarCodigo.setLayoutParams(parametrosCampos);

        pasoUnoLayout.addView(textoTituloUno);
        pasoUnoLayout.addView(pasoUno);
        pasoUnoLayout.addView(emailEditText);
        pasoUnoLayout.addView(botonSolicitarCodigo);

        // ═══ PASO 2 ═══
        pasoDosLayout = new LinearLayout(this);
        pasoDosLayout.setOrientation(LinearLayout.VERTICAL);
        pasoDosLayout.setGravity(Gravity.CENTER);
        pasoDosLayout.setVisibility(android.view.View.GONE);

        TextView textoTituloDos = new TextView(this);
        textoTituloDos.setText("Introduce el código");
        textoTituloDos.setTextColor(ContextCompat.getColor(this, R.color.app_texto));
        textoTituloDos.setTextSize(22);
        textoTituloDos.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams parametrosTituloDos = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosTituloDos.setMargins(80, 0, 80, 10);
        textoTituloDos.setLayoutParams(parametrosTituloDos);

        TextView pasoDos = new TextView(this);
        pasoDos.setText("Revisa tu correo e introduce el código de 6 dígitos");
        pasoDos.setTextColor(ContextCompat.getColor(this, R.color.app_subtexto));
        pasoDos.setTextSize(14);
        pasoDos.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams parametrosPasoDos = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosPasoDos.setMargins(80, 0, 80, 20);
        pasoDos.setLayoutParams(parametrosPasoDos);

        codigoEditText = new EditText(this);
        codigoEditText.setHint("Código de verificación");
        codigoEditText.setHintTextColor(Color.GRAY);
        codigoEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        codigoEditText.setBackground(crearFondoCampo());
        codigoEditText.setPadding(40, 30, 40, 30);
        codigoEditText.setGravity(Gravity.CENTER);
        codigoEditText.setLayoutParams(parametrosCampos);

        boolean[] contrasenaUnoVisible = {false};
        nuevaContrasenaEditText = new EditText(this);
        nuevaContrasenaEditText.setHint("Nueva contraseña");
        nuevaContrasenaEditText.setHintTextColor(Color.GRAY);
        nuevaContrasenaEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        nuevaContrasenaEditText.setBackground(crearFondoCampo());
        nuevaContrasenaEditText.setPadding(40, 30, 40, 30);
        nuevaContrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
        nuevaContrasenaEditText.setCompoundDrawablePadding(20);
        nuevaContrasenaEditText.setLayoutParams(parametrosCampos);
        nuevaContrasenaEditText.setOnTouchListener((vista, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (nuevaContrasenaEditText.getRight() - nuevaContrasenaEditText.getCompoundDrawables()[2].getBounds().width())) {
                    contrasenaUnoVisible[0] = !contrasenaUnoVisible[0];
                    nuevaContrasenaEditText.setTransformationMethod(contrasenaUnoVisible[0]
                            ? android.text.method.HideReturnsTransformationMethod.getInstance()
                            : android.text.method.PasswordTransformationMethod.getInstance());
                    nuevaContrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0,
                            contrasenaUnoVisible[0] ? R.drawable.eye : R.drawable.visibility_off, 0);
                    nuevaContrasenaEditText.setSelection(nuevaContrasenaEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        boolean[] contrasenaDosVisible = {false};
        confirmarContrasenaEditText = new EditText(this);
        confirmarContrasenaEditText.setHint("Confirmar nueva contraseña");
        confirmarContrasenaEditText.setHintTextColor(Color.GRAY);
        confirmarContrasenaEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmarContrasenaEditText.setBackground(crearFondoCampo());
        confirmarContrasenaEditText.setPadding(40, 30, 40, 30);
        confirmarContrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
        confirmarContrasenaEditText.setCompoundDrawablePadding(20);
        confirmarContrasenaEditText.setLayoutParams(parametrosCampos);
        confirmarContrasenaEditText.setOnTouchListener((vista, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmarContrasenaEditText.getRight() - confirmarContrasenaEditText.getCompoundDrawables()[2].getBounds().width())) {
                    contrasenaDosVisible[0] = !contrasenaDosVisible[0];
                    confirmarContrasenaEditText.setTransformationMethod(contrasenaDosVisible[0]
                            ? android.text.method.HideReturnsTransformationMethod.getInstance()
                            : android.text.method.PasswordTransformationMethod.getInstance());
                    confirmarContrasenaEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0,
                            contrasenaDosVisible[0] ? R.drawable.eye : R.drawable.visibility_off, 0);
                    confirmarContrasenaEditText.setSelection(confirmarContrasenaEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        botonConfirmar = crearBoton("Cambiar contraseña");
        botonConfirmar.setLayoutParams(parametrosCampos);

        pasoDosLayout.addView(textoTituloDos);
        pasoDosLayout.addView(pasoDos);
        pasoDosLayout.addView(codigoEditText);
        pasoDosLayout.addView(nuevaContrasenaEditText);
        pasoDosLayout.addView(confirmarContrasenaEditText);
        pasoDosLayout.addView(botonConfirmar);

        TextView textoVolver = new TextView(this);
        textoVolver.setText("¿Recuerdas tu contraseña? Iniciar sesión");
        textoVolver.setTextColor(ContextCompat.getColor(this, R.color.app_texto_link));
        textoVolver.setTextSize(16);
        textoVolver.setGravity(Gravity.CENTER);
        textoVolver.setPadding(20, 30, 20, 10);
        LinearLayout.LayoutParams parametrosVolver = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parametrosVolver.setMargins(80, 20, 80, 10);
        textoVolver.setLayoutParams(parametrosVolver);
        textoVolver.setOnClickListener(v -> finish());

        contenedor.addView(pasoUnoLayout);
        contenedor.addView(pasoDosLayout);
        contenedor.addView(textoVolver);

        layout.addView(titulo);
        layout.addView(contenedor);
        setContentView(layout);

        botonSolicitarCodigo.setOnClickListener(vista -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduce tu email", Toast.LENGTH_SHORT).show();
                return;
            }
            emailGuardado = email;
            viewModel.solicitarCodigo(email);
        });

        botonConfirmar.setOnClickListener(vista -> {
            String codigo = codigoEditText.getText().toString().trim();
            String nuevaContrasena = nuevaContrasenaEditText.getText().toString().trim();
            String confirmarContrasena = confirmarContrasenaEditText.getText().toString().trim();
            if (codigo.isEmpty() || nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!nuevaContrasena.equals(confirmarContrasena)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nuevaContrasena.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.confirmarCambioPassword(emailGuardado, codigo, nuevaContrasena);
        });

        viewModel.codigoEnviado.observe(this, enviado -> {
            if (enviado) {
                pasoUnoLayout.setVisibility(android.view.View.GONE);
                pasoDosLayout.setVisibility(android.view.View.VISIBLE);
                Toast.makeText(this, "Código enviado a tu correo", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.contrasenaCambiada.observe(this, cambiada -> {
            if (cambiada) {
                Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        viewModel.error.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show());
    }

    private Button crearBoton(String texto) {
        Button boton = new Button(this);
        boton.setText(texto);
        boton.setTextColor(ContextCompat.getColor(this, R.color.white));
        boton.setTextSize(18);
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