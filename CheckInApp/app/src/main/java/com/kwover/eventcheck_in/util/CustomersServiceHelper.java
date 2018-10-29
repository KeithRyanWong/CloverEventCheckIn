package com.kwover.eventcheck_in.util;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;

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
    private List<Customers> customers;

    public CustomersServiceHelper(Context context) {
        account = CloverAccount.getAccount(context);
    }

    private void connect(Context context) {
        disconnect();
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

    public void getCustomers() {
        new AsyncTask<Void, Void, Customer>() {
            @Override
            protected Customer doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Customer customer) {
                super.onPostExecute(customer);
            }
        }.execute();
    }

    public void updateCustomer(Customer customer) {

    }
}
