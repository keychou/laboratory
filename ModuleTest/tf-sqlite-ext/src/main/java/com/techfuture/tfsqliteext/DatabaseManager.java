package com.techfuture.tfsqliteext;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;

/**
 * Created by klein on 18-9-1.
 */

public class DatabaseManager {
    public final String TAG = "tech-DatabaseManager";

    SQLiteDatabase mDb;
    Context mContext;
    Cursor mCursor;

    public DatabaseManager(Context context, String path, String table){
        try{
            mDb = SQLiteDatabase.openDatabase(path, null, OPEN_READONLY);;
            this.mContext = context;
            Log.d(TAG, "db.getPath() = " + mDb.getPath());

            mCursor = mDb.query(table, null,null, null, null, null, null);

            //Cursor cursor = db.rawQuery("select * from mbn_list_msg", null);

            int rowsnum = mCursor.getCount();
            Log.d(TAG, "total rows = " + rowsnum);
        } catch (Exception e){
            Log.d(TAG, "fail to open " + path);
            e.printStackTrace();
        }
    }

    public int getRowCount(){
        return mCursor.getCount();
    }

    public boolean hitItemFromColumn(String targetString, String columnname) {
        boolean isitemFound = false;

        mCursor.moveToPosition(-1);

        while (mCursor.moveToNext()) {
            //Log.d(TAG ,"position = " + mCursor.getPosition() + ", database imei = " + getStringFromRow(mCursor, columnname) + ", targetString = " + targetString);
            if (targetString.equals(getStringFromRow(mCursor, columnname))) {
                isitemFound = true;
            }
        }
        return isitemFound;
    }

    private String getStringFromRow(Cursor cursor, String columnname){
        String item = cursor.getString(cursor.getColumnIndex(columnname));
        return item;
    }
}
