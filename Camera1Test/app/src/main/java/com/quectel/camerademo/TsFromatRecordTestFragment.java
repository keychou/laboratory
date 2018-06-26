package com.quectel.camerademo;

import android.app.Fragment;
import android.content.Context;
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
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by klein on 18-5-17.
 */

public class TsFromatRecordTestFragment  extends Fragment implements View.OnClickListener {


    public static final String TAG = "CameraDemo.SCTF";

    Button btRecordMain;
    TextView tvShowCameraInfo;
    SurfaceView svShowPreview;
    private SurfaceHolder surfaceHolder;

    private MediaRecorder mMainMediaRecorder;

    Camera camera = null;
    int cameraId;

    FileUtils fileUtils = new FileUtils();

    CameraSettings frontCameraInfo;

    private TsFromatRecordTestFragment.OnFragmentInteractionListener mListener;

    public TsFromatRecordTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TsFromatRecordTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TsFromatRecordTestFragment newInstance(String param1, String param2) {
        TsFromatRecordTestFragment fragment = new TsFromatRecordTestFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_single_camera_test, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {

        btRecordMain = (Button)view.findViewById(R.id.record_main);


        tvShowCameraInfo = (TextView) view.findViewById(R.id.camera_info);
        svShowPreview =(SurfaceView)view.findViewById(R.id.surfaceview);
        surfaceHolder = svShowPreview.getHolder();
        surfaceHolder.addCallback(surfaceCallBack);

        btRecordMain.setOnClickListener(this);
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
            case R.id.capture:

                break;
            case R.id.record_main:
                Log.d(TAG, "record main stream");
                if (mMainMediaRecorder == null)
                {
                    if (camera != null)
                    {
                        Log.e(TAG, "start main recorder prepare");
                        startFrontRecording();
                        ((Button) v).setText("停止/前");
                    }
                } else
                {
                    stopFrontRecording();
                    ((Button) v).setText("录像/前");
                }
                break;
            case R.id.record_sub:

                camera.setPreviewCallback(new PreviewStreamRecorder(cameraId));

                break;
            default:
                break;
        }
    }

    private void startFrontCameraPreview(Camera camera)
    {

        try {
			/*mp.setDataSource("/system/1.mkv");
			mp.setDisplay(sv1.getHolder());
			mp.prepare();
			mp.start();  */

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(CameraSettings.IMAGE_WIDTH, CameraSettings.IMAGE_HEIGHT);  //very important
            int[] fpsRange = getMaxPreviewFpsRange(parameters);
            if (fpsRange.length > 0) {
                parameters.setPreviewFpsRange(
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
                        fpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
            }



            // parameters.setPreviewFormat(ImageFormat.NV21);
            //parameters.set("preview-format", "yuv420sp");
            //camera.setParameters(parameters);

            Log.d(TAG, "svShowPreview.getHolder() = " + svShowPreview.getHolder().getSurface());
            camera.setPreviewDisplay(svShowPreview.getHolder());
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


    public boolean startFrontRecording() {
        if (prepareFrontVideoRecorder()) {
            Log.e(TAG, "media recorder start");
            mMainMediaRecorder.start();
            return true;
        } else {
            Log.e(TAG, "media recorder release");
            releaseFrontMediaRecorder();
        }
        return false;
    }

    public void stopFrontRecording() {
        if (mMainMediaRecorder != null) {
            mMainMediaRecorder.stop();
        }
        releaseFrontMediaRecorder();
    }

    private boolean prepareFrontVideoRecorder() {

        camera.unlock();
        mMainMediaRecorder = new MediaRecorder();
        mMainMediaRecorder.setCamera(camera);
        mMainMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMainMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMainMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //mMainMediaRecorder.setOutputFormat(/*MediaRecorder.OutputFormat.MPEG_4*/8);
        mMainMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMainMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMainMediaRecorder.setVideoSize(1280, 720);
        mMainMediaRecorder.setVideoEncodingBitRate(16 * 1024 * 1024);
        mMainMediaRecorder.setAudioEncodingBitRate(44100);
        mMainMediaRecorder.setVideoFrameRate(30);
        mMainMediaRecorder.setMaxDuration(-1);
        mMainMediaRecorder.setOutputFile("/sdcard/DCIM/002.ts");
        mMainMediaRecorder.setPreviewDisplay(svShowPreview.getHolder().getSurface());


        try {
            mMainMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing fMediaRecorder: " + e.getMessage());
            releaseFrontMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing fMediaRecorder: " + e.getMessage());
            releaseFrontMediaRecorder();
            return false;
        }
        return true;

    }

    private void releaseFrontMediaRecorder() {
        if (mMainMediaRecorder != null) {
            mMainMediaRecorder.reset();
            mMainMediaRecorder.release();
            mMainMediaRecorder = null;
            camera.lock();
        }
    }

    public  int[] getMaxPreviewFpsRange(Camera.Parameters params) {
        List<int[]> frameRates = params.getSupportedPreviewFpsRange();
        if (frameRates != null && frameRates.size() > 0) {
            return frameRates.get(frameRates.size() - 1);
        }
        return new int[0];
    }


    SurfaceHolder.Callback surfaceCallBack = new SurfaceHolder.Callback(){
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub

            cameraId = 0; //findFirstfrontFacingCamera();
            Log.d(TAG, "cameraId = " + cameraId);
            camera = Camera.open(cameraId);

            frontCameraInfo = new CameraSettings(camera);

            tvShowCameraInfo.setText("max preview size: (" + frontCameraInfo.MaxPreviewSize.width + ", " + frontCameraInfo.MaxPreviewSize.height + ")\n"
                    + "max picture size: (" + frontCameraInfo.MaxPictureSizes.width + ", " + frontCameraInfo.MaxPictureSizes.height + ")\n"
                    + "max video quality: 720p");

            startFrontCameraPreview(camera);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // TODO Auto-generated method stub
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            if (null != camera) {
                camera.setPreviewCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    };
}