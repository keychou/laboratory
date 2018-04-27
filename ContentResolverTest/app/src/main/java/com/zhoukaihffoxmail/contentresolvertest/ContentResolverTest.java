package com.zhoukaihffoxmail.contentresolvertest;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ContentResolverTest extends AppCompatActivity {

    public static final String TAG = "ContentResolverTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_resolver_test);

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(ContactsBook.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()){
            Log.d(TAG, cursor.toString());
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsBook.Contacts.AGE));
            Log.d(TAG, "contactId = " + contactId);
            (new Exception()).printStackTrace();

        }
    }
}
