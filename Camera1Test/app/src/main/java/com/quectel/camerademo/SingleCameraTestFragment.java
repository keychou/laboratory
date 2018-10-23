package com.quectel.camerademo;

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
import android.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SingleCameraTestFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SingleCameraTestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SingleCameraTestFragment extends Fragment implements View.OnClickListener {


    public static final String TAG = "CameraDemo.SCTF";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    Button btRecordMain, btRecordSub, btTakePic;
    TextView tvShowCameraInfo;
    SurfaceView svShowPreview;
    private SurfaceHolder surfaceHolder;

    private MediaRecorder mMainMediaRecorder;

    Camera camera = null;
    int cameraId;

    FileUtils fileUtils = new FileUtils();

    CameraSettings frontCameraInfo;

    private OnFragmentInteractionListener mListener;

    public SingleCameraTestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SingleCameraTestFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SingleCameraTestFragment newInstance(String param1, String param2) {
        SingleCameraTestFragment fragment = new SingleCameraTestFragment();
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
        btTakePic =(Button)view.findViewById(R.id.capture);

        tvShowCameraInfo = (TextView) view.findViewById(R.id.camera_info);
        svShowPreview =(SurfaceView)view.findViewById(R.id.surfaceview);
        surfaceHolder = svShowPreview.getHolder();
        surfaceHolder.addCallback(surfaceCallBack);

        btTakePic.setOnClickListener(this);
        btRecordMain.setOnClickListener(this);
        btRecordSub.setOnClickListener(this);

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

                takePic(camera);

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
            parameters.set("preview-format", "yuv420sp");
            camera.setParameters(parameters);

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
        mMainMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        mMainMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMainMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMainMediaRecorder.setVideoSize(1280, 720);
        mMainMediaRecorder.setVideoEncodingBitRate(16 * 1024 * 1024);
        mMainMediaRecorder.setVideoFrameRate(30);
        mMainMediaRecorder.setOutputFile(fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_VIDEO_FRONT).toString());
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

    /*find front camera*/
    private int findFirstfrontFacingCamera() {
        int numCams = Camera.getNumberOfCameras();
        for (int camId = 0; camId < numCams; camId++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(camId, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = camId;
                break;
            }
        }
        return cameraId;
    }

    private void takePic(Camera camera) {

        //camera.stopPreview();// stop the preview

        camera.takePicture(null, null, pictureCallback); // picture
    }

    // Photo call back
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        //@Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new SavePictureTask().execute(data);
            camera.startPreview();
        }
    };

    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {

            File picture;

            if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
                picture = fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_IMAGE_FRONT);
            }else if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
                picture = fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_IMAGE_BACK);
            }else{
                picture = fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_IMAGE);
            }


            try {
                FileOutputStream fos = new FileOutputStream(picture.getPath()); // Get file output stream
                fos.write(params[0]); // Written to the file
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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

            cameraId = 1; //findFirstfrontFacingCamera();
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
