package com.kwover.eventcheck_in.util;

import java.util.Date;
import java.util.List;

import com.clover.sdk.v1.customer.*;
import android.accounts.Account;
import com.clover.sdk.util.CloverAccount;



import android.content.Context;
import android.database.sqlite.*;
import android.os.AsyncTask;
import android.util.Log;


/**
 * Created by keithwong on 10/25/18.
 */

public class Customers {
    private static Date lastSynced = null;
    private static SQLiteDatabase db = null;
    private static CustomersReaderDbHelper dbHelper;
    private static CustomersServiceHelper servHelper;
    private static CustomersAPIHelper apiHelper;
    private static Boolean isOpen = false;
    private Account account;
    private CustomerConnector connector;
    private static final String TAG = "Customers";

//    private List<Customer> customers;

    public Customers(){}

    public static void openDb(Context context) {
//        dbHelper = new CustomersReaderDbHelper(context);
        servHelper = new CustomersServiceHelper(context);
//        apiHelper = new CustomersAPIHelper();

//        db = dbHelper.getWritableDatabase();
//        isOpen = true;
    }

    public static void syncDb(Context context) {
        servHelper.getCustomers(context, new CustomersCallbackInterface() {
            @Override
            public void onQueryFinished(List<Customer> customers) {
                Log.i(TAG, "onQueryFinished: ");
//                writeToDb(customers);
                //Customers should be set to customers
                //Then list should be parsed
                //  and be written to the database via dbHelper
                //  Keeping in mind that some entries may have just been updated
            }

            @Override
            public void onUpdateFinished() {
                Log.i(TAG, "onUpdateFinished: ");
            }
        });

    }

    private void writeToDb(List<Customer> customers) {

    }

    public boolean isOpen() {
        return db != null;
    }


    public void closeConnection() {
        db.close();
        isOpen = false;
    }
}
