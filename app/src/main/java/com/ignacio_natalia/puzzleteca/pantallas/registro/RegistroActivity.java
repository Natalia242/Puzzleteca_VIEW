package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.ignacio_natalia.puzzleteca.modelos.Usuario;

public class RegistroActivity extends AppCompatActivity {

    private RegistroViewModel registroViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fondo degradado igual que las otras pantallas
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

        // Subtítulo
        TextView subtitulo = new TextView(this);
        subtitulo.setText("Crear cuenta");
        subtitulo.setTextSize(24);
        subtitulo.setTextColor(Color.DKGRAY);
        subtitulo.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams subtituloParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        subtituloParams.setMargins(0, 0, 0, 80);
        subtitulo.setLayoutParams(subtituloParams);

        contenedorArriba.addView(titulo);
        contenedorArriba.addView(subtitulo);

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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 30, 0, 30);

        EditText nombre = new EditText(this);
        nombre.setHint("Nombre");
        nombre.setLayoutParams(params);

        EditText apellido = new EditText(this);
        apellido.setHint("Apellido");
        apellido.setLayoutParams(params);

        EditText email = new EditText(this);
        email.setHint("Email");
        email.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        email.setLayoutParams(params);

        EditText password = new EditText(this);
        password.setHint("Password");
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setLayoutParams(params);

        Button botonRegistro = crearBoton("Registrarse", "#26A69A");
        botonRegistro.setLayoutParams(params);

        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        registroViewModel.getUsuarioCreado().observe(this, exito -> {
            if (exito) {
                Log.d("RegistroActivity", "Usuario registrado con éxito");
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Log.e("RegistroActivity", "Error al registrar el usuario");
                Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
            }
        });

        botonRegistro.setOnClickListener(v -> {
            String n = nombre.getText().toString();
            String a = apellido.getText().toString();
            String e = email.getText().toString();
            String p = password.getText().toString();

            if (n.isEmpty() || a.isEmpty() || e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                Usuario usuario = new Usuario(n, a, e, p, Usuario.TipoUsuario.Usuario);
                registroViewModel.crearUsuario(usuario);
            }
        });

        contenedorForm.addView(nombre);
        contenedorForm.addView(apellido);
        contenedorForm.addView(email);
        contenedorForm.addView(password);
        contenedorForm.addView(botonRegistro);

        // Spacer inferior
        View spacerAbajo = new View(this);
        spacerAbajo.setLayoutParams(spacerParams);

        layout.addView(contenedorArriba);
        layout.addView(spacerArriba);
        layout.addView(contenedorForm);
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
        btn.setPadding(20, 30, 20, 30);

        return btn;
    }
}