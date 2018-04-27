package com.example.zhoukai.sqlitetest;

import android.app.Application;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.util.ArrayList;

import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;

/**
 * Created by zhoukai on 17-1-19.
 */

public class MbnPolicyManager {

    public final String TAG = "MbnPolicyManager";

    private ArrayList<String> mMbnSelected;

    SQLiteDatabase db;

    Context context;

    Cursor cursor;

    MbnPolicyManager(){

    }

    MbnPolicyManager(Context context){
        db = SQLiteDatabase.openDatabase("/sdcard/mbn_list.db", null, OPEN_READONLY);;
        this.context = context;

        Log.d(TAG, "db.getPath() = " + db.getPath());

        cursor = db.query("mbn_list", null,null, null, null, null, null);

        //Cursor cursor = db.rawQuery("select * from mbn_list_msg", null);

        int rowsnum = cursor.getCount();

        Log.d(TAG, "total rows = " + rowsnum);
    }

    public ArrayList<String> findMbnPath() {

        mMbnSelected = new ArrayList<>();

        //cursor = db.query("mbn_list_msg", null,null, null, null, null, null);

        cursor.moveToPosition(-1);

        while (cursor.moveToNext()) {
            Log.d(TAG ,"try to match record = " + cursor.getPosition() + ", total = " + cursor.getCount());
            if (isPlmnMatched(cursor) || isIccidMatched(cursor)) {
                mMbnSelected.add(cursor.getString(cursor.getColumnIndex("path")).replace("\"", ""));
            }
        }

        Log.d(TAG, "cycle end, " + mMbnSelected.size() + " records matched");
        for (int i = 0; i < mMbnSelected.size(); i++) {
            Log.d(TAG, "mMbnSelected[" + i + "] = " + mMbnSelected.get(i));
        }

        return mMbnSelected;
    }

    public Boolean isPlmnMatched(Cursor cursor){
        int mcc_mnc_list_count = cursor.getInt(cursor.getColumnIndex("mcc_mnc_list_count"));
        String mcc_mnc_list = cursor.getString(cursor.getColumnIndex("mcc_mnc_list"));
        String[] plmns = mcc_mnc_list.replace("\"","").split("[,]");

        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String tplmn = mTm.getSimOperator();


        Log.d(TAG, "tplmn = " + tplmn + ", mcc_mnc_list = " + mcc_mnc_list);
        for (String str:plmns){
            if (str.length() == 0){
                continue;
            }

            Boolean ret = str.regionMatches(0, tplmn, 0, str.length());

            if (ret){
                Log.d(TAG, "plmn matched: " + tplmn);
                return true;
            }
        }
        Log.d(TAG, "no plmn matched");
        return false;
    }


    public Boolean isIccidMatched(Cursor cursor){
        int iin_list_count = cursor.getInt(cursor.getColumnIndex("iin_list_count"));
        String iin_list = cursor.getString(cursor.getColumnIndex("iin_list"));
        String[] iccids = iin_list.replace("\"","").split("[,]");

        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String iccid = mTm.getSimSerialNumber();

        Log.d(TAG, "iccid = " + iccid + ", iin_list = " + iin_list);
        for (String str:iccids){
            if (str.length() == 0){
                continue;
            }

            Boolean ret = str.regionMatches(0, iccid, 0, str.length());
            if (ret){
                Log.d(TAG, "iccid matched: " + iccid);
                return true;
            }
        }
        Log.d(TAG, "no iccid matched");
        return false;
    }

    public void listMbn(){

    //    cursor = db.query("mbn_list_msg", null,null, null, null, null, null);
        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String mbn_name = cursor.getString(cursor.getColumnIndex("mbn_name"));
            int iin_list_count = cursor.getInt(cursor.getColumnIndex("iin_list_count"));
            String iin_list = cursor.getString(cursor.getColumnIndex("iin_list"));
            int mcc_mnc_list_count = cursor.getInt(cursor.getColumnIndex("mcc_mnc_list_count"));
            String mcc_mnc_list = cursor.getString(cursor.getColumnIndex("mcc_mnc_list"));
            String path = cursor.getString(cursor.getColumnIndex("path"));

            Log.d(TAG, id + ", " +  mbn_name + ", " +  iin_list_count + ", " +  iin_list + ", " +  mcc_mnc_list_count + ", "  + mcc_mnc_list +  ", "  + path);
        }

    }
}
