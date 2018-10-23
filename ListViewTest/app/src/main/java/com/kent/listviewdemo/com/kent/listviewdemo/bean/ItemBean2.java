package com.kent.listviewdemo.com.kent.listviewdemo.bean;

/**
 * Created by Kent on 2014/12/15.
 */
public class ItemBean2 extends BaseItem{

    private String name = null;
    private String address = null;

    public ItemBean2(int item_Type, String name, String address) {
        super(item_Type);
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getItemType(){
        return super.getItem_type();
    }

    public void setItemType(int itemType){
        super.setItem_type(itemType);
    }
}
