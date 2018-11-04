package com.kwover.eventcheck_in.util;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.ServiceConnector;
import com.clover.sdk.v1.customer.Customer;
import com.clover.sdk.v1.customer.CustomerConnector;

import java.util.List;

/**
 * Created by keithwong on 10/29/18.
 */

public class CustomersServiceHelper {

    private Account account;
    private CustomerConnector connector;
    private Boolean busy;

    public static final String TAG = "CustomersServiceHelper";


    public CustomersServiceHelper(Context context) {
        account = CloverAccount.getAccount(context);
    }

    private void connect(Context context) {
        disconnect();
        account = CloverAccount.getAccount(context);
        if(account != null) {
            connector = new CustomerConnector(context, account, null);
            connector.connect();
        }
    }

    private void disconnect() {
        if(connector != null) {
          connector.disconnect();
          connector = null;
        }
    }

    public Boolean isBusy() {
        return busy;
    }


    public void getCustomers(Context context, CustomersCallbackInterface cb) {

        final CustomersCallbackInterface callback = cb;
        connect(context);
        busy = true;

        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                List<Customer> customers = null;
                try {
                    customers = connector.getCustomers();
                } catch (Exception e) {
                    Log.e(TAG, "getCustomers: Error getting customers through service", e);
                    return null;
                }
                return customers;
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                super.onPostExecute(customers);
                disconnect();
                busy = false;
                callback.onQueryFinished(customers);
            }
        }.execute();
    }

    public void updateCustomer(Context context, String id, CustomersCallbackInterface cb) {
        final CustomersCallbackInterface callback = cb;
//        final Customer customer = c;
        final String customer_id = id;
        if(connector == null) {
            connect(context);
        }
        busy = true;

        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    connector.setMarketingAllowed(customer_id, true);
                } catch (Exception e) {
                    Log.e(TAG, "updateCustomer.doInBackground: Error updating Customers through Service", e);
                    return false;
                }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                disconnect();
                busy = false;
                callback.onUpdateFinished(aBoolean);
            }
        }.execute();

    }
}
