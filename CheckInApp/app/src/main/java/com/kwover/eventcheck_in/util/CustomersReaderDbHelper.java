package com.kwover.eventcheck_in.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.clover.sdk.v1.customer.Customer;

import java.util.ArrayList;
import java.util.List;

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
            CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " TEXT NOT NULL UNIQUE," +
            CustomerEntry.COLUMN_NAME_MARKETING_ALLOWED + " INTEGER NOT NULL," +
            CustomerEntry.COLUMN_NAME_SYNCED + " INTEGER DEFAULT 1)";

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
        // Will strings inserted into clause be okay?
        final String WHERE_CLAUSE =
                CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " = ? AND " +
                CustomerEntry.COLUMN_NAME_SYNCED + " = 1";

        ContentValues vals = new ContentValues();
        vals.put(CustomerEntry.COLUMN_NAME_FIRST_NAME, fn);
        vals.put(CustomerEntry.COLUMN_NAME_LAST_NAME, ln);
        vals.put(CustomerEntry.COLUMN_NAME_CUSTOMER_ID, c_id);
        vals.put(CustomerEntry.COLUMN_NAME_MARKETING_ALLOWED, m_allowed);

        //If values are new, a new row will be added
        //  Otherwise a conflict will be risen via the unique customer key and row will be skipped
        db.insertWithOnConflict(CustomerEntry.TABLE_NAME, null, vals, SQLiteDatabase.CONFLICT_IGNORE);
        //Entry with customer_id will be updated only if it is marked as synced
        db.updateWithOnConflict(CustomerEntry.TABLE_NAME, vals, WHERE_CLAUSE, new String[]{c_id}, SQLiteDatabase.CONFLICT_NONE);
    }

    public void updateRowByCustomerId(SQLiteDatabase db, String c_id, String fn, String ln, Integer m_allowed, Integer synced) {
        final String WHERE_CLAUSE = CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " = ?";

        ContentValues vals = new ContentValues();
        if(fn != null) {
            vals.put(CustomerEntry.COLUMN_NAME_FIRST_NAME, fn);
        }
        if(ln != null) {
            vals.put(CustomerEntry.COLUMN_NAME_LAST_NAME, ln);
        }
        if(m_allowed != null) {
            vals.put(CustomerEntry.COLUMN_NAME_MARKETING_ALLOWED, m_allowed);
        }
        if(synced != null) {
            vals.put(CustomerEntry.COLUMN_NAME_SYNCED, synced);
        }

        db.update(CustomerEntry.TABLE_NAME, vals, WHERE_CLAUSE, new String[]{c_id});
    }

    public String[] fetchFullName(SQLiteDatabase db, String c_id) {
        String[] cols = new String[]{CustomerEntry.COLUMN_NAME_FIRST_NAME, CustomerEntry.COLUMN_NAME_LAST_NAME};
        final String WHERE_CLAUSE = CustomerEntry.COLUMN_NAME_CUSTOMER_ID + " = ?";
        String[] customerName = new String[2];

        Cursor cursor = db.query(CustomerEntry.TABLE_NAME, cols, WHERE_CLAUSE, new String[]{c_id}, null, null, null, null);
        cursor.moveToFirst();

        String[] columns = cursor.getColumnNames();

        for (int i = 0; i < columns.length; i++) {
            if(columns[i].equals(CustomerEntry.COLUMN_NAME_FIRST_NAME)) {
                customerName[0] = cursor.getString(i);
            } else if (columns[i].equals(CustomerEntry.COLUMN_NAME_LAST_NAME)) {
                customerName[1] = cursor.getString(i);
            }
        }

        return customerName;
    }

    public List<String> fetchUnsyncedEntries(SQLiteDatabase db) {

        String[] cols = new String[] {CustomerEntry.COLUMN_NAME_CUSTOMER_ID};
        final String WHERE_CLAUSE = CustomerEntry.COLUMN_NAME_SYNCED + " = 0";


        Cursor cursor = db.query(CustomerEntry.TABLE_NAME, cols, WHERE_CLAUSE, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            List<String> customerIds = new ArrayList<String>();
            customerIds.add(cursor.getString(0));

            while(cursor.moveToNext()){
                customerIds.add(cursor.getString(0));
            }

            return customerIds;
        }

        return null;
    }
}
