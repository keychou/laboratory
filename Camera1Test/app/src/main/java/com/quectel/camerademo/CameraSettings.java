package com.quectel.camerademo;

import android.hardware.Camera;
import android.util.Log;

import java.util.List;

/**
 * Created by zhoukai on 17-5-12.
 */

public class CameraSettings {

    public static final String TAG = "MultiCamera:CameraInfo";

    static public int IMAGE_WIDTH = 1280;
    static public int IMAGE_HEIGHT = 720;

    Camera targetCamera;

    PreviewSize MaxPreviewSize = new PreviewSize();

    PictureSize MaxPictureSizes = new PictureSize();

    public class PreviewSize{
        int width;
        int height;
    }

    public class PictureSize{
        int width;
        int height;
    }

    CameraSettings(Camera camera){
        targetCamera = camera;

        Camera.Parameters parameters = targetCamera.getParameters();

        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for(int i=0;i<previewSizes.size();i++){
            Log.d(TAG, i + ": " + "(" + String.valueOf(previewSizes.get(i).width) + ", " + String.valueOf(previewSizes.get(i).height) + ")");
        }
        MaxPreviewSize.width = previewSizes.get(0).width;
        MaxPreviewSize.height = previewSizes.get(0).height;


        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        for(int i=0;i<supportedPictureSizes.size();i++){

            Log.d(TAG, i + ":PictureSizes " + "(" + String.valueOf(supportedPictureSizes.get(i).width) + ", " + String.valueOf(supportedPictureSizes.get(i).height) + ")");
        }
        MaxPictureSizes.width = supportedPictureSizes.get(0).width;
        MaxPictureSizes.height = supportedPictureSizes.get(0).height;
    }

    String dumpMaxPreviewSize(){
        return ("(" + MaxPreviewSize.width + ", " + MaxPreviewSize.height + ")");
    }

    String dumpMaxPictureSize(){
        return ("(" + MaxPictureSizes.width + ", " + MaxPictureSizes.height + ")");
    }

}
