package com.kwover.eventcheck_in;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.scanner.BarcodeScanner;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startBarcodeScanner();

        try {
            Thread.sleep(10000);
        } catch (Exception e) {

        }

        stopBarcodeScanner();
    }

    private static Bundle getBarcodeSetting(final boolean enabled) {
        final Bundle extras = new Bundle();
        extras.putBoolean(Intents.EXTRA_START_SCAN, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_MERCHANT_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_LED_ON, false);
        return extras;
    }

    public boolean startBarcodeScanner() {
        return new BarcodeScanner(this).startScan(getBarcodeSetting(true));
    }

    public boolean stopBarcodeScanner() {
        return new BarcodeScanner(this).stopScan(getBarcodeSetting(false));
    }
}
