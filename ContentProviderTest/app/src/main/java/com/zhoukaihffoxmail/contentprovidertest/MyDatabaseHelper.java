/**
 * 
 */
package com.zhoukaihffoxmail.contentprovidertest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**

 */
public class MyDatabaseHelper extends SQLiteOpenHelper
{
	final String CREATE_TABLE_SQL =
		"CREATE TABLE IF NOT EXISTS contacts(_id integer primary key autoincrement , name, age , number)";

	public MyDatabaseHelper(Context context, String name, int version)
	{
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		System.out.println("--------onUpdate Called--------" 
			+ oldVersion + "--->" + newVersion);
	}
}
