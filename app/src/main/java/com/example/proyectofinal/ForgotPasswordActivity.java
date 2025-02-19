package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmailReset;
    private Button btnSendReset;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        etEmailReset = findViewById(R.id.etEmailReset);
        btnSendReset = findViewById(R.id.btnSendReset);
        btnBack = findViewById(R.id.btnBack);

        btnSendReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmailReset.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Introduce tu email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Correo de recuperación enviado a " + email, Toast.LENGTH_SHORT).show();
                    // Aquí puedes agregar la lógica para enviar el enlace de recuperación
                }
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cierra esta pantalla y vuelve atrás
                finish();
            }
        });
    }
}
