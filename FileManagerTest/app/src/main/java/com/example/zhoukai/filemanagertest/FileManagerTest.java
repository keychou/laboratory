package com.example.zhoukai.filemanagertest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManagerTest extends AppCompatActivity {

    public static final String TAG = "FileManagerTest";
    Button btBack, btDelete;
    ListView lvFileList;
    File currentParent;
    File[] currentFiles;
    private int checkNum; // 记录选中的条目数量
    FilesListAdapter filesListAdapter;
    TextView tvConut;
    private ArrayList<String> mMbnSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_manager_test);

        btBack = (Button) findViewById(R.id.back);
        btDelete = (Button) findViewById(R.id.delete);
        lvFileList = (ListView) findViewById(R.id.listView);
        tvConut = (TextView) findViewById(R.id.item_count);

        mMbnSelected = new ArrayList<String>();

        mMbnSelected.add("test");

        File root = Environment.getExternalStorageDirectory();

        if (!root.exists()) {
            Toast.makeText(this, "please check if the sdcard exist !", Toast.LENGTH_LONG).show();
        }

        if (!root.canRead()){
            Toast.makeText(this, "please approve the read perssion of sdcard !", Toast.LENGTH_LONG).show();
            return;
        }

        currentParent = root;
        currentFiles = root.listFiles();
        inflateListView(currentFiles);

        lvFileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentFiles[position].isFile()) {
                    // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤
                    FilesListAdapter.ViewHolder holder = (FilesListAdapter.ViewHolder) view.getTag();
                    // 改变CheckBox的状态
                    holder.cb.toggle();
                    // 将CheckBox的选中状况记录下来
                    FilesListAdapter.getIsSelected().put(position, holder.cb.isChecked());
                    // 调整选定条目
                    if (holder.cb.isChecked() == true) {
                        checkNum++;
                    } else {
                        checkNum--;
                    }
                    // 用TextView显示
                    tvConut.setText("已选中" + checkNum + "项");
                }else{
                    File temp[] = currentFiles[position].listFiles();

                    if (temp == null || temp.length == 0) {
                        Toast.makeText(FileManagerTest.this, "当前目录下没有文件", Toast.LENGTH_SHORT).show();
                    } else {
                        currentParent = currentFiles[position];
                        currentFiles = temp;
                        inflateListView(currentFiles);
                    }
                }


            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!currentParent.getCanonicalPath().equals("/sdcard/")){
                        currentParent = currentParent.getParentFile();
                        currentFiles = currentParent.listFiles();
                        inflateListView(currentFiles);
                    }
                }catch (IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<Integer, Boolean> file_Seclect = FilesListAdapter.getIsSelected();

                for (int i = 0; i < currentFiles.length; i++) {
                    if (file_Seclect.get(i)){
                        Log.d(TAG, "will delete currentFiles[" + i + "].getName() = " + currentFiles[i].getName());
                        currentFiles[i].delete();
                    }
                }

            }
        });


    }


    private void inflateListView (File[] files){
        ArrayList<Object> listItem = new ArrayList<>();

        for (int i = 0; i < files.length; i++){

            listItem.add(files[i]);
        }

        filesListAdapter = new FilesListAdapter(listItem, this);
        lvFileList.setAdapter(filesListAdapter);
    }
}
