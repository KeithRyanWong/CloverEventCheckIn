package com.kwover.eventcheck_in.util;

import com.clover.sdk.JSONifiable;
import com.clover.sdk.util.CloverAuth;
import com.clover.sdk.v1.customer.Customer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by keithwong on 10/29/18.
 */

public interface CustomersCallbackInterface {
//    void onQueryFinished(List<Customer> customers);
    void onSyncFinishOk();
    void onSyncFinishBad();
    void onUpdateFinished(Boolean finishedOk, String[] customerName);
//    void onQueryFinished(JSONArray customers);
//
//    void onAuthResult(CloverAuth.AuthResult authResult);
    void onHelpersInitialized(CloverAuth.AuthResult authResult);
}
