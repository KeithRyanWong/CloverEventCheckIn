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
    private Date lastSynced = null;
    private SQLiteDatabase db = null;
    private CustomersReaderDbHelper dbHelper;
    private CustomersServiceHelper servHelper;
//    private static CustomersAPIHelper apiHelper;
    private Boolean isOpen = false;
    private Account account;
    private CustomerConnector connector;
    private static final String TAG = "Customers";

//    private List<Customer> customers;

    public Customers(){}

    public void openDb(Context context) {
        dbHelper = new CustomersReaderDbHelper(context);
        servHelper = new CustomersServiceHelper(context);
//        apiHelper = new CustomersAPIHelper();

        db = dbHelper.getWritableDatabase();
        isOpen = true;
    }

    public void syncDb(final Context context, final ActivityCallbackInterface cb) {
        servHelper.getCustomers(context, new CustomersCallbackInterface() {
            @Override
            public void onQueryFinished(List<Customer> customers) {
                if (customers == null) {
                    cb.onSyncFinishBad();
                    return;
                } else {
                    Log.i(TAG, "onQueryFinished: will write customers to db");
                    writeToDb(customers);
                    cb.onSyncFinishOk();
                }
            }

            @Override
            public void onUpdateFinished() {
                Log.i(TAG, "onUpdateFinished: ");
            }
        });

    }

    private void writeToDb(List<Customer> customers) {
        for(Customer customer : customers ) {
            Log.d(TAG, "writeToDb: customer: " +
                    customer.getFirstName() + " " +
                    customer.getLastName() + ", cID: " +
                    customer.getId() + ", marketing_Allowed: " +
                    customer.getMarketingAllowed()
            );

            dbHelper.addRow(db,
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getId(),
                    customer.getMarketingAllowed() ? 1 : 0
            );
        }
    }

    public Boolean isSyncing () {
        return servHelper.isBusy();
    }

    public Boolean isOpen() {
        return db != null;
    }


    public void closeConnection() {
        db.close();
        isOpen = false;
    }
}
