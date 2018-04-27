package com.zhoukaihffoxmail.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.BaseColumns;

/**
 * Created by zhoukai on 17-3-9.
 */

public class ContactsBook {
    public static final String TAG = "ContactsBook";

    public static final String AUTHORITY = "com.contentprovidertest.contacts_book";

    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static class Contacts implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/contacts");

        public static final String NAME = "name";

        public static final String AGE = "age";

        public static final String NUMBER = "number";


        public static Uri addCall(Context context, String name, String age, String number) {
            Uri result = null;
            final ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues(3);
            values.put(NAME, name);
            values.put(AGE, Integer.valueOf(age));
            values.put(NUMBER, Long.valueOf(number));

            return result;
        }
    }
}
