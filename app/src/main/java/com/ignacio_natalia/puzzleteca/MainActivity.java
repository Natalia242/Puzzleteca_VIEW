package com.ignacio_natalia.puzzleteca;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.ignacio_natalia.puzzleteca.pantallas.inicio.PantallaInicio;
import com.ignacio_natalia.puzzleteca.pantallas.registro.RegistroActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicia la pantalla de registro
        Intent intent = new Intent(MainActivity.this, PantallaInicio.class);
        startActivity(intent);

        // Cierra MainActivity para que no vuelva atrás
        finish();
    }

}