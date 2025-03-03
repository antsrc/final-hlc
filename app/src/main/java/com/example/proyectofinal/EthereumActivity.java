package com.example.proyectofinal;

public class EthereumActivity extends CoinActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_ethereum;
    }

    @Override
    protected String getCoinUrl() {
        return "https://api.binance.com/api/v3/ticker/price?symbol=ETHUSDT";
    }

}
