package com.kwover.eventcheck_in.util;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.clover.sdk.util.CloverAccount;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.customer.Customer;
import com.clover.sdk.v1.merchant.Merchant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by keithwong on 10/29/18.
 */

public class CustomersAPIHelper {
    private Account account;
    private String authToken;
    private String baseUrl;
    private String mid;
    private String customersUri;
    private MerchantServiceHelper merchantHelper;
    private final static String TAG = "CustomersAPIHelper";
    CloverAuth.AuthResult authResult = null;

    public CustomersAPIHelper(Context context) {
        account = CloverAccount.getAccount(context);
        merchantHelper = new MerchantServiceHelper(context);
    }

    public void initializeAPIHelper(final Context context, final CustomersAPICallbackInterface cb) {
        merchantHelper.getMerchant(context, new MerchantCallbackInterface() {
            @Override
            public void onReceiveMerchant(Merchant merchant) {
                mid = merchant.getId();
                getCloverAuth(context, cb);
            }
        });
    }


    public void getCloverAuth(final Context context, final CustomersAPICallbackInterface cb) {
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
                cb.onAuthResult(result);
            }
        }.execute();
    }
    
    public void configureSettings(CloverAuth.AuthResult result) {
        if(result == null){
            Log.e(TAG, "configureSettings: Cannot configure with null result");
            return;
        }

        authToken = result.authToken;
        baseUrl = result.baseUrl;
        customersUri = "/v3/merchants/" + mid + "/customers?limit=500";
    }

    public void getCustomers(Context context, final CustomersAPICallbackInterface cb) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.GET, baseUrl + customersUri,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONArray customers = null;
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            customers = jsonResponse.getJSONArray("elements");
                        } catch (Exception e) {
                            Log.e(TAG, "onResponse: error converting response to JSON", e);
                        }
                        cb.onQueryFinished(customers);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: error getting customers", error);
                cb.onQueryFinished(null);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Authorization", "Bearer " + authToken);
                return headers;
            }
        };


        queue.add(request);
    }
}
