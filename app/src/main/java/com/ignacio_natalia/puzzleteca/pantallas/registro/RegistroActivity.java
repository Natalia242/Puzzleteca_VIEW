package com.ignacio_natalia.puzzleteca.pantallas.registro;

import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 60, 60, 60);
        layout.setGravity(Gravity.CENTER);

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

        Button botonRegistro = new Button(this);
        botonRegistro.setText("Registrarse");
        botonRegistro.setLayoutParams(params);

        registroViewModel = new ViewModelProvider(this).get(RegistroViewModel.class);

        registroViewModel.getUsuarioCreado().observe(this, exito -> {
            if (exito) {
                Log.d("RegistroActivity", "Usuario registrado con éxito");
                Toast.makeText(this, "Usuario registrado con éxito", Toast.LENGTH_SHORT).show();
                finish(); // volver a pantalla anterior
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

        layout.addView(nombre);
        layout.addView(apellido);
        layout.addView(email);
        layout.addView(password);
        layout.addView(botonRegistro);

        setContentView(layout);
    }
}