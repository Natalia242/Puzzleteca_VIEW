package com.ignacio_natalia.puzzleteca.pantallas;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PruebaLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView texto = new TextView(this);
        texto.setText("LOGADO");
        texto.setTextSize(30);
        texto.setGravity(Gravity.CENTER);

        setContentView(texto);
    }
}