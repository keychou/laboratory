package com.quectel.moduletest;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quectel.tf_storage_ext.DiskStat;
import com.quectel.tf_storage_ext.ExternalSdcardManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StorageTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StorageTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StorageTestFragment extends Fragment {
    public static final String TAG = "StorageTestFragment";
    ExternalSdcardManager mExternalSdcardManager;
    Context mContext;


    private OnFragmentInteractionListener mListener;

    public StorageTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StorageTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StorageTestFragment newInstance(String param1, String param2) {
        StorageTestFragment fragment = new StorageTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        mContext = getContext();
        mExternalSdcardManager = new ExternalSdcardManager(mContext);

        String path = mExternalSdcardManager.getExternalStoragePath(mContext, true);
        Log.d(TAG, "path = " + path);
        DiskStat diskStat = mExternalSdcardManager.getDiskCapacity(path);
        Log.d(TAG, "diskStat = " + diskStat);
        Log.d(TAG, "free = " + diskStat.getFreeCapacityByHuman());

        mExternalSdcardManager.getFileName(mExternalSdcardManager.getExternalFileDir(mContext));

        mExternalSdcardManager.deleteFilesProceed(mExternalSdcardManager.getExternalFileDir(mContext), 1);

        mExternalSdcardManager.getFileName(mExternalSdcardManager.getExternalFileDir(mContext));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_storage_test, container, false);
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
}
