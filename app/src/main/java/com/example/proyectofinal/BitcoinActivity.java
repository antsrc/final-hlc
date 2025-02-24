package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class BitcoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bitcoin);

        TextView tvPrice = findViewById(R.id.tvPrice);
        Button btnEthereum = findViewById(R.id.btnEthereum);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String price = getBitcoinPrice();
        if (price != null) {
            DecimalFormat formatter = new DecimalFormat("#,###.00");
            String formattedPrice = formatter.format(Double.parseDouble(price));
            tvPrice.setText("$" + formattedPrice);
        } else {
            Toast.makeText(BitcoinActivity.this, "Error al obtener el precio", Toast.LENGTH_SHORT).show();
        }

        btnEthereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BitcoinActivity.this, EthereumActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private String getBitcoinPrice() {
        String price = null;
        try {
            URL url = new URL("https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            int priceStartIndex = jsonResponse.indexOf("\"price\":\"") + 9;
            int priceEndIndex = jsonResponse.indexOf("\"", priceStartIndex);
            if (priceStartIndex != -1 && priceEndIndex != -1) {
                price = jsonResponse.substring(priceStartIndex, priceEndIndex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return price;
    }
}
