package com.quectel.camerademo;

import android.app.Fragment;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DualCameraTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DualCameraTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DualCameraTestFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public static final String TAG = "CameraDemo.DCTF";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;


    Camera cameraFront = null, cameraBack = null;

    private int cameraIdFront,cameraIdBack;
    private Button btTakeFrontPic, btTakeBackPic;
    private Switch btRecordFrontMain, btRecordFrontSub;
    private Switch btRecordBackMain, btRecordBackSub;
    private TextView tvShowCameraInfoFront, tvShowCameraInfoBack;
    private SurfaceView svShowPreviewFront,svShowPreviewBack;
    private SurfaceHolder surfaceHolderFront, surfaceHolderBack;

    FileUtils fileUtils = new FileUtils();
    CameraSettings frontCameraInfo, backCameraInfo;
    CameraManagerGlobal mCameraManagerGlobal;
    PictureTaker mPictureTaker;
    MainStreamRecorder mFrontMainStreamRecorder = null, mBackMainStreamRecorder = null;
    PreviewStreamRecorder mFrontPreviewStreamRecorder = null, mBackPreviewStreamRecorder = null;
    Context mContext;


    public DualCameraTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DualCameraTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DualCameraTestFragment newInstance(String param1, String param2) {
        DualCameraTestFragment fragment = new DualCameraTestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this.getActivity().getBaseContext();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dual_camera_test, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        //front camera
        btTakeFrontPic =(Button)view.findViewById(R.id.snap_front);
        btRecordFrontMain = (Switch) view.findViewById(R.id.front_record);
        btRecordFrontSub = (Switch)view.findViewById(R.id.front_preview_record);
        tvShowCameraInfoFront = (TextView) view.findViewById(R.id.camera_info_front);

        svShowPreviewFront =(SurfaceView)view.findViewById(R.id.svFront);
        surfaceHolderFront = svShowPreviewFront.getHolder();
        surfaceHolderFront.addCallback(surfaceCallBackFront);

        btTakeFrontPic.setOnClickListener(this);
        btRecordFrontMain.setOnCheckedChangeListener(this);
        btRecordFrontSub.setOnCheckedChangeListener(this);


        //back cmera
        btTakeBackPic =(Button)view.findViewById(R.id.snap_back);
        btRecordBackMain = (Switch)view.findViewById(R.id.back_record);
        btRecordBackSub = (Switch)view.findViewById(R.id.back_preview_record);
        tvShowCameraInfoBack = (TextView) view.findViewById(R.id.camera_info_back);

        svShowPreviewBack =(SurfaceView)view.findViewById(R.id.svBack);
        surfaceHolderBack = svShowPreviewBack.getHolder();
        surfaceHolderBack.addCallback(surfaceCallBackBack);

        btTakeBackPic.setOnClickListener(this);
        btRecordBackMain.setOnCheckedChangeListener(this);
        btRecordBackSub.setOnCheckedChangeListener(this);



        mCameraManagerGlobal = new CameraManagerGlobal(mContext);
        mPictureTaker = new PictureTaker(mContext);
        Log.d(TAG, "cameraIdFront = " + cameraIdFront + ", cameraFront = " + cameraFront);


        //mFrontPreviewStreamRecorder = new PreviewStreamRecorder();
        //mBackPreviewStreamRecorder = new PreviewStreamRecorder();
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
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
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

    public void onClick(View v)
    {
        switch (v.getId()){
            case R.id.snap_front:
                mPictureTaker.takePic(cameraFront, cameraIdFront);
                break;
            case R.id.snap_back:
                mPictureTaker.takePic(cameraBack, cameraIdBack);
                break;
            default:
                break;
        }
    }

    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked)
    {
        switch (buttonView.getId()){
            case R.id.front_record:
                Log.d(TAG, "record front main stream");

                if (isChecked) {
                    Log.d(TAG, "check");
                    if (mFrontMainStreamRecorder != null)
                    {
                        if (cameraFront != null)
                        {
                            Log.d(TAG, "start main recorder prepare");
                            MediaRecorder mMediaRecorder;
                            mMediaRecorder = mFrontMainStreamRecorder.creatVideoRecorder();
                            mMediaRecorder.setPreviewDisplay(svShowPreviewFront.getHolder().getSurface());
                            mFrontMainStreamRecorder.prepareVideoRecorder(mMediaRecorder);
                            mFrontMainStreamRecorder.startFrontRecording();
                        }
                    }
                } else {
                    Log.d(TAG, "uncheck");
                    mFrontMainStreamRecorder.stopFrontRecording();
                }

                break;

            case R.id.back_record:

                Log.d(TAG, "record back main stream");

                if (isChecked) {
                    Log.d(TAG, "check");
                    if (mBackMainStreamRecorder != null)
                    {
                        if (cameraBack != null)
                        {
                            Log.d(TAG, "start back main recorder prepare");
                            MediaRecorder mMediaRecorder;
                            mMediaRecorder = mBackMainStreamRecorder.creatVideoRecorder();
                            mMediaRecorder.setPreviewDisplay(svShowPreviewBack.getHolder().getSurface());
                            mBackMainStreamRecorder.prepareVideoRecorder(mMediaRecorder);
                            mBackMainStreamRecorder.startFrontRecording();
                        }
                    }
                } else {
                    Log.d(TAG, "uncheck");
                    mBackMainStreamRecorder.stopFrontRecording();
                }

                break;

            case R.id.front_preview_record:
                if (isChecked) {
                    Log.d(TAG, "check");
                    mFrontPreviewStreamRecorder = new PreviewStreamRecorder(cameraIdFront);
                    cameraFront.setPreviewCallback(mFrontPreviewStreamRecorder);
                } else {
                    Log.d(TAG, "uncheck");
                    mFrontPreviewStreamRecorder.close();
                }

                break;


            case R.id.back_preview_record:

                if (isChecked) {
                    Log.d(TAG, "check");
                    mBackPreviewStreamRecorder = new PreviewStreamRecorder(cameraIdBack);
                    cameraBack.setPreviewCallback(mBackPreviewStreamRecorder);
                } else {
                    Log.d(TAG, "uncheck");
                    mBackPreviewStreamRecorder.close();
                }

                break;

            default:
                break;
        }
    }

    SurfaceHolder.Callback surfaceCallBackFront = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub

            cameraIdFront = 1; //mCameraManagerGlobal.findFirstfrontFacingCamera();
            Log.d(TAG, "cameraId = " + cameraIdFront);
            cameraFront = Camera.open(cameraIdFront);

            Log.d(TAG, "surfaceCallBackFront --- cameraIdFront = " + cameraIdFront + ", cameraFront = " + cameraFront);

            mFrontMainStreamRecorder = new MainStreamRecorder(mContext, cameraIdFront, cameraFront);

            frontCameraInfo = new CameraSettings(cameraFront);

            tvShowCameraInfoFront.setText("max preview size: (" + frontCameraInfo.MaxPreviewSize.width + ", " + frontCameraInfo.MaxPreviewSize.height + ")\n"
                    + "max picture size: (" + frontCameraInfo.MaxPictureSizes.width + ", " + frontCameraInfo.MaxPictureSizes.height + ")\n"
                    + "max video quality: 720p");

            startFrontCameraPreview(cameraFront);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (null != cameraFront) {
                cameraFront.setPreviewCallback(null);
                cameraFront.stopPreview();
                cameraFront.release();
                cameraFront = null;
            }
        }
    };


    SurfaceHolder.Callback surfaceCallBackBack = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub

            cameraIdBack = 2; //mCameraManagerGlobal.findFirstBackFacingCamera();
            Log.d(TAG, "cameraId = " + cameraIdBack);
            cameraBack = Camera.open(cameraIdBack);

            mBackMainStreamRecorder = new MainStreamRecorder(mContext, cameraIdBack, cameraBack);

            backCameraInfo = new CameraSettings(cameraBack);

            tvShowCameraInfoFront.setText("max preview size: (" + backCameraInfo.MaxPreviewSize.width + ", " + backCameraInfo.MaxPreviewSize.height + ")\n"
                    + "max picture size: (" + backCameraInfo.MaxPictureSizes.width + ", " + backCameraInfo.MaxPictureSizes.height + ")\n"
                    + "max video quality: 720p");

            startBackCameraPreview(cameraBack);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (null != cameraBack) {
                cameraBack.setPreviewCallback(null);
                cameraBack.stopPreview();
                cameraBack.release();
                cameraBack = null;
            }
        }
    };

    private void startFrontCameraPreview(Camera camera)
    {

        try {
			/*mp.setDataSource("/system/1.mkv");
			mp.setDisplay(sv1.getHolder());
			mp.prepare();
			mp.start();  */

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(CameraSettings.IMAGE_WIDTH, CameraSettings.IMAGE_HEIGHT);  //very important
            int[] fpsRange = mCameraManagerGlobal.getMaxPreviewFpsRange(parameters);
            if (fpsRange.length > 0) {
                parameters.setPreviewFpsRange(
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
            }



//            // parameters.setPreviewFormat(ImageFormat.NV21);
//            parameters.set("preview-format", "yuv420sp");
//            camera.setParameters(parameters);

            Log.d(TAG, "svShowPreview.getHolder() = " + svShowPreviewFront.getHolder().getSurface());
            camera.setPreviewDisplay(svShowPreviewFront.getHolder());
            camera.setDisplayOrientation(0);

            camera.startPreview();

            //mCamera.takePicture(null, null, jpegCallback);
        }catch(IllegalArgumentException e) {
            e.printStackTrace();
        }catch(SecurityException e) {
            e.printStackTrace();
        }catch(IllegalStateException e) {
            e.printStackTrace();
        }catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }


    public void startBackCameraPreview(Camera camera)
    {

//        try {
//            Camera.Parameters parameters = camera.getParameters();
//            Log.d(TAG, "back max preview" + "(" + backCameraInfo.MaxPreviewSize.width + ", " + backCameraInfo.MaxPreviewSize.height + ")");
//            parameters.setPreviewSize(1600, 1200);  //very important
//            int[] fpsRange = mCameraManagerGlobal.getMaxPreviewFpsRange(parameters);
//            if (fpsRange.length > 0) {
//                parameters.setPreviewFpsRange(
//                        fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
//                        fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
//            }
//
//            parameters.setPreviewFormat(ImageFormat.YV12);
//            parameters.set("preview-format", "yuv420sp");
//            camera.setParameters(parameters);
//
//            camera.setPreviewDisplay(svShowPreviewBack.getHolder());
//            camera.setDisplayOrientation(90);
//            camera.startPreview();
//
//            //mCamera.takePicture(null, null, jpegCallback);
//        }catch(IllegalArgumentException e) {
//            e.printStackTrace();
//        }catch(SecurityException e) {
//            e.printStackTrace();
//        }catch(IllegalStateException e) {
//            e.printStackTrace();
//        }catch(IOException ie)
//        {
//            ie.printStackTrace();
//        }


        try {
			/*mp.setDataSource("/system/1.mkv");
			mp.setDisplay(sv1.getHolder());
			mp.prepare();
			mp.start();  */

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(CameraSettings.IMAGE_WIDTH, CameraSettings.IMAGE_HEIGHT);  //very important
            int[] fpsRange = mCameraManagerGlobal.getMaxPreviewFpsRange(parameters);
            if (fpsRange.length > 0) {
                parameters.setPreviewFpsRange(
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
            }


            // parameters.setPreviewFormat(ImageFormat.NV21);
//            parameters.set("preview-format", "yuv420sp");
//            camera.setParameters(parameters);

            Log.d(TAG, "svShowPreview.getHolder() = " + svShowPreviewBack.getHolder().getSurface());
            camera.setPreviewDisplay(svShowPreviewBack.getHolder());
            camera.setDisplayOrientation(0);

            camera.startPreview();

            //mCamera.takePicture(null, null, jpegCallback);
        }catch(IllegalArgumentException e) {
            e.printStackTrace();
        }catch(SecurityException e) {
            e.printStackTrace();
        }catch(IllegalStateException e) {
            e.printStackTrace();
        }catch(IOException ie)
        {
            ie.printStackTrace();
        }
    }
}
