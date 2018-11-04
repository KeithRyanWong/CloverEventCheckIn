package com.kwover.eventcheck_in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.clover.sdk.v1.Intents;
import com.clover.sdk.v1.customer.Customer;
import com.clover.sdk.v3.scanner.BarcodeScanner;
import com.kwover.eventcheck_in.util.ActivityCallbackInterface;
import com.kwover.eventcheck_in.util.Customers;

public class StartupActivity extends AppCompatActivity {
    protected Customers customers;
    private static final String BARCODE_BROADCAST = "com.clover.BarcodeBroadcast";
    private String scannedBarcode;

    private static final String TAG = "Startup Activity";

//    When app is started:
//    check for existence of internal customers db
//    if it doesn't exist, make it
//    pull/sync internal customers
//          After syncing should switch to main screen/thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        customers = new Customers();
        registerReceiver(new BarcodeReceiver(), new IntentFilter(BARCODE_BROADCAST));




//        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.startTest);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                customers.openDb(context);
//                customers.syncDb(context, new ActivityCallbackInterface() {
//                    @Override
//                    public void onSyncFinishOk() {
//                        //transition to check in activity
//                    }
//
//                    @Override
//                    public void onSyncFinishBad() {
//                        //transition to error message
//                    }
//                });
//            }
//        });


        //Show spinner
        //


//
//        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.startTest);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Customers.openDb(context);
//                Customers.syncDb(context);
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        final Context context = this;

        customers.openDb(context);
        customers.syncDb(context, new ActivityCallbackInterface() {
                    @Override
                    public void onSyncFinishOk() {
                        //Remove loading bar and message
                        ProgressBar load_bar = (ProgressBar) findViewById(R.id.loading_bar);
                        TextView load_msg = (TextView) findViewById(R.id.loading_message);

                        load_bar.setVisibility(View.INVISIBLE);
                        load_msg.setVisibility(View.INVISIBLE);
                        //transition to check in activity

                        startScanForQR();
                    }

                    @Override
                    public void onSyncFinishBad() {
                        //transition to error message
                        Log.e(TAG, "onSyncFinishBad: There was an error grabbing data from the server");
                    }

                    @Override
                    public void onUpdateFinished(Boolean finishedOk) {

                    }
                }
        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        customers.closeConnection();
    }

    private void startScanForQR() {
        //start the scanner and set a timeout in case nothing gets scanned
        startBarcodeScanner();
    }

    private void markCustomerAttended(final String customerId) {
        final Context context = this;

        customers.markCustomerAttended(context, customerId , new ActivityCallbackInterface() {
            @Override
            public void onSyncFinishOk() {
                return;
            }

            @Override
            public void onSyncFinishBad() {
                return;
            }

            @Override
            public void onUpdateFinished(Boolean finishedOk) {
                //Transition to customer checked in message
                if (finishedOk) {
                    Log.i(TAG, "onUpdateFinished: Finished syncing " + customerId + ".");
                } else {
                    Log.i(TAG, "onUpdateFinished: Error syncing " + customerId + " through Customers Service.");
                }
                return;
            }
        });

//        customers.closeConnection();
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

//        try {
//            Thread.sleep(5000);
//            if (new BarcodeScanner(this).isProcessing()) {
//                stopBarcodeScanner();
//                Log.i(TAG, "startBarcodeScanner: Please start scanner again if nothing has happened");
//            } else {
//                Log.i(TAG, "startBarcodeScanner: Scan completed already");
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "startBarcodeScanner: unknown error prevented barcode scanner from closing");
//            return false;
//        }

//        return true;
    }

    public boolean stopBarcodeScanner() {
        return new BarcodeScanner(this).stopScan(getBarcodeSetting(false));
    }

    private class BarcodeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BARCODE_BROADCAST)) {
                String barcode = intent.getStringExtra("Barcode");
                if (barcode != null) {
                    scannedBarcode = barcode.trim();
                    if (scannedBarcode.startsWith("CloverEventCheck-in.")) {
                        Log.i(TAG, "Scanned entity: " + scannedBarcode.substring(20));
                        markCustomerAttended(scannedBarcode.substring(20));
                    }
                }
            }
        }
    }
}
