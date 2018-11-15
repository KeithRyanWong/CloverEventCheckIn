package com.kwover.eventcheck_in.util;

import android.accounts.Account;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.v1.merchant.Merchant;
import com.clover.sdk.v1.merchant.MerchantConnector;

/**
 * Created by keithwong on 11/13/18.
 */

public class MerchantServiceHelper {
    private Account account;
    private MerchantConnector connector;

    public static final String TAG = "MerchantServiceHelper";

    public MerchantServiceHelper(Context context) {
        account = CloverAccount.getAccount(context);
    }

    private void connect(Context context) {
        disconnect();
        account = CloverAccount.getAccount(context);
        if(account != null) {
            connector = new MerchantConnector(context, account, null);
            connector.connect();
        }
    }

    private void disconnect() {
        if(connector != null) {
            connector.disconnect();
            connector = null;
        }
    }

    public void getMerchant(Context context, MerchantCallbackInterface cb) {
        final MerchantCallbackInterface callback = cb;
        if(connector == null) {
            connect(context);
        }

        new AsyncTask<Void, Void, Merchant>() {
            @Override
            protected Merchant doInBackground(Void... voids) {
                try {
                    return connector.getMerchant();
                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Error getting Merchant data through service", e);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Merchant merchant) {
                super.onPostExecute(merchant);

                disconnect();
                callback.onReceiveMerchant(merchant);
            }
        }.execute();
    }
}
