package com.quectel.parceltest;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by klein on 17-10-23.
 */

public class MyParcel implements Parcelable {
    public static final String TAG = "MyParcel";
    public String desc;
    public int id;
    public int age;
    public String name;

    public MyParcel(Parcel in) {
        this.desc = in.readString();
        this.id = in.readInt();
        this.age = in.readInt();
        this.name = in.readString();
    }

    public MyParcel(byte[] bytedata) {
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytedata, 0, bytedata.length);
        parcel.setDataPosition(0);

        this.desc = parcel.readString();
        this.id = parcel.readInt();
        this.age = parcel.readInt();
        this.name = parcel.readString();
    }

    public MyParcel(String desc) {
        this.desc = desc;
    }

    public static final Creator<MyParcel> CREATOR = new Creator<MyParcel>() {
        @Override
        public MyParcel createFromParcel(Parcel in) {
            return new MyParcel(in);
        }

        @Override
        public MyParcel[] newArray(int size) {
            return new MyParcel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(desc);
        dest.writeInt(id);
        dest.writeInt(age);
        dest.writeString(name);
    }

    @Override
    public String toString() {
        String str = "desc = " + desc
                + ", id = " + id
                + ", age = " + age
                + ", name = " + name;
        return str;
    }
}