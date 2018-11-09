package com.kwover.eventcheck_in.util;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.customer.Customer;

import java.util.List;

/**
 * Created by keithwong on 10/29/18.
 */

public class CustomersAPIHelper {
    private Account account;
    private Context context;
    private String authToken;
    private String baseUrl;
//    private final static String CUSTOMERS_URI = "/v2/merchant";
    private final static String TAG = "CustomersAPIHelper";
    CloverAuth.AuthResult authResult = null;

    public CustomersAPIHelper(Context context) {
        this.context = context;
        account = CloverAccount.getAccount(context);

        getCloverAuth();
    }

    private void getCloverAuth() {
        // This needs to be done on a background thread
        new AsyncTask<Void, Void, CloverAuth.AuthResult>() {
            private Account mAccount = account;
            private Context mContext = context;
            private final String mTAG = TAG;

            @Override
            protected CloverAuth.AuthResult doInBackground(Void... params) {
                try {
                    return CloverAuth.authenticate(mContext, mAccount);
                } catch (OperationCanceledException e) {
                    Log.e(mTAG, "Authentication cancelled", e);
                } catch (Exception e) {
                    Log.e(mTAG, "Error retrieving authentication", e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(CloverAuth.AuthResult result) {
                authResult = result;
                configureSettings(result);
            }
        }.execute();
    }
    
    private void configureSettings(CloverAuth.AuthResult result) {
        if(result == null){
            Log.e(TAG, "configureSettings: Cannot configure with null result");
        }

        authToken = result.authToken;
        baseUrl = result.baseUrl;
    }

    public List<Customer> getCustomers(CustomersCallbackInterface cb) {
        List<Customer> customers = null;

        return customers;
    }
}
