package com.zhoukaihffoxmail.contentprovidertest;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ContentProviderTest extends AppCompatActivity {

    public static final String TAG = "ContentProviderTest";

    MyDatabaseHelper dbHelper;

    Button insert = null;
    Button search = null;
    EditText etName;
    EditText etAge;
    EditText etNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider_test);



        dbHelper = new MyDatabaseHelper(this, "linkman.db" , 1);

        insert = (Button)findViewById(R.id.insert);
        search = (Button)findViewById(R.id.search);

        etName= (EditText)findViewById(R.id.name);
        etAge = (EditText)findViewById(R.id.age);
        etNumber = (EditText)findViewById(R.id.number);



        insert.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View source)
            {
                //获取用户输入

                String name =  etName.getText().toString();
                String age =  etAge.getText().toString();
                String number = etNumber.getText().toString();
                //插入生词记录
                insertData(dbHelper.getWritableDatabase() ,name, age, number);
            }
        });

    }

    private void insertData(SQLiteDatabase db , String name , String age, String number)
    {
        //执行插入语句
        db.execSQL("insert into contacts (name, age, number) values(? , ?, ?)", new String[]{name, age, number});
    }
}
