package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

public abstract class CoinActivity extends AppCompatActivity {

    private TextView tvPrice;
    private Handler handler;
    private Runnable runnable;
    private double lastPrice = 0;


    // Este método es responsable de devolver el layout específico para cada criptomoneda
    protected abstract int getLayoutId();
    protected abstract String getCoinUrl(); // Método abstracto para obtener la URL de la API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        tvPrice = findViewById(R.id.tvPrice);
        Button btnCartera = findViewById(R.id.btnCartera);
        Button btnBitcoin = findViewById(R.id.btnBitcoin);
        Button btnEthereum = findViewById(R.id.btnEthereum);
        Button btnLogout = findViewById(R.id.btnLogout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updatePrice();
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(runnable);

        // Acción para el botón Cartera
        btnCartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinActivity.this, CarteraActivity.class);
                startActivity(intent);
            }
        });

        // Acción para el botón BTC/USDT
        btnBitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinActivity.this, BitcoinActivity.class);
                startActivity(intent);
            }
        });

        // Acción para el botón ETH/USDT
        btnEthereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinActivity.this, EthereumActivity.class);
                startActivity(intent);
            }
        });

        // Acción para el botón Logout
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CoinActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // Método que se encargará de cargar el precio de la criptomoneda
    private void updatePrice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String price = getCryptoPrice();
                if (price != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double currentPrice = Double.parseDouble(price);
                            DecimalFormat formatter = new DecimalFormat("#,###.00");
                            String formattedPrice = formatter.format(currentPrice);
                            tvPrice.setText("$" + formattedPrice);

                            if (lastPrice != 0) {
                                if (currentPrice > lastPrice) {
                                    tvPrice.setTextColor(getResources().getColor(android.R.color.holo_green_light)); // Verde si sube
                                } else if (currentPrice < lastPrice) {
                                    tvPrice.setTextColor(getResources().getColor(android.R.color.holo_red_light)); // Rojo si baja
                                }
                            }

                            lastPrice = currentPrice;
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CoinActivity.this, "Error al obtener el precio", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    // Método para obtener el precio de la criptomoneda desde la URL de la API
    private String getCryptoPrice() {
        String price = null;
        try {
            URL url = new URL(getCoinUrl());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
