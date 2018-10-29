package com.kwover.eventcheck_in.util;

import java.util.Date;
import com.clover.sdk.v1.customer.*;
import android.accounts.Account;
import com.clover.sdk.util.CloverAccount;



import android.content.Context;
import android.database.sqlite.*;
import android.os.AsyncTask;


/**
 * Created by keithwong on 10/25/18.
 */

public class Customers {
    private static Date lastSynced = null;
    private static SQLiteDatabase db = null;
    private static CustomersReaderDbHelper dbHelper;
    private static CustomersServiceHelper servHelper;
    private static CustomersAPIHelper apiHelper;
    private static Boolean isOpen = false;
    private Account account;
    private CustomerConnector connector;

    public void openDb(Context context) {
        dbHelper = new CustomersReaderDbHelper(context);
        servHelper = new CustomersServiceHelper(context);
        apiHelper = new apiHelper();

        db = dbHelper.getWritableDatabase();
        isOpen = true;
    }

    public void syncDb() {
        if(isOpen) {

        } else {

        }
    }
    public boolean isOpen() {
        return db != null;
    }


    public void closeConnection() {
        db.close();
        isOpen = false;
    }
}
