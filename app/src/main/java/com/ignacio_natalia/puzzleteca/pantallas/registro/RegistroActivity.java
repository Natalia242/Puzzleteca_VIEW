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

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.graphics.drawable.ColorDrawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);

        int screenW = getResources().getDisplayMetrics().widthPixels;
        int screenH = getResources().getDisplayMetrics().heightPixels;

        // ── Tarjeta raíz ────────────────────────────────────────────────
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        GradientDrawable rootBg = new GradientDrawable();
        rootBg.setColor(ContextCompat.getColor(this, R.color.white));
        rootBg.setCornerRadius(dp(24));
        root.setBackground(rootBg);

        // ── Cabecera con degradado ───────────────────────────────────────
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        header.setPadding(dp(24), dp(24), dp(24), dp(20));

        GradientDrawable headerBg = new GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                new int[]{
                        ContextCompat.getColor(this, R.color.app_teal_soft),
                        ContextCompat.getColor(this, R.color.app_green_light)
                });
        headerBg.setCornerRadii(new float[]{dp(24), dp(24), dp(24), dp(24), 0, 0, 0, 0});
        header.setBackground(headerBg);

        // Emoji escudo
        TextView tvEmoji = new TextView(this);
        tvEmoji.setText("🛡️");
        tvEmoji.setTextSize(36);
        tvEmoji.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams emojiLP = new LinearLayout.LayoutParams(dp(72), dp(72));
        emojiLP.gravity = Gravity.CENTER_HORIZONTAL;
        emojiLP.bottomMargin = dp(12);
        tvEmoji.setLayoutParams(emojiLP);
        GradientDrawable emojiBg = new GradientDrawable();
        emojiBg.setShape(GradientDrawable.OVAL);
        emojiBg.setColor(ContextCompat.getColor(this, R.color.white));
        tvEmoji.setBackground(emojiBg);

        // Título
        TextView tvTitulo = new TextView(this);
        tvTitulo.setText("Política de Privacidad");
        tvTitulo.setTextSize(20);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(this, R.color.app_teal_dark));
        tvTitulo.setGravity(Gravity.CENTER);

        // Subtítulo
        TextView tvSub = new TextView(this);
        tvSub.setText("Puzzleteca · Versión 1.0");
        tvSub.setTextSize(12);
        tvSub.setTextColor(ContextCompat.getColor(this, R.color.app_subtexto));
        tvSub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams subLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subLP.topMargin = dp(4);
        tvSub.setLayoutParams(subLP);

        header.addView(tvEmoji);
        header.addView(tvTitulo);
        header.addView(tvSub);

        // ── Cuerpo con scroll ────────────────────────────────────────────
        ScrollView scrollView = new ScrollView(this);
        LinearLayout.LayoutParams scrollLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        scrollView.setLayoutParams(scrollLP);

        LinearLayout cuerpo = new LinearLayout(this);
        cuerpo.setOrientation(LinearLayout.VERTICAL);
        cuerpo.setPadding(dp(20), dp(16), dp(20), dp(16));

        // Secciones de la política
        String[][] secciones = {
                {"📋", "¿Qué datos recogemos?",
                        "Nombre, email y contraseña (cifrada). También el contenido que publiques en la app, como puzzles y comentarios."},
                {"🎯", "¿Para qué los usamos?",
                        "• Gestionar tu cuenta\n• Publicar y compartir puzzles\n• Mejorar la experiencia de la app\n• Mantener la seguridad del servicio"},
                {"👥", "Visibilidad del contenido",
                        "Tu contenido puede ser visible para otros usuarios dentro de la plataforma."},
                {"🔒", "Privacidad garantizada",
                        "No compartimos tus datos con terceros, salvo obligación legal."},
                {"🗑️", "Tu control",
                        "Puedes eliminar tu cuenta y todos tus datos en cualquier momento desde el panel de perfil."},
        };

        for (String[] sec : secciones) {
            cuerpo.addView(crearSeccionPolitica(sec[0], sec[1], sec[2]));
        }

        scrollView.addView(cuerpo);

        // ── Separador ────────────────────────────────────────────────────
        View sep = new View(this);
        LinearLayout.LayoutParams sepLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1);
        sepLP.setMargins(dp(20), 0, dp(20), 0);
        sep.setLayoutParams(sepLP);
        sep.setBackgroundColor(ContextCompat.getColor(this, R.color.app_borde));

        // ── Botones ──────────────────────────────────────────────────────
        LinearLayout filaBotones = new LinearLayout(this);
        filaBotones.setOrientation(LinearLayout.HORIZONTAL);
        filaBotones.setPadding(dp(16), dp(14), dp(16), dp(16));
        filaBotones.setGravity(Gravity.CENTER_VERTICAL);

        // Botón Cancelar
        TextView btnCancelar = new TextView(this);
        btnCancelar.setText("No, gracias");
        btnCancelar.setTextSize(14);
        btnCancelar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnCancelar.setTextColor(ContextCompat.getColor(this, R.color.app_subtexto));
        btnCancelar.setGravity(Gravity.CENTER);
        btnCancelar.setPadding(dp(16), dp(12), dp(16), dp(12));
        LinearLayout.LayoutParams cancelLP = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        cancelLP.setMargins(0, 0, dp(8), 0);
        btnCancelar.setLayoutParams(cancelLP);
        GradientDrawable cancelBg = new GradientDrawable();
        cancelBg.setColor(ContextCompat.getColor(this, R.color.app_fondo_cancelar));
        cancelBg.setCornerRadius(dp(14));
        btnCancelar.setBackground(cancelBg);
        btnCancelar.setOnClickListener(v -> {
            dialog.dismiss();
            checkPoliticas.setChecked(false);
            politicasAceptadas = false;
            botonRegistro.setEnabled(false);
            botonRegistro.setAlpha(0.5f);
        });

        // Botón Aceptar
        TextView btnAceptar = new TextView(this);
        btnAceptar.setText("✓  Acepto");
        btnAceptar.setTextSize(14);
        btnAceptar.setTypeface(null, android.graphics.Typeface.BOLD);
        btnAceptar.setTextColor(ContextCompat.getColor(this, R.color.white));
        btnAceptar.setGravity(Gravity.CENTER);
        btnAceptar.setPadding(dp(16), dp(12), dp(16), dp(12));
        btnAceptar.setLayoutParams(new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        GradientDrawable aceptarBg = new GradientDrawable();
        aceptarBg.setColor(ContextCompat.getColor(this, R.color.app_teal));
        aceptarBg.setCornerRadius(dp(14));
        btnAceptar.setBackground(aceptarBg);
        btnAceptar.setOnClickListener(v -> {
            dialog.dismiss();
            checkPoliticas.setChecked(true);
            politicasAceptadas = true;
            botonRegistro.setEnabled(true);
            botonRegistro.setAlpha(1f);
        });

        filaBotones.addView(btnCancelar);
        filaBotones.addView(btnAceptar);

        // ── Montaje ──────────────────────────────────────────────────────
        root.addView(header);
        root.addView(scrollView);
        root.addView(sep);
        root.addView(filaBotones);

        dialog.setContentView(root);
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                    (int) (screenW * 0.92f),
                    (int) (screenH * 0.75f));
        }
    }

    /** Bloque visual de una sección de la política */
    private LinearLayout crearSeccionPolitica(String emoji, String titulo, String texto) {
        LinearLayout sec = new LinearLayout(this);
        sec.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(12);
        sec.setLayoutParams(lp);
        sec.setPadding(dp(14), dp(12), dp(14), dp(12));

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(ContextCompat.getColor(this, R.color.app_teal_soft));
        bg.setCornerRadius(dp(14));
        sec.setBackground(bg);

        // Fila: emoji + título
        LinearLayout fila = new LinearLayout(this);
        fila.setOrientation(LinearLayout.HORIZONTAL);
        fila.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams filaLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        filaLP.bottomMargin = dp(6);
        fila.setLayoutParams(filaLP);

        TextView tvEmoji = new TextView(this);
        tvEmoji.setText(emoji);
        tvEmoji.setTextSize(18);
        LinearLayout.LayoutParams eLP = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        eLP.setMargins(0, 0, dp(10), 0);
        tvEmoji.setLayoutParams(eLP);

        TextView tvTitulo = new TextView(this);
        tvTitulo.setText(titulo);
        tvTitulo.setTextSize(14);
        tvTitulo.setTypeface(null, android.graphics.Typeface.BOLD);
        tvTitulo.setTextColor(ContextCompat.getColor(this, R.color.app_teal_dark));

        fila.addView(tvEmoji);
        fila.addView(tvTitulo);

        // Texto del cuerpo
        TextView tvTexto = new TextView(this);
        tvTexto.setText(texto);
        tvTexto.setTextSize(13);
        tvTexto.setTextColor(ContextCompat.getColor(this, R.color.app_texto));
        tvTexto.setLineSpacing(dp(2), 1f);

        sec.addView(fila);
        sec.addView(tvTexto);
        return sec;
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }

}