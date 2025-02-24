package com.example.proyectofinal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class CarteraActivity extends AppCompatActivity {

    private EditText etBtc, etEth;
    private TextView tvBtcAmount, tvEthAmount, tvTotal;
    private double btcPrice = 0, ethPrice = 0;
    private double btcAmount = 0, ethAmount = 0;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartera); // Asegúrate de tener el layout adecuado

        etBtc = findViewById(R.id.etBtc);
        etEth = findViewById(R.id.etEth);
        tvBtcAmount = findViewById(R.id.tvBtcAmount);
        tvEthAmount = findViewById(R.id.tvEthAmount);
        tvTotal = findViewById(R.id.tvTotal);

        Button btnCartera = findViewById(R.id.btnCartera);
        Button btnBitcoin = findViewById(R.id.btnBitcoin);
        Button btnEthereum = findViewById(R.id.btnEthereum);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updatePrices();
                handler.postDelayed(this, 1000); // Actualiza cada segundo
            }
        };

        handler.post(runnable);

        // Acción para el botón Cartera
        btnCartera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ya estamos en la actividad Cartera, no es necesario hacer nada
            }
        });

        // Acción para el botón BTC/USDT
        btnBitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia a la actividad Bitcoin
                Intent intent = new Intent(CarteraActivity.this, BitcoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Acción para el botón ETH/USDT
        btnEthereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambia a la actividad Ethereum
                Intent intent = new Intent(CarteraActivity.this, EthereumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // Escuchar cambios en las cantidades de BTC y ETH usando TextWatcher directamente
        etBtc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No es necesario hacer nada antes de que cambie el texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateAmounts(); // Llama a la función que actualiza los valores de BTC y ETH
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Después de cambiar el texto, también actualizamos el total
                updateAmounts();
            }
        });

        etEth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No es necesario hacer nada antes de que cambie el texto
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateAmounts(); // Llama a la función que actualiza los valores de BTC y ETH
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Después de cambiar el texto, también actualizamos el total
                updateAmounts();
            }
        });
    }

    // Actualiza los precios de BTC y ETH desde la API
    private void updatePrices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String btcPriceStr = btcAmount > 0 ? getCryptoPrice("BTCUSDT") : null;
                String ethPriceStr = ethAmount > 0 ? getCryptoPrice("ETHUSDT") : null;

                if (btcPriceStr != null) {
                    btcPrice = Double.parseDouble(btcPriceStr);
                    // Usamos runOnUiThread para asegurar que la actualización ocurra en el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvBtcAmount.setText(formatPrice(btcAmount) + " BTC = $" + formatPrice(btcAmount * btcPrice));
                        }
                    });
                }

                if (ethPriceStr != null) {
                    ethPrice = Double.parseDouble(ethPriceStr);
                    // Usamos runOnUiThread para asegurar que la actualización ocurra en el hilo principal
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvEthAmount.setText(formatPrice(ethAmount) + " ETH = $" + formatPrice(ethAmount * ethPrice));
                        }
                    });
                }

                updateTotal();
            }
        }).start();
    }

    // Obtiene el precio de la criptomoneda a partir de la API de Binance
    private String getCryptoPrice(String symbol) {
        String price = null;
        try {
            URL url = new URL("https://api.binance.com/api/v3/ticker/price?symbol=" + symbol);
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

    // Actualiza las cantidades de BTC y ETH
    private void updateAmounts() {
        try {
            btcAmount = TextUtils.isEmpty(etBtc.getText()) ? 0 : Double.parseDouble(etBtc.getText().toString());
            ethAmount = TextUtils.isEmpty(etEth.getText()) ? 0 : Double.parseDouble(etEth.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(CarteraActivity.this, "Por favor ingresa una cantidad válida", Toast.LENGTH_SHORT).show();
        }

        // Actualiza las cantidades y los precios calculados
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBtcAmount.setText(formatPrice(btcAmount) + " BTC = $" + formatPrice(btcAmount * btcPrice));
                tvEthAmount.setText(formatPrice(ethAmount) + " ETH = $" + formatPrice(ethAmount * ethPrice));
                updateTotal();
            }
        });
    }

    // Calcula y actualiza el total
    private void updateTotal() {
        double total = (btcAmount * btcPrice) + (ethAmount * ethPrice);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTotal.setText("Total: $" + formatPrice(total));
            }
        });
    }

    // Formatea el precio a un formato con dos decimales
    private String formatPrice(double price) {
        if (price == 0) {
            return "0.00";
        }
        DecimalFormat formatter = new DecimalFormat("#,###.00");
        return formatter.format(price);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}