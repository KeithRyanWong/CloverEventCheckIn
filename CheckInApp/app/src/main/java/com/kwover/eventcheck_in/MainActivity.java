package com.kwover.eventcheck_in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.scanner.BarcodeScanner;

public class MainActivity extends AppCompatActivity {
//    private String scannedBarcode;
//    public static final String BARCODE_BROADCAST = "com.clover.BarcodeBroadcast";
//    public static final String CHECKIN_TAG = "KeithsCheckInApp";
//    private ConstraintLayout screen = null;
////  private Date lastQueryTime = null;
//
//
//
//    //When app is started:
//    // check for existence of internal customers db
//    // if it doesn't exist, make it
//    // pull/sync internal customers
//    //      After syncing should switch to main screen/thread
//
//
//    //On Main Screen/thread
//
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        //Sync on open
//
//
////        registerReceiver(new BarcodeReceiver(), new IntentFilter(BARCODE_BROADCAST));
////        startBarcodeScanner();
////
////        stopBarcodeScanner();
////
////        screen = findViewById(R.id.check_in_screen);
////        screen.setOnTouchListener(new View.OnTouchListener() {
////            @Override
////            public boolean onTouch(View v, MotionEvent event) {
////                Log.i(CHECKIN_TAG, "onTouch: Screen was touched=======");
////                return false;
////            }
////        });
//    }
//
//    //Register sensor to exit
//
//    private static Bundle getBarcodeSetting(final boolean enabled) {
//        final Bundle extras = new Bundle();
//        extras.putBoolean(Intents.EXTRA_START_SCAN, enabled);
//        extras.putBoolean(Intents.EXTRA_SHOW_PREVIEW, enabled);
//        extras.putBoolean(Intents.EXTRA_SHOW_MERCHANT_PREVIEW, enabled);
//        extras.putBoolean(Intents.EXTRA_LED_ON, false);
//        return extras;
//    }
//
//    public boolean startBarcodeScanner() {
//        new BarcodeScanner(this).startScan(getBarcodeSetting(true));
//
//        try {
//            Thread.sleep(5000);
//            if (new BarcodeScanner(this).isProcessing()) {
//                stopBarcodeScanner();
//                Log.i(CHECKIN_TAG, "startBarcodeScanner: Please start scanner again if nothing has happened");
//            } else {
//                Log.i(CHECKIN_TAG, "startBarcodeScanner: Scan completed already");
//            }
//        } catch (Exception e) {
//            Log.e(CHECKIN_TAG, "startBarcodeScanner: unknown error prevented barcode scanner from closing");
//            return false;
//        }
//
//        return true;
//    }
//
//    public boolean stopBarcodeScanner() {
//        return new BarcodeScanner(this).stopScan(getBarcodeSetting(false));
//    }
//
//    private class BarcodeReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(BARCODE_BROADCAST)) {
//                String barcode = intent.getStringExtra("Barcode");
//                if (barcode != null) {
//                    scannedBarcode = barcode;
//                    Log.i(CHECKIN_TAG, "Scanned entity: " + scannedBarcode);
//                }
//            }
//        }
//    }
//
//    interface CallbackInterface {
//
//    }
//
//    public boolean syncDatabase() {
//
//    }

}
