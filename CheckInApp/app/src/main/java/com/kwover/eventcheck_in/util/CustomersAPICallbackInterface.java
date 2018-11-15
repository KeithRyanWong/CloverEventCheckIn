package com.kwover.eventcheck_in.util;

import com.clover.sdk.util.CloverAuth;

import org.json.JSONArray;

/**
 * Created by keithwong on 11/13/18.
 */

public interface CustomersAPICallbackInterface {
    void onQueryFinished(JSONArray customers);
    void onAuthResult(CloverAuth.AuthResult authResult);
}
