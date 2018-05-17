package com.quectel.camerademo;

import android.content.Context;
import android.hardware.Camera;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by klein on 17-6-27.
 */

public class PictureTaker {


    private Context mContext;
    private int mCameraId;
    private Camera mCamera;
    FileUtils fileUtils = new FileUtils();

    PictureTaker(Context context){
        mContext = context;
    }

    public void takePic(Camera camera, int cameraId) {

        //camera.stopPreview();// stop the preview
        mCameraId = cameraId;
        mCamera = camera;
        mCamera.takePicture(null, null, pictureCallback); // picture
    }

    // Photo call back
    Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        //@Override
        public void onPictureTaken(byte[] data, Camera camera) {
            new PictureTaker.SavePictureTask().execute(data);
            camera.startPreview();
        }
    };

    class SavePictureTask extends AsyncTask<byte[], String, String> {
        @Override
        protected String doInBackground(byte[]... params) {

            File picture;

            if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
                picture = fileUtils.getOutputMediaFile(FileUtils.MediaType.MEDIA_TYPE_IMAGE_FRONT);
            }else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
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
}
