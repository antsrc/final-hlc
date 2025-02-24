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

public class EthereumActivity extends AppCompatActivity {

    private TextView tvPrice;
    private Handler handler;
    private Runnable runnable;
    private double lastPrice = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ethereum);

        tvPrice = findViewById(R.id.tvPrice);
        Button btnBitcoin = findViewById(R.id.btnBitcoin);
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

        btnBitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EthereumActivity.this, BitcoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EthereumActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }


    private void updatePrice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String price = getEthereumPrice();
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
                            Toast.makeText(EthereumActivity.this, "Error al obtener el precio", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

    private String getEthereumPrice() {
        String price = null;
        try {
            URL url = new URL("https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT");
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
