package com.kwover.eventcheck_in;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
//import android.widget.ProgressBar;
import android.widget.TextView;

import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.Intents;
import com.clover.sdk.v3.scanner.BarcodeScanner;
import com.kwover.eventcheck_in.util.ActivityCallbackInterface;
import com.kwover.eventcheck_in.util.Customers;



public class StartupActivity extends AppCompatActivity {
    protected Customers customers;
    private static final String BARCODE_BROADCAST = "com.clover.BarcodeBroadcast";
    private String scannedBarcode;
    private BarcodeReceiver barcodeReceiver;

    private static final String TAG = "Startup Activity";

//    private ProgressBar loadBar;
//    private TextView loadMsg;
//    private TextView tip1Txt;
    private TextView warningMsg;
    private TextView welcomeMsg;
//    private TextView welcomeMsg2;
    private Button startScanBtn;
    private Button okBtn;
    private Button acknowledgeErrorBtn;
    private Button syncBtn;
    private ConstraintLayout loadingView;
    private ConstraintLayout mainView;
    private ConstraintLayout errorView;
    private ConstraintLayout welcomeView;
    private static final String LOADING_VIEW = "LOADING_VIEW";
    private static final String MAIN_VIEW = "MAIN_VIEW";
    private static final String ERROR_VIEW = "ERROR_VIEW";
    private static final String WELCOME_VIEW = "WELCOME_VIEW";



//    When app is started:
//    check for existence of internal customers db
//    if it doesn't exist, make it
//    pull/sync internal customers
//          After syncing should switch to main screen/thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.activity_startup);
//        loadBar = (ProgressBar) findViewById(R.id.loading_bar);
//        loadMsg = (TextView) findViewById(R.id.loading_message);
//        tip1Txt = (TextView) findViewById(R.id.tip1);
        startScanBtn = findViewById(R.id.startScan);
        syncBtn = findViewById(R.id.syncButton);
        acknowledgeErrorBtn = findViewById(R.id.acknowledgementBtn);
        okBtn = findViewById(R.id.ok);
        warningMsg = findViewById(R.id.warningMsg);
        welcomeMsg = findViewById(R.id.welcomeMsg);
//        welcomeMsg2 = findViewById(R.id.welcomeMsg2);
        loadingView = findViewById(R.id.loadingView);
        mainView = findViewById(R.id.mainView);
        errorView = findViewById(R.id.errorView);
        welcomeView = findViewById(R.id.welcomeView);

        transitionToLoad();



        customers = new Customers();
        final Context context = this;

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

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transitionToMain();
            }
        });

        syncBtn.setSoundEffectsEnabled(false);
        syncBtn.setOnClickListener(new View.OnClickListener() {
            private Boolean clickable = true;

            @Override
            public void onClick(View v) {
                if(clickable) {
                    Log.i(TAG, "onClick: Sync button clicked");
                    customers.resolveUnsyncedCustomers(context);

                    clickable = false;
                    new CountDownTimer(8000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            clickable = true;
                        }
                    }.start();
                }
            }
        });

        customers.initializeHelpers(context, new ActivityCallbackInterface() {
            @Override
            public void onSyncFinishOk() {

            }

            @Override
            public void onSyncFinishBad() {

            }

            @Override
            public void onUpdateFinished(Boolean finishedOk, String[] customerName) {

            }

            @Override
            public void onHelpersInitialized() {
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

                            @Override
                            public void onHelpersInitialized() {

                            }
                        }
                );
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();


        barcodeReceiver = new BarcodeReceiver();

        registerReceiver(barcodeReceiver, new IntentFilter(BARCODE_BROADCAST));
    }

    @Override
    protected void onPause() {
        super.onPause();
//        Log.i(TAG, "onPause: Closing DB and unregistering broadcast receiver.");
//        customers.closeConnection();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

            }

            @Override
            public void onSyncFinishBad() {

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

            }

            @Override
            public void onHelpersInitialized() {

            }
        });
        
    }

    private void toggleVisibleView(String view) {
        loadingView.setVisibility(view == LOADING_VIEW ? View.VISIBLE : View.GONE);
        mainView.setVisibility(view == MAIN_VIEW ? View.VISIBLE : View.GONE);
        errorView.setVisibility(view == ERROR_VIEW ? View.VISIBLE : View.GONE);
        welcomeView.setVisibility(view == WELCOME_VIEW ? View.VISIBLE : View.GONE);
    }

    private void transitionToLoad() {
        toggleVisibleView(LOADING_VIEW);
    }

    private void transitionToMain() {
        toggleVisibleView(MAIN_VIEW);
    }

    private void transitionToError(String errMsg) {
        warningMsg.setText(errMsg);
        toggleVisibleView(ERROR_VIEW);
    }

    private void transitionToCheckedIn(String[] customerName) {

//        String msg = "El huésped ha sido registrado. Por favor salude al " +
//                customerName[0] + " " +
//                customerName[1] + ".";
//
//        String msg2 = "The guest has been checked in. Please give your welcome to " +
//                customerName[0] + " " +
//                customerName[1] + ".";

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < customerName.length; i++) {
            if(i == 1) {
                builder.append(" ");
            }
            builder.append(customerName[i]);
        }

        String customer = builder.toString();

        toggleVisibleView(WELCOME_VIEW);
        welcomeMsg.setText(customer);
//        welcomeMsg2.setText(msg2);

//        new CountDownTimer(8000, 1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                transitionToMain();
//            }
//        }.start();
    }

    private static Bundle getBarcodeSetting(final boolean enabled) {
        final Bundle extras = new Bundle();
        extras.putBoolean(Intents.EXTRA_START_SCAN, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_MERCHANT_PREVIEW, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_CLOSE_BUTTON, enabled);
        extras.putBoolean(Intents.EXTRA_SHOW_LED_BUTTON, enabled);
        extras.putBoolean(Intents.EXTRA_LED_ON, false);
        return extras;
    }

    public boolean startBarcodeScanner() {
        return new BarcodeScanner(this).startScan(getBarcodeSetting(true));
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
