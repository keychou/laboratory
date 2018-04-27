package com.example.zhoukai.filemanagertest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhoukai on 17-1-13.
 */

public class FilesListAdapter  extends BaseAdapter {
    // 填充数据的list
    private ArrayList<Object> list;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> fileCheckStatus;
    // 上下文
    private Context context;
    // 用来导入布局
    private LayoutInflater inflater = null;

    // 构造器
    public FilesListAdapter(ArrayList<Object> list, Context context) {
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
        fileCheckStatus = new HashMap<Integer, Boolean>();
        // 初始化数据
        for (int i = 0; i < list.size(); i++) {
            fileCheckStatus.put(i, false);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            // 获得ViewHolder对象
            holder = new ViewHolder();
            // 导入布局并赋值给convertview
            convertView = inflater.inflate(R.layout.files_list_item, null);
            holder.tv = (TextView) convertView.findViewById(R.id.file_name);
            holder.cb = (CheckBox) convertView.findViewById(R.id.check_box);
            // 为view设置标签
            convertView.setTag(holder);
        } else {
            // 取出holder
            holder = (ViewHolder) convertView.getTag();
        }
        // 设置list中TextView的显示
        holder.tv.setText(((File)getItem(position)).getName());
        // 根据isSelected来设置checkbox的选中状况
        holder.cb.setChecked(fileCheckStatus.get(position));
        return convertView;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return fileCheckStatus;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        FilesListAdapter.fileCheckStatus = isSelected;
    }

    public Boolean getCheckStatus(Integer position){

        return fileCheckStatus.get(position);
    }

    public static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
}
