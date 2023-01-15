package com.example.preferencetest;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

public class DynamicPreference extends Preference {
    public static final String TAG = "DynamicPreference";

    public static int mCount = 0;

    public DynamicPreference(Context context){
        super(context, null);
        mCount = mCount + 1;
        setTitle(String.valueOf(mCount));
        //setSummary("22");
    }

    @Override
    public void onAttached() {
        super.onAttached();
        Log.d(TAG, "klein--onAttached");
    }

    @Override
    protected void onClick() {
        super.onClick();
        Log.d(TAG, "klein--onClick");
    }

    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        Log.d(TAG, "klein--onBindViewHolder");

    }
}
