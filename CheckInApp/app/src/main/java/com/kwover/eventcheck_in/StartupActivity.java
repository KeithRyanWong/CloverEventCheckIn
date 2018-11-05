package com.kwover.eventcheck_in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private BarcodeReceiver barcodeReceiver;

    private static final String TAG = "Startup Activity";

    private ProgressBar loadBar;
    private TextView loadMsg;
    private TextView tip1Txt;
    private TextView warningMsg;
    private TextView welcomeMsg;
    private Button startScanBtn;
    private Button acknowledgeErrorBtn;


//    When app is started:
//    check for existence of internal customers db
//    if it doesn't exist, make it
//    pull/sync internal customers
//          After syncing should switch to main screen/thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.activity_startup);
        loadBar = (ProgressBar) findViewById(R.id.loading_bar);
        loadMsg = (TextView) findViewById(R.id.loading_message);
        tip1Txt = (TextView) findViewById(R.id.tip1);
        startScanBtn = (Button) findViewById(R.id.startScan);
        acknowledgeErrorBtn = (Button) findViewById(R.id.acknowledgementBtn);
        warningMsg = (TextView) findViewById(R.id.warningMsg);
        welcomeMsg = (TextView) findViewById(R.id.welcomeMsg);

        customers = new Customers();

        startScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startScanForQR();
            }
        });

        acknowledgeErrorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToMain();
            }
        });

        final Context context = this;

        customers.openDb(context);
        customers.syncDb(context, new ActivityCallbackInterface() {
                    @Override
                    public void onSyncFinishOk() {
                        //Remove loading bar and message
                        //transition to check in activity
                        transitionToMain();
                    }

                    @Override
                    public void onSyncFinishBad() {
                        //transition to error message
                        Log.e(TAG, "onSyncFinishBad: There was an error grabbing data from the server");
                        transitionToError("Se encontró un error al intentar conectarse al servidor. Procediendo sin conexión");
                    }

                    @Override
                    public void onUpdateFinished(Boolean finishedOk, String[] customerName) {

                    }
                }
        );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: RP)*#H(*RH@#( %(Y@#(*Y%(H@$IURHTKUEWHFIEGHRF(H Q(*(*#@Y RIHKUEFHIUPQGF*IGQIEWGFI QWG(F(*@#(Y@*5F4W68R460Q @W3210@6!54 R6840Q@#6540R 86#Q4R60QW");
        
    }

    @Override
    protected void onResume() {
        super.onResume();


        barcodeReceiver = new BarcodeReceiver();

        registerReceiver(barcodeReceiver, new IntentFilter(BARCODE_BROADCAST));

//        startScanBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startScanForQR();
//            }
//        });
//
//        acknowledgeErrorBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                transitionToMain();
//            }
//        });



//        final Context context = this;
//
//        customers.openDb(context);
//        customers.syncDb(context, new ActivityCallbackInterface() {
//                    @Override
//                    public void onSyncFinishOk() {
//                        //Remove loading bar and message
////                        ProgressBar load_bar = (ProgressBar) findViewById(R.id.loading_bar);
////                        TextView load_msg = (TextView) findViewById(R.id.loading_message);
////
////                        load_bar.setVisibility(View.INVISIBLE);
////                        load_msg.setVisibility(View.INVISIBLE);
//                        //transition to check in activity
//                        transitionToMain();
////                        startScanForQR();
//                    }
//
//                    @Override
//                    public void onSyncFinishBad() {
//                        //transition to error message
//                        Log.e(TAG, "onSyncFinishBad: There was an error grabbing data from the server");
//                        transitionToError("Se encontró un error al intentar conectarse al servidor. Procediendo sin conexión");
//                    }
//
//                    @Override
//                    public void onUpdateFinished(Boolean finishedOk, String[] customerName) {
//
//                    }
//                }
//        );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause: Closing DB and unregistering broadcast receiver.");
        customers.closeConnection();
        unregisterReceiver(barcodeReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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
            public void onUpdateFinished(Boolean finishedOk, String[] customerName) {
                //Transition to customer checked in message
                if (finishedOk) {
                    Log.i(TAG, "onUpdateFinished: Finished syncing " + customerId + ".");
                    transitionToCheckedIn(customerName);
                } else {
                    Log.i(TAG, "onUpdateFinished: Error syncing " + customerId + " through Customers Service.");
                }
                return;
            }
        });
        
    }

    private void transitionToMain() {
        loadBar.setVisibility(View.GONE);
        loadMsg.setVisibility(View.GONE);
        welcomeMsg.setVisibility(View.GONE);

        acknowledgeErrorBtn.setVisibility(View.GONE);
        warningMsg.setVisibility(View.GONE);

        //set button to start scan visible
        tip1Txt.setVisibility(View.VISIBLE);
        startScanBtn.setVisibility(View.VISIBLE);

//        transitionToCheckedIn(new String[]{"KEITH", "WONG"});
    }

    private void transitionToError(String errMsg) {
        loadBar.setVisibility(View.GONE);
        loadMsg.setVisibility(View.GONE);
        tip1Txt.setVisibility(View.GONE);
        startScanBtn.setVisibility(View.GONE);
        welcomeMsg.setVisibility(View.GONE);

        //set button to start transition to main screen
        acknowledgeErrorBtn.setVisibility(View.VISIBLE);
        warningMsg.setText(errMsg);
        warningMsg.setVisibility(View.VISIBLE);
    }

    private void transitionToCheckedIn(String[] customerName) {
        loadBar.setVisibility(View.GONE);
        loadMsg.setVisibility(View.GONE);
        tip1Txt.setVisibility(View.GONE);
        startScanBtn.setVisibility(View.GONE);
        acknowledgeErrorBtn.setVisibility(View.GONE);
        warningMsg.setVisibility(View.GONE);

        String msg = "El huésped ha sido registrado. Por favor salude al " +
                customerName[0] + " " +
                customerName[1] + ".";

        welcomeMsg.setText(msg);
        welcomeMsg.setVisibility(View.VISIBLE);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                transitionToMain();
            }
        }.start();
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
