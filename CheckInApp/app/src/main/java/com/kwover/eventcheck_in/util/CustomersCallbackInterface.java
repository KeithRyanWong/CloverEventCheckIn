package com.kwover.eventcheck_in.util;

import com.clover.sdk.v1.customer.Customer;

import java.util.List;

/**
 * Created by keithwong on 10/29/18.
 */

public interface CustomersCallbackInterface {
    void onQueryFinished(List<Customer> customers);
    void onUpdateFinished(Boolean finishedOk);
}
