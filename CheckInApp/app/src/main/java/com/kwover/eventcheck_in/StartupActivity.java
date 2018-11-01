package com.kwover.eventcheck_in;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kwover.eventcheck_in.util.ActivityCallbackInterface;
import com.kwover.eventcheck_in.util.Customers;

public class StartupActivity extends AppCompatActivity {
    protected Customers customers;

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
                }

                @Override
                public void onSyncFinishBad() {
                    //transition to error message
                }
            }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        customers.closeConnection();
    }

    private void startupActivity() {

    }
}
