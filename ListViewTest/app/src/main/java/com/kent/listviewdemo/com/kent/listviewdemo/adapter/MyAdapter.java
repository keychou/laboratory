package com.kent.listviewdemo.com.kent.listviewdemo.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kent.listviewdemo.R;
import com.kent.listviewdemo.com.kent.listviewdemo.adapter.com.kent.listviewdemo.adapter.holder.ItemType;
import com.kent.listviewdemo.com.kent.listviewdemo.adapter.com.kent.listviewdemo.adapter.holder.ViewHolder1;
import com.kent.listviewdemo.com.kent.listviewdemo.adapter.com.kent.listviewdemo.adapter.holder.ViewHolder2;
import com.kent.listviewdemo.com.kent.listviewdemo.bean.BaseItem;
import com.kent.listviewdemo.com.kent.listviewdemo.bean.ItemBean1;
import com.kent.listviewdemo.com.kent.listviewdemo.bean.ItemBean2;

import java.util.List;

/**
 * Created by Kent on 2014/12/12.
 */
public class MyAdapter extends BaseAdapter {

    private Context mContext = null;//上下文
    private LayoutInflater mInflater = null;

    private List<BaseItem> mData = null;//要显示的数据

    public MyAdapter(Context context, List<BaseItem> data){
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    //添加一个新的Item，并通知listview进行显示刷新
    public void addItem(BaseItem newItem){
        this.mData.add(newItem);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        return mData.get(position).getItem_type();
    }

    @Override
    public int getViewTypeCount() {
        return ItemType.ITEM_TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        if(mData == null){
            return 0;
        }
        return this.mData.size();
    }

    @Override
    public Object getItem(int i) {

        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View viewItem1 = null;
        View viewItem2 = null;

        int itemType = this.getItemViewType(position);
        if(itemType == ViewHolder1.ITEM_VIEW_TYPE_1){
            //第一种item
            ViewHolder1 viewHolder1 = null;
            if(convertView == null){
                //没有缓存过
                viewHolder1 = new ViewHolder1();
                viewItem1 = this.mInflater.inflate(R.layout.list_view_item_1, null, false);
                viewHolder1.textView = (TextView)viewItem1.findViewById(R.id.
                        main_activity_list_view_item_1_textview);
                viewHolder1.imageView = (ImageView)viewItem1.findViewById(R.id.
                        main_activity_list_view_item_1_imageview);
                viewItem1.setTag(viewHolder1);
                convertView = viewItem1;
            }else{
                viewHolder1 = (ViewHolder1)convertView.getTag();
            }
            viewHolder1.textView.setText(((ItemBean1) mData.get(position)).getName());
            viewHolder1.imageView.setBackgroundResource(R.drawable.ic_launcher);
        }else if(itemType == ViewHolder2.ITEM_VIEW_TYPE_2){
            //第二种item
            ViewHolder2 viewHolder2 = null;
            if(convertView == null){
                //没有缓存过
                viewHolder2 = new ViewHolder2();
                viewItem2 = this.mInflater.inflate(R.layout.list_view_item_2, null, false);
                viewHolder2.textView1 = (TextView)viewItem2.findViewById(R.id.
                        main_activity_list_view_item_2_textview);
                viewHolder2.textView2 = (TextView)viewItem2.findViewById(R.id.
                        main_activity_list_view_item_2_textview_2);
                viewItem2.setTag(viewHolder2);
                convertView = viewItem2;
            }else{
                viewHolder2 = (ViewHolder2)convertView.getTag();
            }
            viewHolder2.textView1.setText(((ItemBean2)mData.get(position)).getName());
            viewHolder2.textView2.setText(((ItemBean2)mData.get(position)).getAddress());
        }

        return convertView;
    }
}
