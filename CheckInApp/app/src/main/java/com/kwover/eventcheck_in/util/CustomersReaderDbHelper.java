package com.kwover.eventcheck_in.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.clover.sdk.v1.customer.Customer;

/**
 * Created by keithwong on 10/26/18.
 */

public class CustomersReaderDbHelper extends SQLiteOpenHelper{
    public static class CustomerEntry implements BaseColumns {
        public static final String TABLE_NAME = "customer";
        public static final String COLUMN_NAME_FIRST_NAME = "first_name";
        public static final String COLUMN_NAME_LAST_NAME = "last_name";
        public static final String COLUMN_NAME_CUSTOMER_ID = "customer_id";
        public static final String COLUMN_NAME_MARKETING_ALLOWED = "marketing_allowed";
        public static final String COLUMN_NAME_SYNCED = "synced";
    }

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " +
            CustomerEntry.TABLE_NAME + " (" +
            CustomerEntry._ID + " INTEGER PRIMARY KEY," +
            CustomerEntry.COLUMN_NAME_FIRST_NAME + " TEXT," +
            CustomerEntry.COLUMN_NAME_LAST_NAME + " TEXT," +
            CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " TEXT," +
            CustomerEntry.COLUMN_NAME_MARKETING_ALLOWED + " INTEGER," +
            CustomerEntry.COLUMN_NAME_SYNCED + " INTEGER)";

//    private static final String SQL_Create_Index = "CREATE UNIQUE INDEX customer_customer_ids ON " +
//            CustomerEntry.TABLE_NAME + " (" +
//            CustomerEntry.COLUMN_NAME_CUSTOMER_ID + ")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + CustomerEntry.TABLE_NAME;


    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CUSTOMERS.db";

    public CustomersReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);

    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void addRow(SQLiteDatabase db, String fn, String ln, String c_id, Integer m_allowed) {
        ContentValues vals = new ContentValues();
        vals.put(CustomerEntry.COLUMN_NAME_FIRST_NAME, fn);
        vals.put(CustomerEntry.COLUMN_NAME_LAST_NAME, ln);
        vals.put(CustomerEntry.COLUMN_NAME_CUSTOMER_ID, c_id);
        vals.put(CustomerEntry.COLUMN_NAME_MARKETING_ALLOWED, m_allowed);
        vals.put(CustomerEntry.COLUMN_NAME_SYNCED, 0);

        db.insert(CustomerEntry.TABLE_NAME, null, vals);
    }

}
