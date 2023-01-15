package com.example.preferencetest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Bundle;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;

import java.util.prefs.PreferenceChangeEvent;

public class SettingsPreference extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = "SettingsPreference";
    private EditTextPreference mEtPreference;
    private ListPreference mListPreference;
    private CheckBoxPreference mCheckPreference;
    private SharedPreferences mSharedPrefs;

    public static final String EDIT_KEY = "edittext_key";
    public static final String LIST_KEY = "list_key";
    public static final String CHECKBOX_KEY = "checkbox_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        mEtPreference = (EditTextPreference)findPreference(EDIT_KEY);
        mListPreference = (ListPreference)findPreference(LIST_KEY);
        mCheckPreference = (CheckBoxPreference)findPreference(CHECKBOX_KEY);

        mEtPreference.setOnPreferenceClickListener(this);


        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mEtPreference.setOnPreferenceChangeListener(this);
        mListPreference.setOnPreferenceChangeListener(this);
        mCheckPreference.setOnPreferenceChangeListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(1000);
                } catch(Exception e){
                    e.printStackTrace();
                }

                //EditTextPreference statusCat = (EditTextPreference)findPreference(EDIT_KEY);
                // getPreferenceScreen().addPreference(mEtPreference);
                //getPreferenceScreen().addPreference(mListPreference);

                PreferenceCategory mPreferenceCategory = (PreferenceCategory) findPreference("firstcatagory");
                mPreferenceCategory.addPreference(mEtPreference);
                mEtPreference.setText("22");
                mEtPreference.setSummary("33");
                try{
                    Thread.sleep(1000);
                } catch(Exception e){
                    e.printStackTrace();
                }

                mPreferenceCategory.addPreference(mCheckPreference);

            }
        }).start();
    }


    public boolean onPreferenceChange(Preference preference, Object objValue) {

        System.out.println("klein----------onPreferenceChange-------");
        if (preference == mEtPreference) {
            System.out.println("klein--------------mEtPreference change--objValue = " + objValue);
        } else if (preference == mListPreference) {
            System.out.println("klein--------------mListPreference change---objValue = " + objValue);
        } else if (preference == mCheckPreference) {
            System.out.println("klein--------------mCheckPreference change--objValue = " + objValue);
        }
        return true;
    }

    public boolean onPreferenceClick(Preference preference) {
        Log.d(TAG, "klein----onPreferenceTreeClick");
        Toast.makeText(this, "please turn on ethernet", Toast.LENGTH_LONG).show();
        return false;
    }

    protected void onResume() {
        super.onResume();
        System.out.println("klein--------------onResume");
        // Setup the initial values
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        mListPreference.setSummary(sharedPreferences.getString(LIST_KEY, ""));
        mEtPreference.setSummary(sharedPreferences.getString(EDIT_KEY, "linc"));

        // Set up a listener whenever a key changes
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        System.out.println("klein--------------onSharedPreferenceChanged---key = " + key);
        if (key.equals(EDIT_KEY)) {
            mEtPreference.setSummary(
                    sharedPreferences.getString(key, "20"));
        } else if(key.equals(LIST_KEY)) {
            mListPreference.setSummary(sharedPreferences.getString(key, ""));
        }
    }
}
