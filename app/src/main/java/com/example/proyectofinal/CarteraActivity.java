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
import android.widget.ImageButton;
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
        setContentView(R.layout.activity_cartera);

        etBtc = findViewById(R.id.etBtc);
        etEth = findViewById(R.id.etEth);
        tvBtcAmount = findViewById(R.id.tvBtcAmount);
        tvEthAmount = findViewById(R.id.tvEthAmount);
        tvTotal = findViewById(R.id.tvTotal);

//      Button btnCartera = findViewById(R.id.btnCartera);
        Button btnBitcoin = findViewById(R.id.btnBitcoin);
        Button btnEthereum = findViewById(R.id.btnEthereum);
        ImageButton btnLogout = findViewById(R.id.btnLogout);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                updatePrices();
                handler.postDelayed(this, 1000);
            }
        };

        handler.post(runnable);

//        btnCartera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });

        btnBitcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteraActivity.this, BitcoinActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnEthereum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteraActivity.this, EthereumActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarteraActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        etBtc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No es necesario hacer nada
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateAmounts();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateAmounts();
            }
        });

        etEth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                // No es necesario hacer nada
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateAmounts();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateAmounts();
            }
        });
    }

    private void updatePrices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String btcPriceStr = btcAmount > 0 ? getCryptoPrice("BTCUSDT") : null;
                String ethPriceStr = ethAmount > 0 ? getCryptoPrice("ETHUSDT") : null;

                if (btcPriceStr != null) {
                    btcPrice = Double.parseDouble(btcPriceStr);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvBtcAmount.setText(formatPrice(btcAmount) + " BTC = $" + formatPrice(btcAmount * btcPrice));
                        }
                    });
                }

                if (ethPriceStr != null) {
                    ethPrice = Double.parseDouble(ethPriceStr);
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

    private void updateAmounts() {
        try {
            btcAmount = TextUtils.isEmpty(etBtc.getText()) ? 0 : Double.parseDouble(etBtc.getText().toString());
            ethAmount = TextUtils.isEmpty(etEth.getText()) ? 0 : Double.parseDouble(etEth.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(CarteraActivity.this, "Por favor ingresa una cantidad válida", Toast.LENGTH_SHORT).show();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvBtcAmount.setText(formatPrice(btcAmount) + " BTC = $" + formatPrice(btcAmount * btcPrice));
                tvEthAmount.setText(formatPrice(ethAmount) + " ETH = $" + formatPrice(ethAmount * ethPrice));
                updateTotal();
            }
        });
    }

    private void updateTotal() {
        double total = (btcAmount * btcPrice) + (ethAmount * ethPrice);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTotal.setText("Total: $" + formatPrice(total));
            }
        });
    }

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