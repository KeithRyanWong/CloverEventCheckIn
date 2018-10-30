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
//    private List<Customer> customers;
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

    public void getCustomers(Context context, CustomersCallbackInterface cb) {

        final CustomersCallbackInterface callback = cb;
        connect(context);

        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                List<Customer> customers = null;
                try {
                    customers = connector.getCustomers();
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Error getting customers through service", e);
                    return null;
                }
                return customers;
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                super.onPostExecute(customers);
                disconnect();
                callback.onQueryFinished(customers);
            }
        }.execute();
    }

    public void updateCustomer(Customer customer) {

    }
}
