package com.quectel.moduletest;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import com.techfuture.tfsqliteext.DatabaseManager;

import static android.widget.Toast.LENGTH_SHORT;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatabaseTestkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatabaseTestkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatabaseTestkFragment extends Fragment {
    public static final String TAG = "DatabaseTestkFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Button btQuery;
    Context mContext;
    DatabaseManager mDatabaseManager;
    String mDbPath;
    String mImei;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            btQuery.setEnabled(true);
            btQuery.setText("查询");
            Log.d(TAG, "msg.what = " + msg.what);
            switch (msg.what){
                case 0x01:
                    showDialog(true);
                    break;
                case 0x02:
                    showDialog(false);
                    break;
                default:
                    break;
            }

        }
    };

    public DatabaseTestkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatabaseTestkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DatabaseTestkFragment newInstance(String param1, String param2) {
        DatabaseTestkFragment fragment = new DatabaseTestkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        //mContext = this.getActivity().getBaseContext();
        mContext = getActivity();

        //Copy file to sdcard
        try{
            Toast.makeText(mContext,"Copy imei database", LENGTH_SHORT).show();
            String value = "imei";
            int key = getResources().getIdentifier(value,"raw",mContext.getPackageName());
            Log.d(TAG,"value = " + value + ", key = " + key);

            mDbPath = mContext.getFilesDir() + "/" + value + ".db";
            Log.d(TAG, "mDbPath = " + mDbPath);
            copyResToSD(key, mDbPath);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_test, container, false);
        btQuery = (Button) view.findViewById(R.id.query);

        btQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btQuery.setEnabled(false);
                btQuery.setText("正在查询数据库...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            TelephonyManager mTm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
                            Method getImei = TelephonyManager.class.getMethod("getImei");
                            mImei = (String)getImei.invoke(mTm);
                            Log.d(TAG, "mImei = " + mImei);
                        } catch ( Exception e ) {
                            e.printStackTrace();
                        }

                        boolean isImeiInDatabase = mDatabaseManager.hitItemFromColumn(mImei, "SN");
                        Log.d(TAG, "mDatabaseManager = " + isImeiInDatabase);

                        Message msg = Message.obtain();
                        if (isImeiInDatabase){
                            msg.what = 0x01;
                        } else {
                            msg.what = 0x02;
                        }

                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        });

        mDatabaseManager = new DatabaseManager(mContext, mDbPath, "imei");
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showDialog(boolean isImeiHit){
        String msg_true = "IMEI: " + mImei + "\n" + "Memory: 属于715A memory";
        String msg_false = "IMEI: " + mImei + "\n" + "Memory: 不属于715A memory";


        if (isImeiHit){
            new AlertDialog.Builder(mContext)
                    .setTitle("INFO")
                    .setMessage(msg_true)
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            new AlertDialog.Builder(mContext)
                    .setTitle("INFO")
                    .setMessage(msg_false)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    private void copyResToSD(int originalFileId, String strOutFileName) throws IOException
    {
        Log.d(TAG,"copy file to : " + strOutFileName);
        try{
            InputStream myInput = getResources().openRawResource(originalFileId);;
            OutputStream myOutput = new FileOutputStream(strOutFileName);
            byte[] buffer = new byte[1024];
            int length = myInput.read(buffer);
            while(length > 0)
            {
                myOutput.write(buffer, 0, length);
                length = myInput.read(buffer);
            }

            myOutput.flush();
            myInput.close();
            myOutput.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
