package com.example.proyectofinal;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView textView = new TextView(this);
        textView.setText("Welcome to Home Screen");
        textView.setTextSize(24);
        setContentView(textView);
    }
}
