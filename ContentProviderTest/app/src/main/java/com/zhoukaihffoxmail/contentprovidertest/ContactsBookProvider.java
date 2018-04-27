package com.zhoukaihffoxmail.contentprovidertest;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by zhoukai on 17-3-9.
 */

public class ContactsBookProvider extends ContentProvider{

    private static UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int CALL = 1;

    private MyDatabaseHelper dbHelper;

    static {
        matcher.addURI(ContactsBook.AUTHORITY, "contacts", CALL);
    }

    public boolean onCreate(){
        dbHelper = new MyDatabaseHelper(this.getContext(), "linkman.db", 1);

        (new Exception()).printStackTrace();
        return true;
    }


    public Uri insert(Uri uri, ContentValues values)
    {
        // 获得数据库实例
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 插入数据，返回行ID
        long rowId = db.insert("contacts", ContactsBook.Contacts._ID, values);
        // 如果插入成功返回uri
        if (rowId > 0)
        {
            // 在已有的 Uri的后面追加ID数据
            Uri wordUri = ContentUris.withAppendedId(uri, rowId);
            // 通知数据已经改变
            getContext().getContentResolver().notifyChange(wordUri, null);
            return wordUri;
        }
        return null;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 记录所删除的记录数
        int num = 0;
        // 对于uri进行匹配。
        switch (matcher.match(uri))
        {
            case CALL:
                num = db.delete("contacts", selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        // 通知数据已经改变
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // 记录所修改的记录数
        int num = 0;
        switch (matcher.match(uri))
        {
            case CALL:
                num = db.update("contacts", values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
        // 通知数据已经改变
        getContext().getContentResolver().notifyChange(uri, null);
        return num;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (matcher.match(uri))
        {
            case CALL:
                // 执行查询
                return db.query("contacts", projection, selection, selectionArgs,
                        null, null, sortOrder);
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
    }
    // 返回指定uri参数对应的数据的MIME类型
    @Override
    public String getType(Uri uri)
    {
        switch (matcher.match(uri))
        {
            // 如果操作的数据是多项记录
            case CALL:
                return "vnd.android.cursor.dir/org.crazyit.dict";
            default:
                throw new IllegalArgumentException("未知Uri:" + uri);
        }
    }


}
