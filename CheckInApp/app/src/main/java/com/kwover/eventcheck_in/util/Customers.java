package com.kwover.eventcheck_in.util;

import java.util.Date;
import java.util.List;

import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.customer.*;
import android.accounts.Account;
import com.clover.sdk.util.CloverAccount;



import android.content.Context;
import android.database.sqlite.*;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;


/**
 * Created by keithwong on 10/25/18.
 */

public class Customers {
//    private Date lastSynced = null;
    private SQLiteDatabase db = null;
    private CustomersReaderDbHelper dbHelper;
    private CustomersServiceHelper servHelper;
    private static CustomersAPIHelper apiHelper;
    private Boolean isOpen = false;
//    private Account account;
//    private CustomerConnector connector;
    private static final String TAG = "Customers";

//    private List<Customer> customers;

    public Customers(){}

    public void initializeHelpers(Context context, final ActivityCallbackInterface cb) {
        dbHelper = new CustomersReaderDbHelper(context);
        servHelper = new CustomersServiceHelper(context);
        apiHelper = new CustomersAPIHelper(context);

        apiHelper.getCloverAuth(context, new CustomersCallbackInterface() {
            @Override
            public void onQueryFinished(JSONArray customers) {

            }

            @Override
            public void onUpdateFinished(Boolean finishedOk) {

            }

            @Override
            public void onAuthResult(CloverAuth.AuthResult authResult) {
                apiHelper.configureSettings(authResult);
                cb.onHelpersInitialized();
            }
        });
    }

    public void openDb(Context context) {
        db = dbHelper.getWritableDatabase();
        isOpen = true;
    }

    public void syncDb(final Context context, final ActivityCallbackInterface cb) {
        apiHelper.getCustomers(context, new CustomersCallbackInterface() {
//            @Override
//            public void onQueryFinished(List<Customer> customers) {
//                if (customers == null) {
//                    cb.onSyncFinishBad();
//                    return;
//                } else {
//                    Log.i(TAG, "onQueryFinished: will write customers to db");
//                    writeToDb(customers);
//                    cb.onSyncFinishOk();
//                }
//            }
            @Override
            public void onQueryFinished(JSONArray customers) {
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
            public void onUpdateFinished(Boolean result) {

            }

            @Override
            public void onAuthResult(CloverAuth.AuthResult authResult) {

            }
        });

    }

    public void markCustomerAttended(final Context context, final String customer_Id, final ActivityCallbackInterface cb) {
        //Try to update customers in DB first
        //Then try to update via the service/api
        dbHelper.updateRowByCustomerId(db, customer_Id, null, null, 1, 0);

        final String[] customerName = dbHelper.fetchFullName(db, customer_Id);

        servHelper.updateCustomer(context, customer_Id, new CustomersCallbackInterface() {
            @Override
            public void onQueryFinished(JSONArray customers) {

            }

            @Override
            public void onUpdateFinished(Boolean finishedOk) {
                if (finishedOk) {
                    dbHelper.updateRowByCustomerId(db, customer_Id, null, null, null, 1);
                }
                cb.onUpdateFinished(finishedOk, customerName);
            }

            @Override
            public void onAuthResult(CloverAuth.AuthResult authResult) {

            }
        });
    }

    public void resolveUnsyncedCustomers(final Context context) {
        List<String> customerIds = dbHelper.fetchUnsyncedEntries(db);

        if(customerIds != null) {
//            submit the customers through service
//            mark them synced
            for (String customerId : customerIds) {
                markCustomerAttended(context, customerId, new ActivityCallbackInterface() {
                    @Override
                    public void onSyncFinishOk() {

                    }

                    @Override
                    public void onSyncFinishBad() {

                    }

                    @Override
                    public void onUpdateFinished(Boolean finishedOk, String[] customerName) {
                        Log.i(TAG, "onUpdateFinished: synced " + customerName[0] + " " + customerName[1]);
                    }

                    @Override
                    public void onHelpersInitialized() {

                    }
                });
            }
        }
    }

//    private void writeToDb(List<Customer> customers) {
//        for(Customer customer : customers ) {
//            Log.d(TAG, "writeToDb: customer: " +
//                    customer.getFirstName() + " " +
//                    customer.getLastName() + ", cID: " +
//                    customer.getId() + ", marketing_Allowed: " +
//                    customer.getMarketingAllowed()
//            );
//
//            dbHelper.addRow(db,
//                    customer.getFirstName(),
//                    customer.getLastName(),
//                    customer.getId(),
//                    customer.getMarketingAllowed() ? 1 : 0
//            );
//        }
//    }

    private void writeToDb(JSONArray customers) {
        for(int i = 0; i < customers.length(); i++) {
            try {
                JSONObject customer = customers.getJSONObject(i);
                Log.d(TAG, "writeToDb: customer: " +
                        customer.getString("firstName") + " " +
                        customer.getString("lastName") + ", cID: " +
                        customer.getString("id") + ", marketing_Allowed: " +
                        customer.getBoolean("marketingAllowed")
                );

                dbHelper.addRow(db,
                        customer.getString("firstName"),
                        customer.getString("lastName"),
                        customer.getString("id"),
                        customer.getBoolean("marketingAllowed") ? 1 : 0
                );

            } catch (Exception e) {
                Log.e(TAG, "writeToDb: Error readying JSON object", e);
            }
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
