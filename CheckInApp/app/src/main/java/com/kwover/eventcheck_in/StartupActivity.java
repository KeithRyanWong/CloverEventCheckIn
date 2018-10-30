package com.kwover.eventcheck_in;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kwover.eventcheck_in.util.Customers;

public class StartupActivity extends AppCompatActivity {


//    When app is started:
//    check for existence of internal customers db
//    if it doesn't exist, make it
//    pull/sync internal customers
//          After syncing should switch to main screen/thread


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        final Context context = this;

        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.startTest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Customers.openDb(context);
                Customers.syncDb(context);
            }
        });
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

}
