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
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.R;
import com.ignacio_natalia.puzzleteca.pantallas.login.LoginActivity;

public class RecuperarContrasenna extends AppCompatActivity {

    private RecuperarContrasennaViewModel viewModel;

    // Paso 1
    private EditText emailEditText;
    private Button btnSolicitarCodigo;
    private LinearLayout paso1Layout;

    // Paso 2
    private EditText codigoEditText;
    private EditText nuevaPasswordEditText;
    private EditText confirmarPasswordEditText;
    private Button btnConfirmar;
    private LinearLayout paso2Layout;

    private String emailGuardado;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(RecuperarContrasennaViewModel.class);

        GradientDrawable fondo = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{Color.parseColor("#DFF5C9"), Color.parseColor("#B8E6A5")}
        );

        FrameLayout layout = new FrameLayout(this);
        layout.setBackground(fondo);
        layout.setPadding(40, 60, 40, 40);

        // TITULO
        ImageView titulo = new ImageView(this);
        titulo.setImageResource(R.drawable.titulo);
        titulo.setAdjustViewBounds(true);
        FrameLayout.LayoutParams tituloParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        tituloParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        titulo.setLayoutParams(tituloParams);

        // CONTENEDOR PRINCIPAL
        LinearLayout contenedor = new LinearLayout(this);
        contenedor.setOrientation(LinearLayout.VERTICAL);
        contenedor.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams contenedorParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        contenedorParams.gravity = Gravity.CENTER;
        contenedor.setLayoutParams(contenedorParams);

        LinearLayout.LayoutParams campoParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        campoParams.setMargins(80, 25, 80, 25);

        // ========== PASO 1 ==========
        paso1Layout = new LinearLayout(this);
        paso1Layout.setOrientation(LinearLayout.VERTICAL);
        paso1Layout.setGravity(Gravity.CENTER);

        TextView txtTitulo1 = new TextView(this);
        txtTitulo1.setText("Recuperar contraseña");
        txtTitulo1.setTextColor(Color.parseColor("#37474F"));
        txtTitulo1.setTextSize(22);
        txtTitulo1.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titulo1Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titulo1Params.setMargins(80, 0, 80, 10);
        txtTitulo1.setLayoutParams(titulo1Params);

        TextView txtDesc1 = new TextView(this);
        txtDesc1.setText("Introduce tu email y te enviaremos un código");
        txtDesc1.setTextColor(Color.parseColor("#78909C"));
        txtDesc1.setTextSize(14);
        txtDesc1.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams desc1Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        desc1Params.setMargins(80, 0, 80, 20);
        txtDesc1.setLayoutParams(desc1Params);

        emailEditText = new EditText(this);
        emailEditText.setHint("Email");
        emailEditText.setHintTextColor(Color.GRAY);
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        emailEditText.setBackground(crearFondoCampo());
        emailEditText.setPadding(40, 30, 40, 30);
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.email, 0, 0, 0);
        emailEditText.setCompoundDrawablePadding(20);
        emailEditText.setLayoutParams(campoParams);

        btnSolicitarCodigo = crearBoton("Enviar código");
        btnSolicitarCodigo.setLayoutParams(campoParams);

        paso1Layout.addView(txtTitulo1);
        paso1Layout.addView(txtDesc1);
        paso1Layout.addView(emailEditText);
        paso1Layout.addView(btnSolicitarCodigo);

        // ========== PASO 2 ==========
        paso2Layout = new LinearLayout(this);
        paso2Layout.setOrientation(LinearLayout.VERTICAL);
        paso2Layout.setGravity(Gravity.CENTER);
        paso2Layout.setVisibility(android.view.View.GONE);

        TextView txtTitulo2 = new TextView(this);
        txtTitulo2.setText("Introduce el código");
        txtTitulo2.setTextColor(Color.parseColor("#37474F"));
        txtTitulo2.setTextSize(22);
        txtTitulo2.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titulo2Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        titulo2Params.setMargins(80, 0, 80, 10);
        txtTitulo2.setLayoutParams(titulo2Params);

        TextView txtDesc2 = new TextView(this);
        txtDesc2.setText("Revisa tu correo e introduce el código de 6 dígitos");
        txtDesc2.setTextColor(Color.parseColor("#78909C"));
        txtDesc2.setTextSize(14);
        txtDesc2.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams desc2Params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        desc2Params.setMargins(80, 0, 80, 20);
        txtDesc2.setLayoutParams(desc2Params);

        codigoEditText = new EditText(this);
        codigoEditText.setHint("Código de verificación");
        codigoEditText.setHintTextColor(Color.GRAY);
        codigoEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        codigoEditText.setBackground(crearFondoCampo());
        codigoEditText.setPadding(40, 30, 40, 30);
        codigoEditText.setGravity(Gravity.CENTER);
        codigoEditText.setLayoutParams(campoParams);

        boolean[] pass1Visible = {false};
        nuevaPasswordEditText = new EditText(this);
        nuevaPasswordEditText.setHint("Nueva contraseña");
        nuevaPasswordEditText.setHintTextColor(Color.GRAY);
        nuevaPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        nuevaPasswordEditText.setBackground(crearFondoCampo());
        nuevaPasswordEditText.setPadding(40, 30, 40, 30);
        nuevaPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
        nuevaPasswordEditText.setCompoundDrawablePadding(20);
        nuevaPasswordEditText.setLayoutParams(campoParams);
        nuevaPasswordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (nuevaPasswordEditText.getRight() - nuevaPasswordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    pass1Visible[0] = !pass1Visible[0];
                    nuevaPasswordEditText.setTransformationMethod(pass1Visible[0]
                            ? android.text.method.HideReturnsTransformationMethod.getInstance()
                            : android.text.method.PasswordTransformationMethod.getInstance());
                    nuevaPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, pass1Visible[0] ? R.drawable.eye : R.drawable.visibility_off, 0);
                    nuevaPasswordEditText.setSelection(nuevaPasswordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        boolean[] pass2Visible = {false};
        confirmarPasswordEditText = new EditText(this);
        confirmarPasswordEditText.setHint("Confirmar nueva contraseña");
        confirmarPasswordEditText.setHintTextColor(Color.GRAY);
        confirmarPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmarPasswordEditText.setBackground(crearFondoCampo());
        confirmarPasswordEditText.setPadding(40, 30, 40, 30);
        confirmarPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, R.drawable.visibility_off, 0);
        confirmarPasswordEditText.setCompoundDrawablePadding(20);
        confirmarPasswordEditText.setLayoutParams(campoParams);
        confirmarPasswordEditText.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (confirmarPasswordEditText.getRight() - confirmarPasswordEditText.getCompoundDrawables()[2].getBounds().width())) {
                    pass2Visible[0] = !pass2Visible[0];
                    confirmarPasswordEditText.setTransformationMethod(pass2Visible[0]
                            ? android.text.method.HideReturnsTransformationMethod.getInstance()
                            : android.text.method.PasswordTransformationMethod.getInstance());
                    confirmarPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.ic_lock_lock, 0, pass2Visible[0] ? R.drawable.eye : R.drawable.visibility_off, 0);
                    confirmarPasswordEditText.setSelection(confirmarPasswordEditText.getText().length());
                    return true;
                }
            }
            return false;
        });

        btnConfirmar = crearBoton("Cambiar contraseña");
        btnConfirmar.setLayoutParams(campoParams);

        paso2Layout.addView(txtTitulo2);
        paso2Layout.addView(txtDesc2);
        paso2Layout.addView(codigoEditText);
        paso2Layout.addView(nuevaPasswordEditText);
        paso2Layout.addView(confirmarPasswordEditText);
        paso2Layout.addView(btnConfirmar);

        // VOLVER AL LOGIN
        TextView txtVolver = new TextView(this);
        txtVolver.setText("¿Recuerdas tu contraseña? Iniciar sesión");
        txtVolver.setTextColor(Color.parseColor("#455A64"));
        txtVolver.setTextSize(16);
        txtVolver.setGravity(Gravity.CENTER);
        txtVolver.setPadding(20, 30, 20, 10);
        LinearLayout.LayoutParams volverParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        volverParams.setMargins(80, 20, 80, 10);
        txtVolver.setLayoutParams(volverParams);
        txtVolver.setOnClickListener(v -> finish());

        contenedor.addView(paso1Layout);
        contenedor.addView(paso2Layout);
        contenedor.addView(txtVolver);

        layout.addView(titulo);
        layout.addView(contenedor);
        setContentView(layout);

        // ========== LÓGICA BOTONES ==========
        btnSolicitarCodigo.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(this, "Introduce tu email", Toast.LENGTH_SHORT).show();
                return;
            }
            emailGuardado = email;
            viewModel.solicitarCodigo(email);
        });

        btnConfirmar.setOnClickListener(v -> {
            String codigo = codigoEditText.getText().toString().trim();
            String nuevaPassword = nuevaPasswordEditText.getText().toString().trim();
            String confirmarPassword = confirmarPasswordEditText.getText().toString().trim();

            if (codigo.isEmpty() || nuevaPassword.isEmpty() || confirmarPassword.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!nuevaPassword.equals(confirmarPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }
            if (nuevaPassword.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.confirmarCambioPassword(emailGuardado, codigo, nuevaPassword);
        });

        // ========== OBSERVERS ==========
        viewModel.codigoEnviado.observe(this, enviado -> {
            if (enviado) {
                paso1Layout.setVisibility(android.view.View.GONE);
                paso2Layout.setVisibility(android.view.View.VISIBLE);
                Toast.makeText(this, "Código enviado a tu correo", Toast.LENGTH_LONG).show();
            }
        });

        viewModel.passwordCambiada.observe(this, cambiada -> {
            if (cambiada) {
                Toast.makeText(this, "Contraseña cambiada correctamente", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        viewModel.error.observe(this, msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        );
    }

    private Button crearBoton(String texto) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setTextColor(Color.WHITE);
        btn.setTextSize(18);
        btn.setAllCaps(false);
        btn.setPadding(20, 30, 20, 30);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(60);
        shape.setColor(Color.parseColor("#F06292"));
        btn.setBackground(shape);
        return btn;
    }

    private GradientDrawable crearFondoCampo() {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.WHITE);
        shape.setCornerRadius(50);
        shape.setStroke(3, Color.parseColor("#A5D6A7"));
        return shape;
    }
}