package com.quectel.camerademo;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhoukai on 17-5-12.
 */

public class FileUtils {

    public static final String TAG = "CameraDemo.FU";


    public enum  MediaType{
        MEDIA_TYPE_IMAGE,
        MEDIA_TYPE_IMAGE_FRONT,
        MEDIA_TYPE_IMAGE_BACK,
        MEDIA_TYPE_VIDEO_FRONT,
        MEDIA_TYPE_VIDEO_FRONT_PREVIEW_H264,
        MEDIA_TYPE_VIDEO_FRONT_PREVIEW_MP4,
        MEDIA_TYPE_VIDEO_BACK,
        MEDIA_TYPE_VIDEO_BACK_PREVIEW_H264,
        MEDIA_TYPE_VIDEO_BACK_PREVIEW_MP4
    }


//    public static final int MEDIA_TYPE_IMAGE = 1;
//    public static final int MEDIA_TYPE_IMAGE_FRONT = 2;
//    public static final int MEDIA_TYPE_IMAGE_BACK = 3;
//    public static final int MEDIA_TYPE_VIDEO_FRONT = 2;
//    public static final int MEDIA_TYPE_VIDEO_FRONT_PREVIEW_H264 = 3;
//    public static final int MEDIA_TYPE_VIDEO_FRONT_PREVIEW_MP4 = 4;
//    public static final int MEDIA_TYPE_VIDEO_BACK = 5;
//    public static final int MEDIA_TYPE_VIDEO_BACK_PREVIEW = 6;
//
//
//



    public static String T_FLASH_PATH = "/storage/sdcard1";

    private Uri outputMediaFileUri;
    private String outputMediaFileType;


    public File getOutputMediaFile(MediaType type) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), TAG);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;

        switch (type) {
            case MEDIA_TYPE_IMAGE:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_" + timeStamp + ".jpg");
                outputMediaFileType = "image/*";
                break;
            case MEDIA_TYPE_IMAGE_FRONT:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_FRONT_" + timeStamp + ".jpg");
                outputMediaFileType = "image/*";
                break;
            case MEDIA_TYPE_IMAGE_BACK:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG_BACK_" + timeStamp + ".jpg");
                outputMediaFileType = "image/*";
                break;
            case MEDIA_TYPE_VIDEO_FRONT:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_Front_" + timeStamp + ".mp4");
                outputMediaFileType = "video/*";
                break;
            case MEDIA_TYPE_VIDEO_FRONT_PREVIEW_H264:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_Front_Preview_" + timeStamp + ".h264");
                outputMediaFileType = "video/*";
                break;
            case MEDIA_TYPE_VIDEO_FRONT_PREVIEW_MP4:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_Front_Preview_" + timeStamp + ".mp4");
                outputMediaFileType = "video/*";
                break;
            case MEDIA_TYPE_VIDEO_BACK:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_Back_" + timeStamp + ".mp4");
                outputMediaFileType = "video/*";
                break;
            case MEDIA_TYPE_VIDEO_BACK_PREVIEW_MP4:
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                        "VID_Back_Preview_" + timeStamp + ".mp4");
                outputMediaFileType = "video/*";
                break;
            default:
                Log.d(TAG, "no file type found");
                mediaFile = null;
                break;
        }

        outputMediaFileUri = Uri.fromFile(mediaFile);
        Log.e(TAG, " media getFile path = " + mediaFile.getPath());
        return mediaFile;
    }

    public static boolean isTFlashCardExists() {
        boolean tfExistsFlag = false;
        tfExistsFlag = new File(T_FLASH_PATH, "Android").exists();

        if (getStorageDirWhenInsertSdcard() != null && testNewTfFile() == true) {
            tfExistsFlag = true;
        }
        return tfExistsFlag;
    }

    public static File getStorageDirWhenInsertSdcard() {
        File dir;
        try {
            dir = new File(T_FLASH_PATH, getMainDirName());
        } catch (Exception e) {
            return null;
        }

        if (!dir.exists()) {
            dir.mkdirs();
        }
//        log.debug("dir:{}, free:{}", dir.getAbsolutePath(), dir.getFreeSpace());

        return dir;
    }

    public static boolean testNewTfFile() {
        File testFile = new File(T_FLASH_PATH, "testNewFile");
        boolean returnFlag = false;
        if (!testFile.exists()) {
            try {
                if (testFile.createNewFile()) {
                    returnFlag = true;
                    testFile.delete();
                }
            } catch (IOException e) {
                returnFlag = false;
            }
        } else {
            testFile.delete();
            returnFlag = true;
        }
        return returnFlag;
    }

    public static String getMainDirName() {
        return "/dudu";
    }

    public static String getExternalStorageDirectory() {
        String path = Environment.getExternalStorageDirectory().getPath();
        return path;
    }

    public Uri getOutputMediaFileUri() {
        return outputMediaFileUri;
    }

    public String getOutputMediaFileType() {
        return outputMediaFileType;
    }
}
