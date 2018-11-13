package com.kwover.eventcheck_in.util;

import com.clover.sdk.v1.merchant.Merchant;

/**
 * Created by keithwong on 11/13/18.
 */

public interface MerchantCallbackInterface {
    void onReceiveMerchant(Merchant merchant);
}
